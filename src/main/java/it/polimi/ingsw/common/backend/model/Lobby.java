package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.AsynchronousEventDispatcher;
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
import java.util.logging.Logger;

public class Lobby extends AsynchronousEventDispatcher {
    private static final Logger LOGGER = Logger.getLogger(Lobby.class.getName());
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

    public synchronized void joinLobby(View view, String nickname) {
        if (nicknames.containsKey(view)) {
            dispatch(new ErrNickname(view, ErrNicknameReason.ALREADY_SET));
            return;
        }
        if (nickname == null || nickname.isBlank()) {
            dispatch(new ErrNickname(view, ErrNicknameReason.NOT_SET));
            return;
        }
        if (nicknames.containsValue(nickname)) {
            dispatch(new ErrNickname(view, ErrNicknameReason.TAKEN));
            return;
        }

        /* nickname validated, join lobby */
        nicknames.put(view, nickname);

        if (disconnected.containsKey(nickname)) {
            LOGGER.info(String.format("Player '%s' rejoined", nickname));

            /* Get the match the nickname was previously in and set the player back to active */
            GameContext context = disconnected.get(nickname);
            context.setActive(nickname, true);

            /* Resuming routine (observer registrations and state messages) */
            view.registerOnModelGameContext(context);
            joined.put(view, context);
            disconnected.remove(nickname);
            context.dispatchResumeState(view, nickname);
        } else {
            LOGGER.info(String.format("Set nickname '%s'", nickname));

            waiting.add(view);

            LOGGER.info(String.format("Adding '%s', %d waiting", nickname, waiting.size()));

            waiting.forEach(v -> dispatch(new UpdateBookedSeats(v, waiting.size(), nicknames.get(waiting.get(0)))));

            if (newGamePlayersCount != 0)
                dispatch(new UpdateJoinGame(view, newGamePlayersCount));

            if (waiting.size() == newGamePlayersCount) {
                LOGGER.info(String.format("%s joining started a new game", nicknames.get(view)));
                startNewGame();
            }
        }
    }

    /* If the player is one of the first to join, it will have to choose the total number of players in the match. */
    public synchronized void prepareNewGame(View view, int newGamePlayersCount) {
        if (!checkNickname(view))
            return;

        if (waiting.indexOf(view) != 0) {
            LOGGER.info(String.format("%s: failed to set players count to %d", nicknames.get(view), newGamePlayersCount));
            dispatch(new ErrNewGame(view, false));
            return;
        }

        if (newGamePlayersCount == 0) {
            dispatch(new ErrNewGame(view, true));
            return;
        }

        LOGGER.info(String.format("%s: setting players count to %d", nicknames.get(view), newGamePlayersCount));
        this.newGamePlayersCount = newGamePlayersCount;

        waiting.subList(0, Math.min(waiting.size(), newGamePlayersCount)).forEach(v -> dispatch(new UpdateJoinGame(v, newGamePlayersCount)));

        if (waiting.size() >= newGamePlayersCount) {
            LOGGER.info(String.format("%s: prepared a new game", nicknames.get(view)));
            startNewGame();
        }
    }

    public synchronized void quit(View view) {
        String nickname = nicknames.get(view);
        if (nickname != null) {
            GameContext context = joined.get(view);
            if (context != null) {
                view.unregisterOnModelGameContext(context);

                context.setActive(nickname, false);
                disconnected.put(nickname, context);
                joined.remove(view);
            }
            nicknames.remove(view);

            waiting.remove(view);
        }
        dispatch(new ResQuit(view));
    }

    public void checkJoinedThen(View view, BiConsumer<GameContext, String> then) {
        boolean hasJoined;

        synchronized (this) {
            hasJoined = checkJoined(view);
        }

        if (hasJoined) {
            String nickname;

            synchronized (this) {
                nickname = nicknames.get(view);
            }

            synchronized (joined.get(view)) {
                then.accept(joined.get(view), nickname);
            }
        }
    }

    @Override
    public void close() {
        super.close();
        joined.values().forEach(AsynchronousEventDispatcher::close);
        disconnected.values().forEach(AsynchronousEventDispatcher::close);
    }

    private void startNewGame() {
        Game newGame = newGamePlayersCount == 1 ?
                gameFactory.getSoloGame(nicknames.get(waiting.get(0))) :
                gameFactory.getMultiGame(waiting.subList(0, newGamePlayersCount).stream().map(nicknames::get).toList());

        GameContext context = new GameContext(newGame, gameFactory);
        waiting.subList(0, newGamePlayersCount).forEach(view -> {
            view.registerOnModelGameContext(context);
            joined.put(view, context);
        });
        waiting.subList(0, newGamePlayersCount).forEach(view -> context.dispatchStartState(view, nicknames.get(view)));

        LOGGER.info(String.format("Started context, %d waiting", waiting.size()));

        /* Remove players who joined from waiting list */
        waiting.subList(0, newGamePlayersCount).clear();

        LOGGER.info(String.format("%d removed, %d waiting", newGamePlayersCount, waiting.size()));

        waiting.forEach(v -> dispatch(new UpdateBookedSeats(v, waiting.size(), nicknames.get(waiting.get(0)))));

        newGamePlayersCount = 0;
    }

    private boolean checkNickname(View view) {
        if (!nicknames.containsKey(view)) {
            dispatch(new ErrNickname(view, ErrNicknameReason.NOT_SET));
            return false;
        }
        return true;
    }

    private boolean checkJoined(View view) {
        synchronized(lock) {
            if (!checkNickname(view))
                return false;
            if (!joined.containsKey(view)) {
                dispatch(new ErrNickname(view, ErrNicknameReason.NOT_IN_GAME));
                return false;
            }
            return true;
        }
    }
}
