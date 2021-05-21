package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ResQuit;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNewGame;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname.ErrNicknameReason;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Lobby {
    private final Object lock;
    private final GameFactory gameFactory;
    public final Map<View, String> nicknames;
    public final Map<View, GameContext> joined;
    public final Map<String, GameContext> disconnected;
    public final List<View> waiting;
    public int newGamePlayersCount;

    public Lobby(GameFactory gameFactory) {
        this.lock = new Object();
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
        this.disconnected = new HashMap<>();
    }

    /* The player joining is one of the first of the match if 'freeSeats' is negative. */
    public void joinLobby(View view, String nickname) {
        synchronized(lock) {
            if (nicknames.containsKey(view)) {
                view.dispatch(new ErrNickname(ErrNicknameReason.ALREADYSET));
                return;
            }
            if (nickname == null || nickname.isBlank()) {
                view.dispatch(new ErrNickname(ErrNicknameReason.NOTSET));
                return;
            }
            if (nicknames.containsValue(nickname)) {
                view.dispatch(new ErrNickname(ErrNicknameReason.TAKEN));
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
                    // view.dispatch(new ErrAction(e));
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
                System.out.printf("adding %s, %d waiting\n", nickname, waiting.size());

                waiting.forEach(v -> v.dispatch(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.get(0)))));
    
                if (newGamePlayersCount != 0)
                    view.dispatch(new UpdateJoinGame(newGamePlayersCount));
    
                if (waiting.size() == newGamePlayersCount) {
                    System.out.printf("%s joining started a new game\n", nicknames.get(view));
                    startNewGame();
                }
            }
        }
    }

    /* If the player is one of the first to join, it will have to choose the total number of players in the match. */
    public void prepareNewGame(View view, int newGamePlayersCount) {
        synchronized(lock) {
            if (!checkNickname(view))
                return;
    
            if (waiting.indexOf(view) != 0) {
                System.out.printf("%s: failed to set players count to %d.%n", nicknames.get(view), newGamePlayersCount);
                view.dispatch(new ErrNewGame(false));
                return;
            }
    
            if (newGamePlayersCount == 0) {
                view.dispatch(new ErrNewGame(true));
                return;
            }
    
            System.out.printf("%s: setting players count to %d.%n", nicknames.get(view), newGamePlayersCount);
            this.newGamePlayersCount = newGamePlayersCount;

            waiting.subList(0, Math.min(waiting.size(), newGamePlayersCount)).forEach(v -> v.dispatch(new UpdateJoinGame(newGamePlayersCount)));
    
            if (waiting.size() >= newGamePlayersCount){
                startNewGame();
                System.out.printf("%s prepared a new game\n", nicknames.get(view));
            }
        }
    }

    public void startNewGame() {
        synchronized(lock) {
            Game newGame = newGamePlayersCount == 1 ?
                    gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
                    gameFactory.getMultiGame(waiting.subList(0, newGamePlayersCount).stream().map(nicknames::get).toList());
    
            GameContext context = new GameContext(newGame, gameFactory);
            waiting.subList(0, newGamePlayersCount).forEach(view -> {
                context.registerViewToModel(view, nicknames.get(view));
                joined.put(view, context);
            });
            context.start();
            System.out.printf("started context, waiting list %d\n", waiting.size());
    
            /* Remove players who joined from waiting list */
            waiting.subList(0, newGamePlayersCount).clear();
            System.out.printf("removed %d waiting %d\n", newGamePlayersCount, waiting.size());

            waiting.forEach(v -> v.dispatch(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.get(0)))));
    
            newGamePlayersCount = 0;
        }
    }

    public void exit(View view) {
        synchronized(lock) {
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
            view.dispatch(new ResQuit());
        }
    }

    public void checkJoinedThen(View view, BiConsumer<GameContext, String> then) {
        if (checkJoined(view)) {
            String nickname = "";
    
            synchronized(lock) {
                nickname = nicknames.get(view);
            }
    
            then.accept(joined.get(view), nickname);
        }
    }

    private boolean checkNickname(View view) {
        if (!nicknames.containsKey(view)) {
            view.dispatch(new ErrNickname(ErrNicknameReason.NOTSET));
            return false;
        }
        return true;
    }

    private boolean checkJoined(View view) {
        synchronized(lock) {
            if (!checkNickname(view))
                return false;
            if (!joined.containsKey(view)) {
                view.dispatch(new ErrNickname(ErrNicknameReason.NOTINGAME));
                return false;
            }
            return true;
        }
    }
}
