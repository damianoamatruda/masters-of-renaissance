package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.LocalClient;
import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.mvevents.errors.*;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Cli extends EventDispatcher implements Ui {
    static final int width = 80;

    private final View view;

    /** The current state of the interface. */
    private CliState state;

    public Cli() {
        this.view = new CliView();
        this.state = new SplashState();
    }

    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static String center(String s) {
        if (s.lines().count() == 0)
            return "";

        int maxLineWidth = s.lines().mapToInt(String::length).max().orElseThrow();

        if (maxLineWidth > width)
            throw new IllegalArgumentException();

        int marginLeft = (width - maxLineWidth) / 2;

        StringBuilder stringBuilder = new StringBuilder();
        s.lines().forEachOrdered(line -> stringBuilder.append(" ".repeat(marginLeft)).append(line).append("\n"));
        return stringBuilder.toString();
    }

    static void clear(PrintStream out) {
        out.print("\033[H\033[2J");
        out.flush();
    }

    static void pause(Scanner in) {
        in.nextLine();
    }

    static String prompt(PrintStream out, Scanner in, String prompt, String defaultValue) {
        out.printf("%s (default: %s): ", prompt, defaultValue);
        String value = in.nextLine();
        return !value.isBlank() ? value : defaultValue;
    }

    static String prompt(PrintStream out, Scanner in, String prompt) {
        out.printf("%s: ", prompt);
        return in.nextLine();
    }

    /**
     * Sets the state.
     *
     * @param state the next state
     */
    void setState(CliState state) {
        this.state = state;
        state.render(this, System.out, new Scanner(System.in));
    }

    @Override
    public void execute() {
        state.render(this, System.out, new Scanner(System.in));
    }

    public void registerOnMV(EventDispatcher view) {
        view.addEventListener(ErrAction.class, this::on);
        view.addEventListener(ErrActiveLeaderDiscarded.class, this::on);
        view.addEventListener(ErrBuyDevCard.class, this::on);
        view.addEventListener(ErrCardRequirements.class, this::on);
        view.addEventListener(ErrInitialChoice.class, this::on);
        view.addEventListener(ErrNewGame.class, this::on);
        view.addEventListener(ErrNickname.class, this::on);
        view.addEventListener(ErrObjectNotOwned.class, this::on);
        view.addEventListener(ErrReplacedTransRecipe.class, this::on);
        view.addEventListener(ErrReplacedTransRecipe.class, this::on);
        view.addEventListener(ErrResourceReplacement.class, this::on);
        view.addEventListener(ErrResourceTransfer.class, this::on);
        view.addEventListener(UpdateBookedSeats.class, this::on);
        view.addEventListener(UpdateJoinGame.class, this::on);
        view.addEventListener(UpdateGameStart.class, this::on);
        view.addEventListener(UpdateCurrentPlayer.class, this::on);
        view.addEventListener(UpdateSetupDone.class, this::on);
        view.addEventListener(UpdateGameResume.class, this::on);
        view.addEventListener(UpdateLastRound.class, this::on);
        view.addEventListener(UpdateGameEnd.class, this::on);
        view.addEventListener(UpdatePlayer.class, this::on);
        view.addEventListener(UpdateLeadersHandCount.class, this::on);
        view.addEventListener(UpdateFaithPoints.class, this::on);
        view.addEventListener(UpdateVictoryPoints.class, this::on);
        view.addEventListener(UpdatePlayerStatus.class, this::on);
        view.addEventListener(UpdateDevCardSlot.class, this::on);
        view.addEventListener(UpdateLeader.class, this::on);
        view.addEventListener(UpdateResourceContainer.class, this::on);
        view.addEventListener(UpdateDevCardGrid.class, this::on);
        view.addEventListener(UpdateMarket.class, this::on);
        view.addEventListener(UpdateVaticanSection.class, this::on);
        view.addEventListener(UpdateActionToken.class, this::on);
        view.addEventListener(UpdateLeadersHand.class, this::on);
    }

    public void unregisterOnMV(EventDispatcher view) {
        view.removeEventListener(ErrAction.class, this::on);
        view.removeEventListener(ErrActiveLeaderDiscarded.class, this::on);
        view.removeEventListener(ErrBuyDevCard.class, this::on);
        view.removeEventListener(ErrCardRequirements.class, this::on);
        view.removeEventListener(ErrInitialChoice.class, this::on);
        view.removeEventListener(ErrNewGame.class, this::on);
        view.removeEventListener(ErrNickname.class, this::on);
        view.removeEventListener(ErrObjectNotOwned.class, this::on);
        view.removeEventListener(ErrReplacedTransRecipe.class, this::on);
        view.removeEventListener(ErrReplacedTransRecipe.class, this::on);
        view.removeEventListener(ErrResourceReplacement.class, this::on);
        view.removeEventListener(ErrResourceTransfer.class, this::on);
        view.removeEventListener(UpdateBookedSeats.class, this::on);
        view.removeEventListener(UpdateJoinGame.class, this::on);
        view.removeEventListener(UpdateGameStart.class, this::on);
        view.removeEventListener(UpdateCurrentPlayer.class, this::on);
        view.removeEventListener(UpdateSetupDone.class, this::on);
        view.removeEventListener(UpdateGameResume.class, this::on);
        view.removeEventListener(UpdateLastRound.class, this::on);
        view.removeEventListener(UpdateGameEnd.class, this::on);
        view.removeEventListener(UpdatePlayer.class, this::on);
        view.removeEventListener(UpdateLeadersHandCount.class, this::on);
        view.removeEventListener(UpdateFaithPoints.class, this::on);
        view.removeEventListener(UpdateVictoryPoints.class, this::on);
        view.removeEventListener(UpdatePlayerStatus.class, this::on);
        view.removeEventListener(UpdateDevCardSlot.class, this::on);
        view.removeEventListener(UpdateLeader.class, this::on);
        view.removeEventListener(UpdateResourceContainer.class, this::on);
        view.removeEventListener(UpdateDevCardGrid.class, this::on);
        view.removeEventListener(UpdateMarket.class, this::on);
        view.removeEventListener(UpdateVaticanSection.class, this::on);
        view.removeEventListener(UpdateActionToken.class, this::on);
        view.removeEventListener(UpdateLeadersHand.class, this::on);
    }

    void startNetworkClient(String host, int port) {
        new NetworkClient(host, port, view, this).start();
    }

    void startLocalClient() {
        new LocalClient(view, this).start();
    }

    private void on(MVEvent event) {
        // TODO: Implement all listeners
        System.out.println("Temporary listener.");
    }
}
