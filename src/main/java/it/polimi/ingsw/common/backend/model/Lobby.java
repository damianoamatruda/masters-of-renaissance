package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ResGoodbye;
import it.polimi.ingsw.common.events.mvevents.UpdateBookedSeats;
import it.polimi.ingsw.common.events.mvevents.UpdateJoinGame;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNewGame;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname;
import it.polimi.ingsw.common.events.mvevents.errors.ErrNickname.ErrNicknameReason;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

public class Lobby {
    private final Object lock;
    private final GameFactory gameFactory;
    public final Map<View, String> nicknames;
    public final Map<View, GameContext> joined;
    public final Map<String, GameContext> disconnected;
    public final ConcurrentLinkedQueue<View> waiting;
    public int newGamePlayersCount;

    public Lobby(GameFactory gameFactory) {
        this.lock = new Object();
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        this.joined = new HashMap<>();
        this.waiting = new ConcurrentLinkedQueue<>();
        this.disconnected = new HashMap<>();
    }

    /* The player joining is one of the first of the match if 'freeSeats' is negative. */
    public void joinLobby(View view, String nickname) {
        synchronized(lock) {
            if (nicknames.containsKey(view)) {
                view.on(new ErrNickname(ErrNicknameReason.ALREADYSET));
                return;
            }
            if (nickname == null || nickname.isBlank()) {
                view.on(new ErrNickname(ErrNicknameReason.NOTSET));
                return;
            }
            if (nicknames.containsValue(nickname)) {
                view.on(new ErrNickname(ErrNicknameReason.TAKEN));
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
                System.out.printf("adding %s, %d waiting\n", nickname, waiting.size());
    
                waiting.forEach(v -> v.on(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.peek()))));
    
                if (newGamePlayersCount != 0)
                    view.on(new UpdateJoinGame(newGamePlayersCount));
    
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
    
            if (waiting.peek() != view) {
                System.out.printf("%s: failed to set players count to %d.%n", nicknames.get(view), newGamePlayersCount);
                view.on(new ErrNewGame(false));
                return;
            }
    
            if (newGamePlayersCount == 0) {
                view.on(new ErrNewGame(true));
                return;
            }
    
            System.out.printf("%s: setting players count to %d.%n", nicknames.get(view), newGamePlayersCount);
            this.newGamePlayersCount = newGamePlayersCount;
    
            Iterator<View> iter = waiting.iterator();
            int i = 0;
            while (iter.hasNext() && i < Math.min(waiting.size(), newGamePlayersCount)) {
                View v = iter.next();
                v.on(new UpdateJoinGame(newGamePlayersCount));
            }
    
            if (waiting.size() >= newGamePlayersCount){
                startNewGame();
                System.out.printf("%s prepared a new game\n", nicknames.get(view));
            }
        }
    }

    public void startNewGame() {
        synchronized(lock) {
            ConcurrentLinkedQueue<View> gamePlayers = new ConcurrentLinkedQueue<>();
            Iterator<View> iter = waiting.iterator();
            int i = 0;
            while (iter.hasNext() && i < newGamePlayersCount)
                gamePlayers.add(iter.next());

            Game newGame = newGamePlayersCount == 1 ?
                    gameFactory.getSoloGame(nicknames.get(waiting.peek())) :
                    gameFactory.getMultiGame(gamePlayers.stream().map(nicknames::get).toList());
    
            GameContext context = new GameContext(newGame, gameFactory);
            
            iter = waiting.iterator(); i = 0;
            while (iter.hasNext() && i < newGamePlayersCount) {
                View view = iter.next();
                
                context.registerViewToModel(view, nicknames.get(view));
                joined.put(view, context);

                iter.remove();
            }

            context.start();    
            
            System.out.printf("removed %d waiting %d\n", newGamePlayersCount, waiting.size());
    
            waiting.forEach(v -> v.on(new UpdateBookedSeats(waiting.size(), nicknames.get(waiting.peek()))));
    
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

                if(waiting.contains(view))
                    waiting.remove(view);
            }
            view.on(new ResGoodbye());
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
            view.on(new ErrNickname(ErrNicknameReason.NOTSET));
            return false;
        }
        return true;
    }

    private boolean checkJoined(View view) {
        synchronized(lock) {
            if (!checkNickname(view))
                return false;
            if (!joined.containsKey(view)) {
                view.on(new ErrNickname(ErrNicknameReason.NOTINGAME));
                return false;
            }
            return true;
        }
    }
}
