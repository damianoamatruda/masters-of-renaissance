package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gamecontext.GameContext;
import it.polimi.ingsw.server.view.View;

import java.util.*;

public class Lobby {
    private final GameFactory gameFactory;
    private final Map<View, String> nicknames;
    private final List<GameContext> games;
    private final Map<String, GameContext> joined;
    private final List<String> waiting;
    private int countToNewGame;

    public Lobby(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        this.nicknames = new HashMap<>();
        this.games = new ArrayList<>();
        this.joined = new HashMap<>();
        this.waiting = new ArrayList<>();
    }

    public void joinLobby(View view, String nickname) {
        if (nicknames.containsKey(view)) {
            view.updateNicknameError("Client already has nickname \"" + nicknames.get(view) + "\".");
            return;
        }
        if (nickname == null || nickname.isBlank()) {
            view.updateNicknameError("Given nickname is empty.");
            return;
        }
        if (nicknames.containsValue(nickname)) {
            view.updateNicknameError("Nickname \"" + nickname + "\" already in use. Choose another one.");
            return;
        }
        System.out.println("Setting nickname \"" + nickname + "\"...");
        nicknames.put(view, nickname);

        waiting.add(nickname);
        if (waiting.size() == countToNewGame)
            startNewGame();

        view.updateNickname(waiting.size() == 1);
    }

    public Optional<GameContext> getJoinedGame(String nickname) {
        return Optional.ofNullable(joined.get(nickname));
    }

    public void setCountToNewGame(View view, int count) {
        if (!isPlayerFirst(view)) {
            view.updateActionError("Command unavailable. You are not the first player who joined.");
            return;
        }
        System.out.println("Setting count of players...");
        this.countToNewGame = count;
        if (waiting.size() == countToNewGame)
            startNewGame();
    }

    public void startNewGame() {
        Game newGame = countToNewGame == 1 ? gameFactory.getSoloGame(waiting.get(0)) : gameFactory.getMultiGame(waiting.subList(0, countToNewGame));
        GameContext newContext = new GameContext(newGame);
        games.add(newContext);
        waiting.subList(0, countToNewGame).forEach(p -> joined.put(p, newContext));
        waiting.subList(0, countToNewGame).clear();
        countToNewGame = 0;
    }

    public boolean isPlayerFirst(View view) {
        return waiting.indexOf(nicknames.get(view)) == 0;
    }

    public Optional<Player> getPlayer(View view) {
        return joined.get(nicknames.get(view)).getPlayer(nicknames.get(view));
    }

    public boolean checkView(View view) {
        boolean valid = nicknames.containsKey(view);
        if (!valid)
            view.updateActionError("Client must first request a nickname.");
        return valid;
    }
}
