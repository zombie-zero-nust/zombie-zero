package edu.nust.game.scenes.highscores;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.scenes.highscores.highscores.HighScoreEntry;
import edu.nust.game.scenes.highscores.highscores.HighScoreStorage;
import edu.nust.game.scenes.start.StartScene;
import edu.nust.game.systems.audio.MusicManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class HighScoresScene extends GameScene
{
    private static final int MAX_ROWS = 5;

    @FXML private Label bannerLabel;
    @FXML private Label bannerSubLabel;
    @FXML private VBox scoreRowsContainer;

    public HighScoresScene(GameWorld world)
    {
        super(world);
    }

    @Override
    public void onInit()
    {
        if (!MusicManager.isMenuMusicPlaying())
            MusicManager.ensureMenuMusicPlaying();

        List<HighScoreEntry> topScores = HighScoreStorage.loadTop(MAX_ROWS);
        updateBanner(topScores);
        populateRows(topScores);
    }

    private void updateBanner(List<HighScoreEntry> topScores)
    {
        if (bannerLabel == null || bannerSubLabel == null)
            return;

        if (topScores.isEmpty())
        {
            bannerLabel.setText("#1 ---");
            bannerSubLabel.setText("No highscores yet");
            return;
        }

        HighScoreEntry best = topScores.getFirst();
        bannerLabel.setText("#1 " + best.name());
        bannerSubLabel.setText("Score: " + best.score() + "   |   " + best.timestamp()
                .format(HighScoreStorage.TIMESTAMP_FORMAT));
    }

    private void populateRows(List<HighScoreEntry> topScores)
    {
        if (scoreRowsContainer == null)
            return;

        scoreRowsContainer.getChildren().clear();

        for (int i = 0; i < topScores.size(); i++)
        {
            HighScoreEntry entry = topScores.get(i);
            HBox row = new HBox();
            row.setSpacing(0);
            row.getStyleClass().add("score-row");

            Label rank = createCell("#" + (i + 1), "col-rank");
            Label name = createCell(entry.name(), "col-name");
            Label score = createCell(String.valueOf(entry.score()), "col-score");
            Label timestamp = createCell(entry.timestamp().format(HighScoreStorage.TIMESTAMP_FORMAT), "col-time");

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
