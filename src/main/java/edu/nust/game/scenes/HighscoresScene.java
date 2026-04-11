package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.highscores.HighscoreEntry;
import edu.nust.game.highscores.HighscoreStore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class HighscoresScene extends GameScene
{
    private static final int MAX_ROWS = 10;

    @FXML private Label bannerLabel;
    @FXML private Label bannerSubLabel;
    @FXML private VBox scoreRowsContainer;

    public HighscoresScene(GameWorld world)
    {
        super(world);
    }

    @Override
    public void onInit()
    {
        List<HighscoreEntry> topScores = HighscoreStore.loadTop(MAX_ROWS);
        updateBanner(topScores);
        populateRows(topScores);
    }

    private void updateBanner(List<HighscoreEntry> topScores)
    {
        if (bannerLabel == null || bannerSubLabel == null)
            return;

        if (topScores.isEmpty())
        {
            bannerLabel.setText("#1 ---");
            bannerSubLabel.setText("No highscores yet");
            return;
        }

        HighscoreEntry best = topScores.get(0);
        bannerLabel.setText("#1 " + best.getName());
        bannerSubLabel.setText("Score: " + best.getScore() + "   |   " + best.getTimestamp().format(HighscoreStore.TIMESTAMP_FORMAT));
    }

    private void populateRows(List<HighscoreEntry> topScores)
    {
        if (scoreRowsContainer == null)
            return;

        scoreRowsContainer.getChildren().clear();

        for (int i = 0; i < topScores.size(); i++)
        {
            HighscoreEntry entry = topScores.get(i);
            HBox row = new HBox();
            row.setSpacing(8);
            row.getStyleClass().add("score-row");

            Label rank = createCell("#" + (i + 1), "col-rank");
            Label name = createCell(entry.getName(), "col-name");
            Label score = createCell(String.valueOf(entry.getScore()), "col-score");
            Label timestamp = createCell(entry.getTimestamp().format(HighscoreStore.TIMESTAMP_FORMAT), "col-time");

            row.getChildren().addAll(rank, name, score, timestamp);
            scoreRowsContainer.getChildren().add(row);
        }

        if (topScores.isEmpty())
        {
            Label empty = new Label("No entries yet.");
            empty.getStyleClass().add("empty-row");
            scoreRowsContainer.getChildren().add(empty);
        }
    }

    private Label createCell(String text, String styleClass)
    {
        Label label = new Label(text);
        label.getStyleClass().addAll("score-cell", styleClass);
        HBox.setHgrow(label, Priority.NEVER);
        return label;
    }

    @FXML
    private void backToMainMenu()
    {
        this.getWorld().setScene(new StartScene(this.getWorld()));
    }
}



