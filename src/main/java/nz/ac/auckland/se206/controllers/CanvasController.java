package nz.ac.auckland.se206.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.ml.DoodlePrediction;
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
  @FXML private Label lblTimer;
  @FXML private Label lblCategory;
  @FXML private Button clearButton;
  @FXML private Button btnSave;
  @FXML private Button btnNewGame;
  @FXML private Button btnExitGame;
  @FXML private ToggleButton btnToggleEraser;
  @FXML private HBox hbxGameEnd;
  @FXML private HBox hbxDrawTools;
  private Timeline timer;
  private ObservableList<String> predictions;
  private String category;

  private GraphicsContext graphic;
  private TextToSpeech textToSpeech = new TextToSpeech();
  private DoodlePrediction model;

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

    graphic = canvas.getGraphicsContext2D();

    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 5.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.fillOval(x, y, size, size);
        });

    model = new DoodlePrediction();
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

  public void startTimer() {
    // set up the label
    category = CategorySelect.getCategory();
    lblCategory.setText("Draw: " + category);
    // set up what to do every second
    timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> changeTime()));
    timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> triggerPredict()));
    timer.setCycleCount(60);
    timer.setOnFinished(e -> endGame(false)); // if the timer runs to zero
    timer.play();
  }

  private void changeTime() {
    lblTimer.setText(String.valueOf(Integer.valueOf(lblTimer.getText()) - 1));
  }

  private void triggerPredict() {
    predictions.clear();

    List<Classifications.Classification> rawPredictions = null;

    // get the predictions
    try {
      rawPredictions = model.getPredictions(getCurrentSnapshot(), 10);
    } catch (TranslateException e) {
      System.out.println("Translate Exception when getting predictions");
      System.exit(-1);
    }

    StringBuilder sb = new StringBuilder();

    int i = 1;

    // add the retrieved predictions to the observable list
    for (final Classifications.Classification classification : rawPredictions) {
      sb.setLength(0);
      // format the string and replace the underscores with a space
      sb.append(i)
          .append(" : ")
          .append(classification.getClassName().replace('_', ' '))
          .append(" : ")
          .append(String.format("%.2f%%", 100 * classification.getProbability()));
      predictions.add(sb.toString());

      // check if player won (guess is correct within the top three)
      if ((i < 4) && (category.equals(classification.getClassName().replace('_', ' ')))) {
        endGame(true);
      }

      i++;
    }
  }

  private void endGame(Boolean wonGame) {
    // lock the drawing and stop timer
    timer.pause();
    canvas.setDisable(true);
    hbxDrawTools.setVisible(false);
    hbxGameEnd.setVisible(true);

    // set the label to win/lose event
    if (wonGame) {
      lblCategory.setText("You Win!");
    } else {
      lblCategory.setText("You Lose!");
    }

    // run the text to speech on a background thread to avoid lag
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            if (wonGame) {
              textToSpeech.speak("You Win!");
            } else {
              textToSpeech.speak("You Lose!");
            }
            return null;
          }
        };

    // start the thread
    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
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
  private void onNewGame(ActionEvent event) {
    resetGame();

    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.CATEGORY_SELECT));

    // run the text to speech on a background thread to avoid lag
    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {
            // repeat the instructions from the start of the game
            textToSpeech.speak("Pick a category.");
            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }

  private void resetGame() {
    // reset timer and re-enable the canvas and drawing tools
    lblTimer.setText("60");
    canvas.setDisable(false);
    hbxDrawTools.setVisible(true);
    hbxGameEnd.setVisible(false);
    predictions.clear();
    onClear();
  }

  @FXML
  private void onExitGame() {
    System.exit(0);
  }

  @FXML
  private void onToggleEraser() {

    // change label
    if (btnToggleEraser.isSelected()) {
      btnToggleEraser.setText("Pen");
    } else {
      btnToggleEraser.setText("Eraser");
    }

    // change pen color
    canvas.setOnMouseDragged(
        e -> {
          // keep existing settings
          final double size = 5.0;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // change color depending on toggle
          if (btnToggleEraser.isSelected()) {
            graphic.setFill(Color.WHITE);
          } else {
            graphic.setFill(Color.BLACK);
          }

          graphic.fillOval(x, y, size, size);
        });
  }
}