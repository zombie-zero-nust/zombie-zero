package edu.nust.engine.core;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.*;

public class DevConsole
{
    private final VBox container = new VBox(6);
    private final Label hint = new Label();
    private final TextField input = new TextField();

    private final Map<String, DevCommand> commands = new LinkedHashMap<>();
    private final List<String> suggestions = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    private int historyIndex = -1;
    private int suggestionIndex = -1;

    private boolean open = false;

    public DevConsole() { setupUI(); }

    /* UI */

    private void setupUI()
    {
        container.setVisible(false);
        container.setManaged(false);
        container.getStyleClass().add("dev-console");
        container.setMaxWidth(520);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: rgba(20,20,20,0.92);" + "-fx-border-color: rgba(255,255,255,0.22);" + "-fx-border-width: 1;");

        hint.setText("Dev Console");
        hint.setStyle("-fx-text-fill: #b8e0ff;");

        input.setPromptText("Type a command");
        input.setFocusTraversable(false);
        input.setText("/");

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
                    if (!ch.matches("[a-zA-Z0-9/ ]"))
                    {
                        event.consume();
                    }
                }
        );

        container.getChildren().setAll(hint, input);
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

        history.add(text);
        historyIndex = history.size();

        hint.setText(runCommand(text));
        input.clear();
        updateAutocomplete();
    }

    private String runCommand(String input)
    {
        if (!input.startsWith("/")) return "Commands must start with '/'";

        String[] words = input.split("\\s+");
        String name = normalizeCommandName(words[0]);

        DevCommand cmd = commands.get(name);
        if (cmd == null) return "Unknown command";

        List<String> args = Arrays.asList(words).subList(1, words.length);

        try { return cmd.executor.execute(args); }
        catch (Exception e) { return "Command failed: " + e.getMessage(); }
    }

    /* REGISTRATION */

    public void register(String name, String usage, String desc, DevCommandExecutor exec)
    {
        commands.put(normalizeCommandName(name), new DevCommand(name, usage, desc, exec));
    }

    public final void registerDevCommand(String commandName, String usage, String description, DevCommandExecutor executor)
    {
        String normalized = normalizeCommandName(commandName);
        commands.put(normalized, new DevCommand(normalized, usage, description, executor));
    }

    public final void unregisterDevCommand(String commandName)
    {
        commands.remove(normalizeCommandName(commandName));
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