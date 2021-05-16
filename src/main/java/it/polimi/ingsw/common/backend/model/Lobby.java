package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventEmitter;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;

import java.util.*;
import java.util.function.BiConsumer;

public class Lobby extends EventEmitter {
    private final GameFactory gameFactory;
    // private final List<GameContext> games;
    private final Map<View, String> nicknames;
    private final Map<View, GameContext> joined;
    private final Map<String, GameContext> disconnected;
    private final List<View> waiting;
    private int newGamePlayersCount;

    public Lobby(GameFactory gameFactory) {
        super(Set.of(
                /* Lobby and GameContext */
                ErrAction.class,
                /* Lobby (only) */
                UpdateBookedSeats.class, UpdateJoinGame.class, ResGoodbye.class,
                /* GameContext (only) */
                UpdateSetupDone.class,
                /* Game */
                UpdateGameStart.class, UpdateCurrentPlayer.class, UpdateGameResume.class, UpdateLastRound.class, UpdateGameEnd.class,
                /* Player */
                UpdateLeadersHand.class, UpdateFaithPoints.class, UpdateVictoryPoints.class, UpdatePlayerStatus.class, UpdateDevCardSlot.class,
                /* Card */
                UpdateLeader.class,
                /* ResourceContainer */
                UpdateResourceContainer.class,
                /* DevCardGrid */
                UpdateDevCardGrid.class,
                /* Market */
                UpdateMarket.class,
                /* FaithTrack */
                UpdateVaticanSection.class,
                /* SoloGame */
                UpdateActionToken.class));

        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
        this.disconnected = new HashMap<>();
    }

    /* The player joining is one of the first of the match if 'freeSeats' is negative. */
    public void joinLobby(View view, String nickname) {
        if (nicknames.containsKey(view)) {
            emit(new ErrAction("You already have nickname \"" + nicknames.get(view) + "\"."));
            return;
        }
        if (nickname == null || nickname.isBlank()) {
            emit(new ErrAction("Given nickname is empty."));
            return;
        }
        if (nicknames.containsValue(nickname)) {
            emit(new ErrAction("Nickname \"" + nickname + "\" already in use. Choose another one."));
            return;
        }

        /* nickname validated, join lobby */
        nicknames.put(view, nickname);

        if (disconnected.containsKey(nickname)) {
            System.out.println("Player \"" + nickname + "\" rejoined");

            /* Get the match the nickname was previously in and set the player back to active */
            GameContext oldContext = disconnected.get(nickname);
            try {
                oldContext.setActive(nickname, true);
            } catch (NoActivePlayersException e) {
                // emit(new ErrAction(e.getMessage()));
                throw new RuntimeException("No active players after player rejoining.");
            }

            /* Resuming routine (observer registrations and state messages) */
            oldContext.resume();
            joined.put(view, oldContext);
            disconnected.remove(nickname);
        } else {
            System.out.println("Set nickname \"" + nickname + "\".");

            waiting.add(view);

            // Sort of notifyBroadcast
            waiting.forEach(v -> emit(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.get(0)))));

            if (newGamePlayersCount != 0)
                emit(new UpdateJoinGame(newGamePlayersCount));

            if (waiting.size() == newGamePlayersCount)
                startNewGame();
        }
    }

    /* If the player is one of the first to join, it will have to choose the total number of players in the match. */
    public void prepareNewGame(View view, int newGamePlayersCount) {
        if (!checkNickname(view))
            return;

        if (waiting.indexOf(view) != 0) {
            emit(new ErrAction("Cannot prepare a new game. You are not the first player who booked a seat."));
            return;
        }

        if (newGamePlayersCount == 0) {
            emit(new ErrAction("Cannot have zero players in a game, please choose a valid amount."));
            return;
        }

        System.out.printf("Setting players count to %d.%n", newGamePlayersCount);
        this.newGamePlayersCount = newGamePlayersCount;

        // Sort of notifyBroadcast
        waiting.subList(0, Math.min(waiting.size(), newGamePlayersCount)).forEach(v -> emit(new UpdateJoinGame(newGamePlayersCount)));

        if (waiting.size() >= newGamePlayersCount)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = newGamePlayersCount == 1 ?
                gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
                gameFactory.getMultiGame(waiting.subList(0, newGamePlayersCount).stream().map(nicknames::get).toList());

        GameContext context = new GameContext(newGame, gameFactory);
        waiting.subList(0, newGamePlayersCount).forEach(view -> {
            context.register(UpdateGameStart.class, event -> emitFromContext(view, event));
            context.register(UpdateCurrentPlayer.class, event -> emitFromContext(view, event));
            context.register(UpdateGameResume.class, event -> emitFromContext(view, event));
            context.register(UpdateLastRound.class, event -> emitFromContext(view, event));
            context.register(UpdateGameEnd.class, event -> emitFromContext(view, event));
            context.register(UpdateLeadersHand.class, event -> emitFromContext(view, event));
            context.register(UpdateFaithPoints.class, event -> emitFromContext(view, event));
            context.register(UpdateVictoryPoints.class, event -> emitFromContext(view, event));
            context.register(UpdatePlayerStatus.class, event -> emitFromContext(view, event));
            context.register(UpdateDevCardSlot.class, event -> emitFromContext(view, event));
            context.register(UpdateLeader.class, event -> emitFromContext(view, event));
            context.register(UpdateResourceContainer.class, event -> emitFromContext(view, event));
            context.register(UpdateDevCardGrid.class, event -> emitFromContext(view, event));
            context.register(UpdateMarket.class, event -> emitFromContext(view, event));
            context.register(UpdateVaticanSection.class, event -> emitFromContext(view, event));
            context.register(UpdateActionToken.class, event -> emitFromContext(view, event));

            joined.put(view, context);
        });
        context.start();

        /* Remove players who joined from waiting list */
        waiting.subList(0, newGamePlayersCount).clear();

        // Sort of notifyBroadcast
        waiting.forEach(v -> emit(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.get(0)))));

        newGamePlayersCount = 0;
    }

    public void emitFromContext(View view, MVEvent event) {
        // TODO: Implement private events in some way
        emit(event);
    }

    public void exit(View view) {
        String nickname = nicknames.get(view);
        if (nickname != null) {
            GameContext context = joined.get(view);
            if (context != null) {
                context.unregister(UpdateGameStart.class, event -> emitFromContext(view, event));
                context.unregister(UpdateCurrentPlayer.class, event -> emitFromContext(view, event));
                context.unregister(UpdateGameResume.class, event -> emitFromContext(view, event));
                context.unregister(UpdateLastRound.class, event -> emitFromContext(view, event));
                context.unregister(UpdateGameEnd.class, event -> emitFromContext(view, event));
                context.unregister(UpdateLeadersHand.class, event -> emitFromContext(view, event));
                context.unregister(UpdateFaithPoints.class, event -> emitFromContext(view, event));
                context.unregister(UpdateVictoryPoints.class, event -> emitFromContext(view, event));
                context.unregister(UpdatePlayerStatus.class, event -> emitFromContext(view, event));
                context.unregister(UpdateDevCardSlot.class, event -> emitFromContext(view, event));
                context.unregister(UpdateLeader.class, event -> emitFromContext(view, event));
                context.unregister(UpdateResourceContainer.class, event -> emitFromContext(view, event));
                context.unregister(UpdateDevCardGrid.class, event -> emitFromContext(view, event));
                context.unregister(UpdateMarket.class, event -> emitFromContext(view, event));
                context.unregister(UpdateVaticanSection.class, event -> emitFromContext(view, event));
                context.unregister(UpdateActionToken.class, event -> emitFromContext(view, event));

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
        emit(new ResGoodbye());
    }

    public void checkJoinedThen(View view, BiConsumer<GameContext, String> then) {
        if (checkJoined(view))
            then.accept(joined.get(view), nicknames.get(view));
    }

    private boolean checkNickname(View view) {
        if (!nicknames.containsKey(view)) {
            emit(new ErrAction("You must first request a nickname."));
            return false;
        }
        return true;
    }

    private boolean checkJoined(View view) {
        if (!checkNickname(view))
            return false;
        if (!joined.containsKey(view)) {
            emit(new ErrAction("You must first join a game."));
            return false;
        }
        return true;
    }
}
