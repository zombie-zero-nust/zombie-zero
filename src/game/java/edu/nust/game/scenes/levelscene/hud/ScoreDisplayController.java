package edu.nust.game.scenes.levelscene.hud;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;

/**
 * ScoreDisplayController - Dynamic 3-digit score display for Game Over screen
 * <p>
 * Features:
 * - Displays score as 3-digit number with zero-padding (e.g., 7 displays as "007")
 * - Uses separate scorePanel.png (background) and NumeralSpriteSheet.png (digits)
 * - Overlays digits ON TOP of the score panel, positioned to the RIGHT
 * - Pixel-perfect rendering with no image smoothing
 * - Supports scores from 0 to 999
 */
public class ScoreDisplayController extends StackPane
{
    private static final GameLogger logger = GameLogger.getLogger(ScoreDisplayController.class);

    // Asset paths (relative to assets/images/GameOver)
    private static final String SCORE_PANEL_FILENAME = "scorePanel.png";
    private static final String NUMERAL_SHEET_FILENAME = "NumeralSpriteSheet.png";

    // Sprite sheet dimensions for numerals (actual measured from sprite sheet)
    // 992px wide / 10 digits = ~99.2px per digit width, 132px height
    private static final int DIGIT_WIDTH = 99;
    private static final int DIGIT_HEIGHT = 132;

    // UI Components
    private ImageView scorePanelView;
    private ImageView[] digitImageViews;
    private Image numeralSpriteSheet;
    private HBox digitsContainer;

    /**
     * Constructor - Initialize the score display
     */
    public ScoreDisplayController()
    {
        logger.debug("Initializing ScoreDisplayController");
        this.setStyle("-fx-image-smoothing: false;");

        digitImageViews = new ImageView[3];

        loadNumeralSpriteSheet();
        createScorePanelBackground();
        createDigitContainer();
        updateScoreDisplay(0);
    }

    /**
     * Load the numeral sprite sheet containing digits 0-9
     */
    private void loadNumeralSpriteSheet()
    {
        try
        {
            URL numeralUrl = Resources.tryGetResource("assets", "images", "GameOver", NUMERAL_SHEET_FILENAME);
            if (numeralUrl != null)
            {
                numeralSpriteSheet = new Image(numeralUrl.toExternalForm());
                logger.debug("Numeral sprite sheet loaded: {}x{}",
                        numeralSpriteSheet.getWidth(), numeralSpriteSheet.getHeight());
            }
            else
            {
                logger.debug("Failed to load numeral sprite sheet: {} - URL is null", NUMERAL_SHEET_FILENAME);
            }
        }
        catch (Exception e)
        {
            logger.debug("Exception loading numeral sprite sheet: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create the score panel background (static image with "SCORE:" text)
     */
    private void createScorePanelBackground()
    {
        try
        {
            URL panelUrl = Resources.tryGetResource("assets", "images", "GameOver", SCORE_PANEL_FILENAME);
            if (panelUrl != null)
            {
                Image panelImage = new Image(panelUrl.toExternalForm());
                scorePanelView = new ImageView(panelImage);
                scorePanelView.setStyle("-fx-image-smoothing: false;");
                scorePanelView.setPreserveRatio(true);
                scorePanelView.setFitHeight(100);

                this.getChildren().add(scorePanelView);
                logger.debug("Score panel background created: {}", SCORE_PANEL_FILENAME);
            }
            else
            {
                logger.debug("Failed to load score panel: {} - URL is null", SCORE_PANEL_FILENAME);
            }
        }
        catch (Exception e)
        {
            logger.debug("Exception creating score panel background: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create the container for digit displays.
     *
     * Panel renders at fitHeight=100 with preserved ratio.
     * The original scorePanel.png has "SCORE:" filling ~65% of its width,
     * leaving the right ~35% for digits.
     *
     * We anchor from CENTER_LEFT and use a left margin to skip past "SCORE:".
     *
     * Tuning guide (only touch these two values):
     *   marginLeft : move digits LEFT (smaller) or RIGHT (larger)
     *   fitHeight  : make digits SMALLER or LARGER
     */
    private void createDigitContainer()
    {
        digitsContainer = new HBox();
        digitsContainer.setStyle("-fx-image-smoothing: false;");
        digitsContainer.setSpacing(4);
        digitsContainer.setAlignment(Pos.CENTER);

        // fitHeight=68 makes digits roughly as tall as the "SCORE:" lettering.
        // fitWidth = fitHeight * (99/132) to preserve the sprite's aspect ratio.
        final double FIT_HEIGHT = 68;
        final double FIT_WIDTH  = FIT_HEIGHT * 99.0 / 132.0; // ≈ 51

        for (int i = 0; i < 3; i++)
        {
            ImageView digitView = new ImageView();
            digitView.setSmooth(false);
            digitView.setStyle("-fx-image-smoothing: false;");
            digitView.setPreserveRatio(false);
            digitView.setFitHeight(FIT_HEIGHT);
            digitView.setFitWidth(FIT_WIDTH);

            digitImageViews[i] = digitView;
            digitsContainer.getChildren().add(digitView);
        }

        // Anchor to CENTER_LEFT, then push right past "SCORE:" with left margin.
        // marginLeft=240 puts the digit block just after the colon.
        // → digits still overlap "SCORE:"? increase marginLeft (e.g. 260, 270)
        // → digits pushed off the right edge?  decrease marginLeft (e.g. 220, 210)
        StackPane.setAlignment(digitsContainer, Pos.CENTER_LEFT);
        StackPane.setMargin(digitsContainer, new Insets(0, 0, 0, 240));

        this.getChildren().add(digitsContainer);
        logger.debug("Digit container: marginLeft=240, fitHeight={}, fitWidth={}, spacing=4",
                FIT_HEIGHT, FIT_WIDTH);
    }

    /**
     * Update the score display with a new score value.
     *
     * @param currentScore The score to display (0-999)
     */
    public void updateScoreDisplay(int currentScore)
    {
        if (numeralSpriteSheet == null)
        {
            logger.warn("Numeral sprite sheet not loaded, cannot update score display");
            System.err.println("ERROR: numeralSpriteSheet is NULL!");
            return;
        }

        System.out.println("=== SCORE DISPLAY DEBUG ===");
        System.out.println("Sheet URL:  " + numeralSpriteSheet.getUrl());
        System.out.println("Sheet size: " + numeralSpriteSheet.getWidth() + "x" + numeralSpriteSheet.getHeight());

        int score = Math.min(currentScore, 999);
        String paddedScore = String.format("%03d", score);
        logger.debug("Updating score display: {} (from {})", paddedScore, currentScore);

        for (int i = 0; i < 3; i++)
        {
            int digitValue = Character.getNumericValue(paddedScore.charAt(i));
            int spriteX    = digitValue * 99;

            Rectangle2D viewport = new Rectangle2D(spriteX, 0, 99.0, 132.0);

            ImageView digitView = digitImageViews[i];
            digitView.setImage(numeralSpriteSheet);
            digitView.setViewport(viewport);
            digitView.setSmooth(false);
            digitView.setStyle("-fx-image-smoothing: false;");

            System.out.printf("  Digit[%d] value=%d  spriteX=%d  viewport=%s%n",
                    i, digitValue, spriteX, viewport);

            logger.debug("Digit {}: value={}, spriteX={}, viewport={}", i, digitValue, spriteX, viewport);
        }

        System.out.println("=== END DEBUG ===");
    }

    /** @return The background panel ImageView */
    public ImageView getBackgroundPanel()
    {
        return scorePanelView;
    }

    /** @return The HBox containing the three digit ImageViews */
    public HBox getDigitsContainer()
    {
        return digitsContainer;
    }
}