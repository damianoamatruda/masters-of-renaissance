package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Ui;
import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.components.Resource;
import it.polimi.ingsw.client.cli.components.ResourceContainerSet;
import it.polimi.ingsw.client.cli.components.ResourceMap;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents the command-line interface of the client application.
 */
public class Cli implements Runnable {
    private static final int width = 179;
    private static final String backValue = " ";
    private static Cli instance = null;
    private final Ui ui;
    private final Thread runThread;
    private final AtomicBoolean isReturningToMainMenu;
    private final PrintStream out;
    private final Scanner in;
    private ExecutorService executor;
    private volatile boolean hasNextState;
    private volatile CliController controller;

    public Cli() {
        Cli.instance = this;
        this.ui = new Ui();
        this.runThread = new Thread(this);
        this.executor = Executors.newCachedThreadPool();
        this.isReturningToMainMenu = new AtomicBoolean(false);
        this.out = System.out;
        this.in = new Scanner(System.in);
        this.hasNextState = false;
        this.controller = null;
    }

    public static void main(String[] args) {
        new Cli().start();
    }

    public static Cli getInstance() {
        return instance;
    }

    public static String color(String str, String ansiColor) {
        return String.format("%s%s\u001B[0m", ansiColor, str);
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String center(String str, int width, char fill) {
        List<String> lines = str.lines().findAny().isPresent() ? str.lines().toList() : List.of("");

        if (maxLineWidth(str) > width)
            throw new IllegalArgumentException("String too long:" + "\n" +
                    lines.stream().filter(s -> s.length() == maxLineWidth(str)).findAny().orElseThrow() + "\n" +
                    String.format("Max length: %d. Got: %d.", width, textLength(lines.stream().filter(s -> s.length() == maxLineWidth(str)).findAny().orElseThrow())));

        int marginLeft = (width - maxLineWidth(str)) / 2;

        StringBuilder stringBuilder = new StringBuilder();
        lines.forEach(line -> stringBuilder.append(Character.toString(fill).repeat(marginLeft)).append(line).append(Character.toString(fill).repeat(width - marginLeft - textLength(line))).append("\n"));
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public static String center(String str, int width) {
        return center(str, width, ' ');
    }

    public static String center(String str) {
        return center(str, width);
    }

    public static String centerAll(String str) {
        List<String> lines = str.lines().findAny().isPresent() ? str.lines().toList() : List.of("");
        StringBuilder stringBuilder = new StringBuilder();
        lines.forEach(line -> stringBuilder.append(center(line)).append("\n"));
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public static String left(String str, int width, char fill) {
        if (textLength(str) > width)
            throw new IllegalArgumentException("String too long:" + "\n" +
                    str + "\n" +
                    String.format("Max length: %d. Got: %d.", width, textLength(str)));
        return str + Character.toString(fill).repeat(width - textLength(str));
    }

    public static String left(String str, int width) {
        return left(str, width, ' ');
    }

    public static String left(String str) {
        return left(str, width);
    }

    public static String right(String str, int width, char fill) {
        return Character.toString(fill).repeat(width - textLength(str)) + str;
    }

    public static String right(String str, int width) {
        return right(str, width, ' ');
    }

    public static String right(String str) {
        return right(str, width);
    }

    public static int textLength(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    public static int maxLineWidth(String str) {
        return str.lines().mapToInt(Cli::textLength).max().orElse(0);
    }

    public static int maxLinesHeight(List<String> str) {
        return Math.toIntExact(str.stream().map(String::lines).mapToLong(Stream::count).max().orElse(0));
    }

    public static String slimLine(int width) {
        return slimLineNoNewLine(width) + "\n";
    }

    public static String slimLineNoNewLine(int width) {
        return "─".repeat(width);
    }

    public static String slimLine() {
        return slimLine(width);
    }

    public static String bold(String str) {
        return String.format("\u001B[1m%s\u001B[0m", str);
    }

    public static String boldColor(String str, String ansiColor) {
        return bold(color(str, ansiColor));
    }

    public void start() {
        runThread.start();
        setController(new SplashController());
    }

    /**
     * Sets the controller.
     *
     * @param newController the controller to set
     */
    synchronized void setController(CliController newController) {
        if (!executor.isShutdown())
            executor.submit(() -> {
                synchronized (this) {
                    ui.setController(newController);
                    this.controller = newController;
                    this.hasNextState = true;
                    notifyAll();
                }
            });
    }

    synchronized void reloadController() {
        setController(controller);
    }

    synchronized void reloadController(String str) {
        alert(str);
        reloadController();
    }

    void quit() {
        clear();
        stop();
        System.exit(0);
    }

    public void stop() {
        runThread.interrupt();
        executor.shutdownNow();
        ui.stop();
    }

    void setReturningToMainMenu() {
        isReturningToMainMenu.set(true);

        executor.shutdownNow();

        synchronized (this) {
            controller = new MainMenuController();
            hasNextState = true;
            notifyAll();
        }
    }

    public void restartExecutor() {
        executor = Executors.newSingleThreadExecutor();
    }

    public Ui getUi() {
        return ui;
    }

    public PrintStream getOut() {
        return out;
    }

    public Scanner getIn() {
        return in;
    }

    public ViewModel getViewModel() {
        return ui.getViewModel();
    }

    /**
     * Sets the current state based on the next requested state. If no state is requested, waits for a request. While a
     * state is being rendered, requested states are not applied: only the last requested state is applied, and only as
     * soon as the current state has finished rendering.
     */
    @Override
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()) {
            while (!hasNextState) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            clear();

            if (isReturningToMainMenu.get())
                controller = new MainMenuController();

            controller.render();

            isReturningToMainMenu.set(false);
            hasNextState = false;
        }
    }

    synchronized void pause() {
        out.flush();
        in.nextLine();
    }

    synchronized void promptPause() {
        out.println();
        out.println(center("[Press ENTER to continue]"));
        pause();
    }

    synchronized void alert(String message) {
        out.println();
        out.println(center(message));
        promptPause();
    }

    synchronized void clear() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    public synchronized Optional<String> prompt(String prompt, String defaultValue) {
        if (prompt.isEmpty())
            throw new IllegalArgumentException("Prompt cannot be empty.");
        out.print(right(String.format("%s (default: %s): ", prompt, defaultValue), width / 3));
        out.flush();
        String value = in.nextLine();
        if (value.equals(backValue))
            return Optional.empty();
        return Optional.of(!value.isBlank() ? value : defaultValue);
    }

    public synchronized Optional<String> prompt(String prompt) {
        if (!prompt.isEmpty())
            out.print(right(String.format("%s: ", prompt), width / 3));
        out.flush();
        String value = in.nextLine();
        if (value.equals(backValue))
            return Optional.empty();
        return Optional.of(value);
    }

    public Optional<Integer> promptInt(String prompt) {
        int value;
        while (true) {
            Optional<String> input = prompt(prompt);
            if (input.isEmpty())
                return Optional.empty();
            try {
                value = Integer.parseInt(input.get());
                break;
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.of(value);
    }

    public Optional<File> promptFile(String prompt) {
        Optional<String> path = prompt(prompt);
        if (path.isEmpty())
            return Optional.empty();
        return Optional.of(new File(path.get()));
    }

    Optional<Map<Integer, Map<String, Integer>>> promptShelves(Map<String, Integer> resMap, Set<Integer> allowedShelves,
                                                               boolean includeStrongbox, boolean discardable) {
        ViewModel vm = ui.getViewModel();

        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        new ResourceContainerSet(
                vm.getLocalPlayer().orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerDepots).orElseThrow(),
                includeStrongbox ? vm.getLocalPlayer().flatMap(vm::getPlayerStrongbox).orElseThrow() : null)
                .render();

        Map<String, Integer> remainingResMap = new HashMap<>(resMap);
        int totalQuantity = remainingResMap.values().stream().mapToInt(Integer::intValue).sum();
        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();

            String string = "Remaining resources:" + "\n" +
                    new ResourceMap(remainingResMap).getString();
            out.println(center(string));

            AtomicBoolean valid = new AtomicBoolean(true);
            AtomicBoolean discarded = new AtomicBoolean(false);

            out.println();

            if (discardable) {
                out.println(center("Press D if you want to discard all remaining resources."));
                out.println(center("Press ENTER otherwise."));

                prompt("").ifPresentOrElse(input -> {
                    if (input.equalsIgnoreCase("D"))
                        discarded.set(true);
                }, () -> valid.set(false));
            }

            if (discarded.get())
                break;

            if (valid.get()) {
                Set<String> allowedResources;
                if (remainingResMap.containsKey("Blank")) {
                    allowedResources = vm.getResourceTypes().stream().filter(ReducedResourceType::isStorable).map(ReducedResourceType::getName).collect(Collectors.toSet());
                } else
                    allowedResources = remainingResMap.keySet();
                promptResource(allowedResources).ifPresentOrElse(res -> promptQuantity(remainingResMap.containsKey("Blank") ? remainingResMap.get("Blank") : remainingResMap.get(res)).ifPresentOrElse(quantity -> promptShelfId(allowedShelves, includeStrongbox).ifPresentOrElse(shelfId -> {
                    shelves.compute(shelfId, (sid, rMap) -> {
                        if (rMap == null)
                            rMap = new HashMap<>();
                        rMap.compute(res, (k, v) -> v == null ? quantity : quantity + v);
                        return rMap;
                    });

                    allocQuantity.addAndGet(quantity);
                    if (remainingResMap.containsKey("Blank") && remainingResMap.get("Blank") > 0)
                        remainingResMap.computeIfPresent("Blank", (k, v) -> v - quantity);
                    else
                        remainingResMap.computeIfPresent(res, (k, v) -> v - quantity);
                }, () -> valid.set(false)), () -> valid.set(false)), () -> valid.set(false));
            }

            if (!valid.get())
                return Optional.empty();
        }

        return Optional.of(shelves);
    }

    Optional<Map<String, Integer>> promptResources(Set<String> allowedResources, int totalQuantity) {
        Map<String, Integer> replacedRes = new HashMap<>();

        if (!allowedResources.isEmpty())
            out.println(center("Resources you can choose:"));

        out.println();
        out.println(center(printAllowedResources(allowedResources, 14)));

        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();

            AtomicBoolean valid = new AtomicBoolean(true);

            promptResource(allowedResources).ifPresentOrElse(res -> promptQuantity(totalQuantity - allocQuantity.get()).ifPresentOrElse(quantity -> {
                allocQuantity.addAndGet(quantity);
                replacedRes.compute(res, (r, q) -> q == null ? quantity : q + quantity);
            }, () -> valid.set(false)), () -> valid.set(false));

            if (!valid.get())
                return Optional.empty();
        }

        return Optional.of(replacedRes);
    }

    Optional<Map<Integer, Map<String, Integer>>> promptShelvesSetup(Set<String> allowedResources, int totalQuantity, Set<Integer> allowedShelves) {
        ViewModel vm = ui.getViewModel();

        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        new ResourceContainerSet(
                vm.getLocalPlayer().orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerDepots).orElseThrow(),
                null)
                .render();

        AtomicInteger allocQuantity = new AtomicInteger();
        while (allocQuantity.get() < totalQuantity) {
            out.println();
            out.print(center(String.format("You can choose %d resources among these:%n%n", totalQuantity - allocQuantity.get())));

            out.println();
            out.println(center(printAllowedResources(allowedResources, 14)));

            out.println();

            AtomicBoolean valid = new AtomicBoolean(true);
            promptResource(allowedResources).ifPresentOrElse(res -> promptQuantity(totalQuantity - allocQuantity.get()).ifPresentOrElse(quantity -> promptShelfId(allowedShelves, false).ifPresentOrElse(shelfId -> {
                shelves.compute(shelfId, (sid, rMap) -> {
                    if (rMap == null)
                        rMap = new HashMap<>();
                    rMap.compute(res, (k, v) -> v == null ? quantity : quantity + v);
                    return rMap;
                });

                allocQuantity.addAndGet(quantity);
            }, () -> valid.set(false)), () -> valid.set(false)), () -> valid.set(false));

            if (!valid.get())
                return Optional.empty();
        }

        return Optional.of(shelves);
    }

    private Optional<String> promptResource(Set<String> resTypes) {
        Optional<String> resType;
        do {
            Optional<String> input = prompt("Resource");
            if (input.isEmpty())
                return Optional.empty();
            resType = resTypes.stream().filter(r -> r.equalsIgnoreCase(input.get())).findAny();
        } while (resType.isEmpty());
        return resType;
    }

    private Optional<Integer> promptQuantity(int maxQuantity) {
        int quantity = -1;
        Optional<String> input;
        while (quantity < 1) {
            input = prompt("Quantity");
            if (input.isEmpty())
                return Optional.empty();
            try {
                quantity = Integer.parseInt(input.get());
                if (quantity > maxQuantity)
                    quantity = -1;
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.of(quantity);
    }

    private Optional<Integer> promptShelfId(Set<Integer> allowedShelves, boolean includeStrongbox) {
        int shelfId = -1;
        Optional<String> input;
        while (shelfId < 0) {
            input = prompt(includeStrongbox ? "Container" : "Shelf");
            if (input.isEmpty())
                return Optional.empty();
            try {
                shelfId = Integer.parseInt(input.get());
                if (!allowedShelves.contains(shelfId))
                    shelfId = -1;
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.of(shelfId);
    }

    private String printAllowedResources(Set<String> resources, int padding) {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        for (String resType : resources) {
            if (i % 2 == 0)
                stringBuilder.append(String.format("  %s", left(String.format("%s", new Resource(resType).getString()), padding)));
            else
                stringBuilder.append(String.format("%s", new Resource(resType).getString())).append("\n");
            i++;
        }
        if (resources.size() % 2 != 0)
            stringBuilder.append("\n");

        return stringBuilder.toString();

    }
}
