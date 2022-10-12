package nz.ac.auckland.se206.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.daos.GameDao;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.models.UserModel;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the controller of the canvas. You are free to modify this class and the corresponding
 * FXML file as you see fit. For example, you might no longer need the "Predict" button because the
 * DL model should be automatically queried in the background every second.
 *
 * <p>!! IMPORTANT !!
 *
 * <p>Although we added the scale of the image, you need to be careful when changing the size of the
 * drawable canvas and the brush size. If you make the brush too big or too small with respect to
 * the canvas size, the ML model will not work correctly. So be careful. If you make some changes in
 * the canvas and brush sizes, make sure that the prediction works fine.
 */
public class CanvasController implements Controller {

  @FXML private ListView<String> lvwPredictions;
  @FXML private Canvas canvas;
  @FXML private Pane canvasPane;
  @FXML private Label lblTimer;
  @FXML private Label lblCategory;
  @FXML private Label eraserMessage;
  @FXML private Label progressMessage;
  @FXML private Button clearButton;
  @FXML private Button btnSave;
  @FXML private Button btnReturnToMenu;
  @FXML private ToggleButton btnToggleEraser;
  @FXML private ToggleButton btnNewGame;
  @FXML private HBox hbxGameEnd;
  @FXML private HBox hbxDrawTools;
  @FXML private HBox hbxNewGame;

  private Timeline timer;
  private ObservableList<String> predictions;
  private String category;

  private GraphicsContext graphic;
  private DoodlePrediction model;
  private Boolean isFinished;

  // mouse coordinates
  private double currentX;
  private double currentY;

  // database tools
  private GameDao gameDao = new GameDao();
  private int activeUserId;
  private int activeGameId;
  private CategorySelect.Difficulty actualDifficulty;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   */
  public void initialize() throws ModelException, IOException {

    predictions = FXCollections.observableArrayList();
    lvwPredictions.setItems(predictions);
    hbxGameEnd.setVisible(false);
    hbxNewGame.setVisible(false);

    graphic = canvas.getGraphicsContext2D();

    // save coordinates when mouse is pressed on the canvas
    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
        });

    switchToPen();

    model = new DoodlePrediction();

    // set default canvas border color
    canvasPane.getStyleClass().add("end-game");
    progressMessage.getStyleClass().add("defaultMessage");
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear() {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  private BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  public void initializeGame() {
    resetGame();
    btnNewGame.fire();
  }

  public void startTimer() throws SQLException {
    // set up the label and enable canvas
    isFinished = false;
    canvas.setDisable(false);
    hbxDrawTools.setVisible(true);
    category = CategorySelect.getCategory();
    lblCategory.setText("Draw: " + category);
    // create new game database object
    activeUserId = UserModel.getActiveUser().getId();
    activeGameId = gameDao.addNewGame(activeUserId, actualDifficulty, category);
    // set up what to do every second
    timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> changeTime()));
    timer.setCycleCount(60);
    timer.setOnFinished(
        e -> {
          endGame(false);
        }); // if the timer runs to zero
    timer.play();
    runPredictionsInBkg();
  }

  private void changeTime() {
    lblTimer.setText(String.valueOf(Integer.valueOf(lblTimer.getText()) - 1));
  }

  private void triggerPredict() {

    if (isFinished) {
      return;
    }

    predictions.clear();

    // all predictions
    List<Classifications.Classification> rawPredictions = null;

    try {
      rawPredictions = model.getPredictions(getCurrentSnapshot(), 345);
    } catch (TranslateException e) {
      System.out.println("Translate Exception when getting predictions");
      System.exit(-1);
    }

    int i = 0;

    // add the retrieved predictions to the observable list
    for (final Classifications.Classification classification : rawPredictions) {
      if (i < 10) {
        String formattedPrediction = formatPrediction(classification, i);
        // add the retrieved predictions to the observable list
        predictions.add(formattedPrediction);
      }

      // format the string and replace the underscores with a space
      String categoryClass = classification.getClassName().replace('_', ' ');
      if (category.equals(categoryClass)) {
        // check if player won (guess is correct within the top three)
        checkCategoryPosition(i);
      }
      i++;
    }
  }

  private String formatPrediction(Classification classification, int index) {
    StringBuilder sb = new StringBuilder();
    // format the string and replace the underscores with a space
    sb.append(index + 1)
        .append(" : ")
        .append(classification.getClassName().replace('_', ' '))
        .append(" : ")
        .append(String.format("%d%%", Math.round(100 * classification.getProbability())));
    return sb.toString();
  }

  private void checkCategoryPosition(int position) {
    // this determines which style class to use
    String pseudoClass = null;
    String messageClass = null;
    // remove border color when cateogry is outside any ranking
    canvasPane.getStyleClass().clear();
    progressMessage.getStyleClass().clear();
    ;
    // change the border color depending on its ranking
    if (position < 3) {
      endGame(true);
    } else if (position < 10) {
      pseudoClass = "top10";
      messageClass = "message10";
      progressMessage.setText("You're almost there!");
    } else if (position < 20) {
      pseudoClass = "top20";
      messageClass = "message20";
      progressMessage.setText("Getting closer!");
    } else if (position < 50) {
      pseudoClass = "top50";
      messageClass = "message50";
      progressMessage.setText("You're getting close!");
    } else if (position < 100) {
      pseudoClass = "top100";
      messageClass = "message100";
      progressMessage.setText("Looking good!");
    } else {
      pseudoClass = "end-game";
      messageClass = "defaultMessage";
      progressMessage.setText("Keep drawing!");
    }
    // set color to border
    canvasPane.getStyleClass().add(pseudoClass);
    progressMessage.getStyleClass().add(messageClass);
  }

  private void endGame(Boolean wonGame) {
    // lock the drawing and stop timer
    canvas.setOnMouseDragged(e -> {});
    timer.pause();
    canvas.setDisable(true);
    hbxDrawTools.setVisible(false);
    hbxGameEnd.setVisible(true);
    hbxNewGame.setVisible(true);
    isFinished = true;

    // update current game stats
    gameDao.setWon(wonGame, activeGameId);
    gameDao.setTime((60 - Integer.valueOf(lblTimer.getText())), activeGameId);

    // set the label to win/lose event and use the tts
    if (wonGame) {
      progressMessage.setText("You Win!");
      TextToSpeech.main(new String[] {"You Win!"});
      progressMessage.getStyleClass().clear();
      progressMessage.getStyleClass().add("winMessage");
      canvasPane.getStyleClass().clear();
      canvasPane.getStyleClass().add("top3");
    } else {
      progressMessage.setText("You Lose!");
      TextToSpeech.main(new String[] {"You Lose!"});
      progressMessage.getStyleClass().clear();
      progressMessage.getStyleClass().add("lossMessage");
      canvasPane.getStyleClass().clear();
      canvasPane.getStyleClass().add("loss");
    }
  }

  /**
   * Save the current snapshot on a bitmap file in the location specified by the user.
   *
   * @throws IOException If the image cannot be saved.
   */
  @FXML
  private void onSave(ActionEvent event) {
    // set up file chooser
    FileChooser fileChooser = new FileChooser();
    fileChooser
        .getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.bmp"));
    fileChooser.setInitialFileName(category);

    // get directory and name of the new file
    File fileToSave =
        fileChooser.showSaveDialog(((Button) event.getSource()).getScene().getWindow());

    // save the file to the selected directory and name (if selected)
    try {
      ImageIO.write(getCurrentSnapshot(), "bmp", fileToSave);
      lblCategory.setText("File Saved!");
    } catch (Exception e) {
      // if the file save fails, tell the user.
      lblCategory.setText("Save cancelled.");
    }
  }

  @FXML
  private void onReturnToMenu(ActionEvent event) {
    resetGame();
    hbxNewGame.setVisible(false);

    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.CATEGORY_SELECT));

    // repeat instructions
    TextToSpeech.main(new String[] {"Pick a difficulty"});
  }

  private void resetGame() {
    // reset timer and re-enable the drawing tools
    switchToPen();
    eraserMessage.setText("Eraser OFF");
    btnToggleEraser.setSelected(false);
    lblTimer.setText("60");
    hbxGameEnd.setVisible(false);
    hbxDrawTools.setVisible(false);
    hbxNewGame.setVisible(true);
    predictions.clear();
    canvas.setDisable(true);
    onClear();
    // reset conditional border color rendering
    canvasPane.getStyleClass().add("end-game");
    progressMessage.getStyleClass().clear();
    progressMessage.getStyleClass().add("defaultMessage");
  }

  @FXML
  private void onToggleEraser() {

    // Switching between pen and eraser
    if (btnToggleEraser.isSelected()) {
      // Changing label
      eraserMessage.setText("Eraser ON");
      switchToEraser();
    } else {
      // Changing label
      eraserMessage.setText("Eraser OFF");
      switchToPen();
    }
  }

  @FXML
  private void onNewGame() throws SQLException {
    if (btnNewGame.isSelected()) {
      // clear the canvas and timer
      resetGame();
      btnNewGame.setText("Start Game");
      progressMessage.setText("Get ready to start!");
      // generate a new word
      actualDifficulty = CategorySelect.generateSetCategory();
      category = CategorySelect.getCategory();
      lblCategory.setText("Draw: " + category);
      TextToSpeech.main(new String[] {"Your word is:" + category});

    } else {
      TextToSpeech.main(new String[] {"Let's draw"});
      // start the game and hide the new game toolbar
      hbxNewGame.setVisible(false);
      btnNewGame.setText("Play Again?");
      startTimer();
    }
  }

  private void switchToPen() {
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 5;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.setLineWidth(size);

          // Create a line that goes from the point (currentX, currentY) and (x,y)
          graphic.strokeLine(currentX, currentY, x, y);

          // update the coordinates
          currentX = x;
          currentY = y;
        });
  }

  private void switchToEraser() {
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 20;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // clear in the area specified
          graphic.clearRect(x, y, size, size);

          // update the coordinates
          currentX = x;
          currentY = y;
        });
  }

  private void runPredictionsInBkg() {
    // run the predictions on a background thread to avoid lag
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // run predictions for as long as the game is not considered finished
            while (!isFinished) {
              doPredict();
            }
            return null;
          }

          private void doPredict() {

            // add time delay of one second
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            }

            // run the prediction function as a 'run later' so that the page updates
            Platform.runLater(
                () -> {
                  triggerPredict();
                });
          }
        };

    // start the thread
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }
}
