package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ErrAction;
import it.polimi.ingsw.common.events.mvevents.ResGoodbye;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Lobby {
    private final GameFactory gameFactory;
    // private final List<GameContext> games;
    private final Map<View, String> nicknames;
    private final Map<View, GameContext> joined;
    private final Map<String, GameContext> disconnected;
    private final List<View> waiting;
    private int newGamePlayersCount;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
        this.disconnected = new HashMap<>();
    }

    /* The player joining is one of the first of the match if 'freeSeats' is negative. */
    public void joinLobby(View view, String nickname) {
        if (nicknames.containsKey(view)) {
            view.on(new ErrAction(String.format("You already have nickname \"%s\".", nicknames.get(view))));
            return;
        }
        if (nickname == null || nickname.isBlank()) {
            view.on(new ErrAction("Given nickname is empty."));
            return;
        }
        if (nicknames.containsValue(nickname)) {
            view.on(new ErrAction(String.format("Nickname \"%s\" already in use. Choose another one.", nickname)));
            return;
        }

        /* nickname validated, join lobby */
        nicknames.put(view, nickname);

        if (disconnected.containsKey(nickname)) {
            System.out.printf("Player \"%s\" rejoined%n", nickname);

            /* Get the match the nickname was previously in and set the player back to active */
            GameContext oldContext = disconnected.get(nickname);
            try {
                oldContext.setActive(nickname, true);
            } catch (NoActivePlayersException e) {
                // view.on(new ErrAction(e));
                throw new RuntimeException("No active players after player rejoining.");
            }

            /* Resuming routine (observer registrations and state messages) */
            oldContext.resume(view);
            oldContext.registerViewToModel(view, nickname);
            joined.put(view, oldContext);
            disconnected.remove(nickname);
        } else {
            System.out.printf("Set nickname \"%s\".%n", nickname);

            waiting.add(view);

            waiting.forEach(v -> v.on(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.get(0)))));

            if (newGamePlayersCount != 0)
                view.on(new UpdateJoinGame(newGamePlayersCount));

            if (waiting.size() == newGamePlayersCount)
                startNewGame();
        }
    }

    /* If the player is one of the first to join, it will have to choose the total number of players in the match. */
    public void prepareNewGame(View view, int newGamePlayersCount) {
        if (!checkNickname(view))
            return;

        if (waiting.indexOf(view) != 0) {
            view.on(new ErrAction("Cannot prepare a new game. You are not the first player who booked a seat."));
            return;
        }

        if (newGamePlayersCount == 0) {
            view.on(new ErrAction("Cannot have zero players in a game, please choose a valid amount."));
            return;
        }

        System.out.printf("Setting players count to %d.%n", newGamePlayersCount);
        this.newGamePlayersCount = newGamePlayersCount;

        waiting.subList(0, Math.min(waiting.size(), newGamePlayersCount)).forEach(v -> v.on(new UpdateJoinGame(newGamePlayersCount)));

        if (waiting.size() >= newGamePlayersCount)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = newGamePlayersCount == 1 ?
                gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
                gameFactory.getMultiGame(waiting.subList(0, newGamePlayersCount).stream().map(nicknames::get).toList());

        GameContext context = new GameContext(newGame, gameFactory);
        waiting.subList(0, newGamePlayersCount).forEach(view -> {
            context.registerViewToModel(view, nicknames.get(view));
            joined.put(view, context);
        });
        context.start();

        /* Remove players who joined from waiting list */
        waiting.subList(0, newGamePlayersCount).clear();

        waiting.forEach(v -> v.on(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.get(0)))));

        newGamePlayersCount = 0;
    }

    public void exit(View view) {
        String nickname = nicknames.get(view);
        if (nickname != null) {
            GameContext context = joined.get(view);
            if (context != null) {
                context.unregisterViewToModel(view, nickname);

                try {
                    context.setActive(nickname, false);
                    disconnected.put(nickname, context);
                } catch (NoActivePlayersException e) {
                    disconnected.entrySet().removeIf(entry -> entry.getValue() == context);
                }
                joined.remove(view);
            }
            nicknames.remove(view);
        }
        view.on(new ResGoodbye());
    }

    public void checkJoinedThen(View view, BiConsumer<GameContext, String> then) {
        if (checkJoined(view))
            then.accept(joined.get(view), nicknames.get(view));
    }

    private boolean checkNickname(View view) {
        if (!nicknames.containsKey(view)) {
            view.on(new ErrAction("You must first request a nickname."));
            return false;
        }
        return true;
    }

    private boolean checkJoined(View view) {
        if (!checkNickname(view))
            return false;
        if (!joined.containsKey(view)) {
            view.on(new ErrAction("You must first join a game."));
            return false;
        }
        return true;
    }
}
