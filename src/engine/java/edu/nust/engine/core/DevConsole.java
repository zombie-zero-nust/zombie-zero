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

    private final Map<String, DevCommand> commands = new LinkedHashMap<>();
    private final List<String> suggestions = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    private int historyIndex = -1;
    private int suggestionIndex = -1;

    private boolean open = false;

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

        container.setVisible(false);
        container.setManaged(false);
        container.getStyleClass().add("dev-console");
        container.setSpacing(8);
        container.setMaxWidth(520);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: rgba(15,15,15,0.95);" + "-fx-border-color: rgba(255,255,255,0.15);" + "-fx-border-width: 1;" + "-fx-background-radius: 6;" + "-fx-border-radius: 6;");

        hint.setText("Dev Console");
        hint.setStyle("-fx-text-fill: #9aa4ad;" + "-fx-font-size: 12px;");

        input.setStyle("-fx-background-color: rgba(30,30,30,0.9);" + "-fx-text-fill: #e6e6e6;" + "-fx-prompt-text-fill: #666666;" + "-fx-border-color: rgba(255,255,255,0.10);" + "-fx-border-radius: 3;" + "-fx-background-radius: 3;");
        input.setPromptText("Type a command");
        input.setFocusTraversable(false);
        input.setText("/");

        LOGGER.trace("Configuring input field event listeners");
        input.textProperty().addListener((obs, o, n) -> updateAutocomplete());
        input.setOnKeyPressed(this::handleKey);

        // disable tab navigation
        input.addEventFilter(
                KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.TAB)
                    {
                        applySuggestion(event.isShiftDown() ? -1 : 1);
                        event.consume(); // blocks focus traversal
                    }
                }
        );

        input.addEventFilter(
                KeyEvent.KEY_TYPED, event -> {
                    String ch = event.getCharacter();

                    // allow only alphanumeric, slash and space
                    if (!ch.matches(ALLOWED_REGEX))
                    {
                        event.consume();
                    }
                }
        );

        LOGGER.trace("Configuring output holder and constraints");
        outputHolder.setContent(outputContainer);
        outputHolder.setFitToWidth(true);
        outputHolder.setPrefHeight(Integer.MAX_VALUE);
        outputHolder.setPadding(new Insets(8));
        outputHolder.setStyle("-fx-background: transparent;" + "-fx-background-color: transparent;" + "-fx-border-color: rgba(255,255,255,0.10);" + "-fx-border-radius: 4;");

        outputHolder.setFitToWidth(true);
        outputHolder.setPannable(true);

        container.getChildren().setAll(hint, input, outputHolder);

        Platform.runLater(() -> outputHolder.setVvalue(1.0));

        initConsoleLogger.end("DevConsole UI initialized successfully");
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
        LOGGER.debug("DevConsole is now {}", open ? "OPEN" : "CLOSED");
        container.setVisible(open);
        container.setManaged(open);
        if (open)
        {
            input.setText("/");
            input.requestFocus();
            input.positionCaret(input.getText().length());
            updateAutocomplete();
        }
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
        if (text.isBlank()) return;

        LOGGER.info("DevConsole input: {}", text);

        history.add(text);
        historyIndex = history.size();

        addLine("> " + text);
        addLine(runCommand(text));
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

        try
        {
            LOGGER.trace("Executing command '{}' with args: {}", name, args);
            String result = cmd.executor.execute(args);
            LOGGER.debug("Command '{}' executed successfully", name);
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
        LOGGER.trace("Registering dev command: {}", normalized);
        commands.put(normalized, new DevCommand(normalized, usage, description, executor));
    }

    public final void unregisterDevCommand(String commandName)
    {
        String normalized = normalizeCommandName(commandName);
        LOGGER.trace("Unregistering dev command: {}", normalized);
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
            return;
        }

        String token = normalizeCommandName(text.split("\\s+")[0]);

        for (DevCommand cmd : commands.values())
        {
            if (cmd.name.startsWith(token)) suggestions.add(cmd.usage);
        }

        if (suggestions.isEmpty())
        {
            hint.setText("No matches");
        }
        else
        {
            suggestionIndex = 0;
            hint.setText(suggestions.getFirst());
        }
    }

    private void applySuggestion(int dir)
    {
        if (suggestions.isEmpty()) return;

        int size = suggestions.size();
        suggestionIndex = (suggestionIndex + dir + size) % size;

        String cmd = suggestions.get(suggestionIndex).split("\\s+")[0];
        input.setText(cmd + " ");
        input.positionCaret(input.getText().length());
    }

    private void navigateHistory(int delta)
    {
        if (history.isEmpty()) return;

        historyIndex = Math.clamp(historyIndex + delta, 0, history.size());

        if (historyIndex == history.size())
        {
            input.clear();
            return;
        }

        input.setText(history.get(historyIndex));
        input.positionCaret(input.getText().length());
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

    /* UTILITIES */

    @FunctionalInterface
    public interface DevCommandExecutor
    {
        String execute(List<String> args);
    }

    private record DevCommand(String name, String usage, String description, DevCommandExecutor executor) { }
}