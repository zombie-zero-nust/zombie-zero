package edu.nust.engine.core;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DevConsole
{
    private static final GameLogger LOGGER = GameLogger.getLogger(DevConsole.class);

    public static final String ALLOWED_REGEX = "[a-zA-Z0-9/ -.]";

    private final VBox container = new VBox(6);
    private final Label hint = new Label();
    private final TextField input = new TextField();
    private final ScrollPane outputHolder = new ScrollPane();
    private final VBox outputContainer = new VBox(8);
    private final VBox statsBox = new VBox(2);
    private final Label fpsLabel = new Label("FPS: --");
    private final Label objectsInViewLabel = new Label("GOiV: --");
    private final Label totalObjectsLabel = new Label("TGO: --");


    private final Map<String, DevCommand> commands = new LinkedHashMap<>();
    private final List<String> suggestions = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    private int historyIndex = -1;
    private int suggestionIndex = -1;

    private boolean open = false;

    private Supplier<Double> fpsSupplier;
    private Supplier<Integer> objectsInViewSupplier;
    private Supplier<Integer> totalObjectsSupplier;

    /// <b>{@code INTERNAL}</b>
    DevConsole()
    {
        LOGGER.trace("Constructing DevConsole");
        setupUI();
    }

    /* UI */

    private void setupUI()
    {
        LogProgress initConsoleLogger = LogProgress.create("CONSOLE", LOGGER);
        initConsoleLogger.begin("Initializing DevConsole UI");

        setupContainer();
        setupHint();
        setupInput();
        setupOutputHolder();
        setupStatsPanel();

        setupInputListeners(initConsoleLogger);

        container.getChildren().setAll(hint, input, outputHolder, statsBox);

        Platform.runLater(() -> outputHolder.setVvalue(1.0));

        initConsoleLogger.end("DevConsole UI initialized successfully");
    }

    private void setupContainer()
    {
        container.setVisible(false);
        container.setManaged(false);
        container.getStyleClass().add("dev-console");
        container.setSpacing(8);
        container.setMaxWidth(520);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: rgba(15,15,15,0.95);" + "-fx-border-color: rgba(255,255,255,0.15);" + "-fx-border-width: 1;" + "-fx-background-radius: 6;" + "-fx-border-radius: 6;");
    }

    private void setupHint()
    {
        hint.setText("Dev Console");
        hint.setStyle("-fx-text-fill: #9aa4ad; -fx-font-size: 12px;");
    }

    private void setupInput()
    {
        input.setStyle("-fx-background-color: rgba(30,30,30,0.9);" + "-fx-text-fill: #e6e6e6;" + "-fx-prompt-text-fill: #666666;" + "-fx-border-color: rgba(255,255,255,0.10);" + "-fx-border-radius: 3;" + "-fx-background-radius: 3;");
        input.setPromptText("Type a command");
        input.setFocusTraversable(false);
        input.setText("/");
    }

    private void setupInputListeners(LogProgress progress)
    {
        progress.log("Configuring input field event listeners");

        input.textProperty().addListener((obs, o, n) -> updateAutocomplete());
        input.setOnKeyPressed(this::handleKey);

        // Disable tab navigation; instead, apply suggestions
        input.addEventFilter(
                KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.TAB)
                    {
                        applySuggestion(event.isShiftDown() ? -1 : 1);
                        event.consume(); // blocks focus traversal
                    }
                }
        );

        // Restrict allowed characters
        input.addEventFilter(
                KeyEvent.KEY_TYPED, event -> {
                    if (!event.getCharacter().matches(ALLOWED_REGEX))
                    {
                        event.consume();
                    }
                }
        );
    }

    private void setupOutputHolder()
    {
        outputHolder.setContent(outputContainer);
        outputHolder.setFitToWidth(true);
        outputHolder.setPrefHeight(Integer.MAX_VALUE);
        outputHolder.setPadding(new Insets(8));
        outputHolder.setStyle("-fx-background: transparent;" + "-fx-background-color: transparent;" + "-fx-border-color: rgba(255,255,255,0.10);" + "-fx-border-radius: 4;");
        outputHolder.setFitToWidth(true);
        outputHolder.setPannable(true);
    }

    private void setupStatsPanel()
    {
        statsBox.setStyle("-fx-background: transparent;" + "-fx-background-color: transparent;" + "-fx-border-color: rgba(255,255,255,0.10);" + "-fx-border-radius: 4;");
        statsBox.setPadding(new Insets(8));

        String labelStyle = "-fx-text-fill: #00ff00; -fx-font-family: monospace; -fx-font-size: 12px;";
        fpsLabel.setStyle(labelStyle);
        objectsInViewLabel.setStyle(labelStyle);
        totalObjectsLabel.setStyle(labelStyle);

        statsBox.getChildren().addAll(fpsLabel, objectsInViewLabel, totalObjectsLabel);
        statsBox.setVisible(false);
        statsBox.setManaged(false);
    }

    public StackPane createLayer()
    {
        StackPane layer = new StackPane(container);
        StackPane.setAlignment(container, Pos.TOP_LEFT);
        layer.setPickOnBounds(false);
        return layer;
    }

    /* STATE */

    public void toggle()
    {
        open = !open;
        LOGGER.debug("DevConsole is now {}", open ? "Open" : "Closed");
        container.setVisible(open);
        container.setManaged(open);
        if (open)
        {
            input.setText("/");
            input.requestFocus();
            input.positionCaret(input.getText().length());
            updateAutocomplete();
        }

        updateStatsDisplay();
    }

    public boolean isOpen() { return open; }

    /* INPUT */

    public void handleShouldOpen(KeyEvent event)
    {
        if (event.isShiftDown() && event.getCode() == KeyCode.BACK_QUOTE)
        {
            toggle();
            event.consume();
            return;
        }

        if (open) event.consume();
    }

    private void handleKey(KeyEvent event)
    {
        switch (event.getCode())
        {
            case ENTER -> execute();
            case TAB -> applySuggestion(event.isShiftDown() ? -1 : 1);
            case UP -> navigateHistory(-1);
            case DOWN -> navigateHistory(1);
            case ESCAPE -> toggle();
        }
        event.consume();
    }

    /* COMMAND */

    private void execute()
    {
        String text = input.getText().trim();
        if (text.isBlank())
        {
            LOGGER.trace("Empty command entered – ignored");
            return;
        }

        LOGGER.info("DevConsole input: {}", text);

        history.add(text);
        historyIndex = history.size();
        LOGGER.trace("Command added to history (size={})", history.size());

        addLine("> " + text);
        String result = runCommand(text);
        LOGGER.trace("Command result: {}", result);
        addLine(result);
        input.clear();
        updateAutocomplete();
    }

    private String runCommand(String input)
    {
        if (!input.startsWith("/"))
        {
            LOGGER.warn("Invalid command format (missing '/'): {}", input);
            return "Commands must start with '/'";
        }

        String[] words = input.split("\\s+");
        String name = normalizeCommandName(words[0]);

        DevCommand cmd = commands.get(name);
        if (cmd == null)
        {
            LOGGER.warn("Attempted to execute unknown command: {}", name);
            return getUnknownCommandsString();
        }

        List<String> args = Arrays.asList(words).subList(1, words.length);
        LOGGER.trace("Executing command '{}' with {} args: {}", name, args.size(), args);

        try
        {
            LOGGER.trace("Calling executor for command '{}'", name);
            String result = cmd.executor.execute(args);
            LOGGER.debug("Command '{}' executed successfully – result length: {}", name, result.length());
            return result;
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Command '" + name + "' failed to execute", e);
            return "Command failed: " + e.getMessage();
        }
    }

    private String getUnknownCommandsString()
    {
        if (commands.isEmpty()) return "No Commands Registered.";

        return commands.values()
                .stream()
                .map(c -> String.format("- %s: %s", c.usage, c.description))
                .collect(Collectors.joining("\n", "Unknown command.\nRegistered commands:\n", ""));
    }

    /// Adds a line to outputHolder box
    private void addLine(String text)
    {
        Label line = new Label(text);

        line.setStyle("-fx-text-fill: #d6d6d6;" + "-fx-font-family: monospace;" + "-fx-font-size: 12px;");

        line.setWrapText(true);
        line.setMaxWidth(480);

        outputContainer.getChildren().add(line);

        // limit history (prevents memory/UI explosion)
        int maxLines = 50;
        if (outputContainer.getChildren().size() > maxLines)
        {
            outputContainer.getChildren().removeFirst();
            LOGGER.trace("Output line limit reached – removed oldest line");
        }

        // Auto-scroll to bottom
        Platform.runLater(() -> outputHolder.setVvalue(1.1));
    }

    /* REGISTRATION */

    public void register(String name, String usage, String desc, DevCommandExecutor exec)
    {
        String normalized = normalizeCommandName(name);
        LOGGER.trace("Registering command: {}", normalized);
        commands.put(normalized, new DevCommand(name, usage, desc, exec));
    }

    public final void registerDevCommand(String commandName, String usage, String description, DevCommandExecutor executor)
    {
        String normalized = normalizeCommandName(commandName);
        LOGGER.info("Registering dev command: {} (usage: {})", normalized, usage);
        commands.put(normalized, new DevCommand(normalized, usage, description, executor));
    }

    public final void unregisterDevCommand(String commandName)
    {
        String normalized = normalizeCommandName(commandName);
        LOGGER.info("Unregistering dev command: {}", normalized);
        commands.remove(normalized);
    }

    public final List<String> getRegisteredDevCommands()
    {
        return new ArrayList<>(commands.keySet());
    }

    /* SUGGESTIONS & HISTORY */

    private void updateAutocomplete()
    {
        suggestions.clear();
        suggestionIndex = -1;

        String text = input.getText().trim();
        if (!text.startsWith("/"))
        {
            hint.setText("Dev Console");
            LOGGER.trace("No slash prefix – resetting suggestions");
            return;
        }

        String token = normalizeCommandName(text.split("\\s+")[0]);
        LOGGER.trace("Updating autocomplete for token: {}", token);

        for (DevCommand cmd : commands.values())
        {
            if (cmd.name.startsWith(token)) suggestions.add(cmd.usage);
        }

        if (suggestions.isEmpty())
        {
            hint.setText("No matches");
            LOGGER.trace("No suggestions found for token: {}", token);
        }
        else
        {
            suggestionIndex = 0;
            hint.setText(suggestions.getFirst());
            LOGGER.trace("{} suggestions available, first: {}", suggestions.size(), suggestions.getFirst());
        }
    }

    private void applySuggestion(int dir)
    {
        if (suggestions.isEmpty())
        {
            LOGGER.trace("applySuggestion called but no suggestions available");
            return;
        }

        int size = suggestions.size();
        int oldIndex = suggestionIndex;
        suggestionIndex = (suggestionIndex + dir + size) % size;

        String cmd = suggestions.get(suggestionIndex).split("\\s+")[0];
        input.setText(cmd + " ");
        input.positionCaret(input.getText().length());

        LOGGER.trace("Suggestion applied: index {} -> {} / {}, command: {}", oldIndex, suggestionIndex, size, cmd);
    }

    private void navigateHistory(int delta)
    {
        if (history.isEmpty())
        {
            LOGGER.trace("History navigation attempted but history is empty");
            return;
        }

        int oldIndex = historyIndex;
        historyIndex = Math.clamp(historyIndex + delta, 0, history.size());

        if (historyIndex == history.size())
        {
            LOGGER.trace("History moved to end – clearing input");
            input.clear();
            return;
        }

        String cmd = history.get(historyIndex);
        input.setText(cmd);
        input.positionCaret(input.getText().length());
        LOGGER.trace("History navigated: index {} -> {}, command: {}", oldIndex, historyIndex, cmd);
    }

    /* STATIC HELPERS */

    public static boolean parseBooleanArg(String arg)
    {
        String raw = arg.toLowerCase();
        return switch (raw)
        {
            case "true", "1", "on", "yes" -> true;
            default -> false;
        };
    }

    public static String normalizeCommandName(String s)
    {
        if (s == null || s.isBlank()) return "/";
        s = s.toLowerCase(Locale.ROOT);
        return s.startsWith("/") ? s : "/" + s;
    }

    /* STATS */

    public void updateStatsDisplay()
    {
        if (!open)
        {
            statsBox.setVisible(false);
            statsBox.setManaged(false);
            return;
        }

        LOGGER.trace("Updating stats display");

        statsBox.setVisible(true);
        statsBox.setManaged(true);
        try
        {
            if (fpsSupplier != null)
            {
                double fps = fpsSupplier.get();
                fpsLabel.setText(String.format("FPS: %.0f", fps));
                LOGGER.trace("FPS updated: {}", fps);
            }
            if (objectsInViewSupplier != null)
            {
                int oiv = objectsInViewSupplier.get();
                objectsInViewLabel.setText("GOiV: " + oiv);
                LOGGER.trace("GOiV updated: {}", oiv);
            }
            if (totalObjectsSupplier != null)
            {
                int tgo = totalObjectsSupplier.get();
                totalObjectsLabel.setText("TGO: " + tgo);
                LOGGER.trace("TGO updated: {}", tgo);
            }
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to update stats display", e);
        }
    }

    public DevConsole setFpsSupplier(Supplier<Double> fpsSupplier)
    {
        this.fpsSupplier = fpsSupplier;
        return this;
    }

    public DevConsole setObjectsInViewSupplier(Supplier<Integer> supplier)
    {
        this.objectsInViewSupplier = supplier;
        return this;
    }

    public DevConsole setTotalObjectsSupplier(Supplier<Integer> supplier)
    {
        this.totalObjectsSupplier = supplier;
        return this;
    }

    /* UTILITIES */

    @FunctionalInterface
    public interface DevCommandExecutor
    {
        String execute(List<String> args);
    }

    private record DevCommand(String name, String usage, String description, DevCommandExecutor executor) { }
}