package it.polimi.ingsw.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ServerClientHandler implements Runnable {
    private final Socket socket;
    private final int timeout;

    public ServerClientHandler(Socket socket) {
        this.socket = socket;
        this.timeout = 7;
    }

    public ServerClientHandler(Socket socket, int timeout) {
        this.socket = socket;
        this.timeout = timeout;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine, outputLine;

            GameProtocol gp = new GameProtocol();
            outputLine = gp.processInput(null);
            out.println(outputLine);

            while (true) {
                ExecutorService ex = Executors.newSingleThreadExecutor();
                Future<String> result = ex.submit(in::readLine);

                ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
                ScheduledFuture<?> counter = timer.scheduleAtFixedRate(new Runnable() {
                    int i = timeout;
                    @Override
                    public void run() {
                        System.out.println(i > 0 ? i-- : "Turn ended due to inactivity.");
                    }
                }, 0, 1, TimeUnit.SECONDS);

                try {
                    inputLine = result.get(timeout, SECONDS);
                    if(inputLine == null) break;
                    outputLine = gp.processInput(inputLine);
                    out.println(outputLine);
                    if (outputLine.equals("Bye.")) {
                        break;
                    }
                } catch (ExecutionException e) {
                    e.getCause().printStackTrace();
                } catch (TimeoutException e) {
                    result.cancel(true);
                    counter.cancel(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    ex.shutdownNow();
                    timer.shutdownNow();
                    //Some code to change state or turn
                }
            }
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
