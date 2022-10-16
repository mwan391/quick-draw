package nz.ac.auckland.se206.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import animatefx.animation.Pulse;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.BadgeManager;
import nz.ac.auckland.se206.CategorySelect;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.daos.GameDao;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.dictionary.DictionaryLookup;
import nz.ac.auckland.se206.dictionary.WordInfo;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.models.GameSettingModel;
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
  @FXML private Pane timeBg;
  @FXML private Pane canvasPane;
  @FXML private Label lblTimer;
  @FXML private Label timeLeft;
  @FXML private Label lblCategory;
  @FXML private Label lblDefinition;
  @FXML private Label eraserMessage;
  @FXML private Label progressMessage;
  @FXML private Label hintMessage;
  @FXML private Button clearButton;
  @FXML private Button btnSave;
  @FXML private Button btnReturnToMenu;
  @FXML private Button btnHint;
  @FXML private ToggleButton btnToggleEraser;
  @FXML private ToggleButton btnNewGame;
  @FXML private HBox hbxGameEnd;
  @FXML private HBox hbxDrawTools;
  @FXML private HBox hbxNewGame;
  @FXML private ColorPicker zenPicker;

  private Timeline timer;
  private ObservableList<String> predictions;
  private String category;
  private GraphicsContext graphic;
  private DoodlePrediction model;
  private Boolean isFinished;

  // Alternative modes
  public Boolean isHidden = false;
  public Boolean isZen = false;
  private Boolean zenWin = false;
  private Color selectedPen;

  // mouse coordinates
  private double currentX;
  private double currentY;

  // database tools
  private GameDao gameDao = new GameDao();
  private String activeUserId;
  private int activeGameId;
  private CategorySelect.Difficulty actualDifficulty;

  // settings
  private int time;
  private int accuracy;
  private int confidence;

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

    selectedPen = Color.BLACK;
    switchToPen();

    model = new DoodlePrediction();

    // set default canvas border color
    canvasPane.getStyleClass().add("end-game");
    progressMessage.getStyleClass().add("defaultMessage");
  }

  /** This method is called when the "Clear" button is pressed. */
  @FXML
  private void onClear(ActionEvent event) {
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    if (event != null) {
      SoundManager.playSound();
    }
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

  /**
   * This method clears the scene and presses the new game button once to simulate a new game screen
   * and applies the selected user settings
   */
  public void initializeGame() {
    // get settings
    activeUserId = UserModel.getActiveUser().getId();
    GameSettingDao settingDao = new GameSettingDao();
    GameSettingModel userSettings = settingDao.get(activeUserId);

    zenPicker.setVisible(false);
    // disable timer in zen mode
    if (isZen) {
      timeBg.setVisible(false);
      timeLeft.setVisible(false);
      lblTimer.setVisible(false);
    } else {
      timeBg.setVisible(true);
      timeLeft.setVisible(true);
      lblTimer.setVisible(true);
    }

    // set time settings
    CategorySelect.Difficulty timeDifficulty =
        CategorySelect.Difficulty.valueOf(userSettings.getTime());
    // set time according to the setting
    switch (timeDifficulty) {
      case EASY:
        time = 60;
        break;
      case MEDIUM:
        time = 45;
        break;
      case HARD:
        time = 30;
        break;
      default:
        time = 15;
        break;
    }

    // set accuracy settings
    CategorySelect.Difficulty accuracyDifficulty =
        CategorySelect.Difficulty.valueOf(userSettings.getAccuracy());
    // set accuracy according to the setting
    switch (accuracyDifficulty) {
      case EASY:
        accuracy = 3;
        break;
      case MEDIUM:
        accuracy = 2;
        break;
      default:
        accuracy = 1;
        break;
    }

    // set confidence settings
    CategorySelect.Difficulty confidenceDifficulty =
        CategorySelect.Difficulty.valueOf(userSettings.getConfidence());
    // set confidence according to the setting
    switch (confidenceDifficulty) {
      case EASY:
        confidence = 1;
        break;
      case MEDIUM:
        confidence = 10;
        break;
      case HARD:
        confidence = 25;
        break;
      default:
        confidence = 50;
        break;
    }

    // set the page as it should look like and generate the word
    resetGame();
    btnNewGame.fire();
  }

  /**
   * This method sets the scene to what it looks like when running, sets up the timer and events,
   * and formally starts the game
   */
  private void startTimer() {
    // set up the label and enable canvas
    isFinished = false;
    canvas.setDisable(false);
    hbxDrawTools.setVisible(true);
    if (isHidden) {
      btnHint.setVisible(true);
    }
    // create new game database object
    if (!isZen) {
      activeGameId = gameDao.addNewGame(activeUserId, actualDifficulty, category);
    }

    // set up what to do every second
    timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> changeTime()));
    timer.setCycleCount(time);
    timer.setOnFinished(
        e -> {
          endGame(false);
        });
    // disable timer in zen mode
    if (isZen) {
      timer.setOnFinished(e -> {});
    }

    // if the timer runs to zero
    timer.play();
    runPredictionsInBkg();
  }

  /**
   * When triggered, this method updates the timer label in the scene by decrementing by one value
   */
  private void changeTime() {
    int timeValue = Integer.valueOf(lblTimer.getText()) - 1;
    lblTimer.setText(String.valueOf(timeValue));
    // set red timer when less than ten seconds left
    if (timeValue == 10) {
      new Pulse(lblTimer).play();
      ;
      lblTimer.getStyleClass().add("red-timer");
    }
  }

  /** this method gets retrieves the ai predictions and adds them into a list */
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
        // check if player won (guess is correct within the top three and within
        // confidence range)
        checkCategoryPosition(i, classification.getProbability());
      }
      i++;
    }
  }

  /**
   * This method formats the given prediction classification into a reader friendly string
   *
   * @param classification of the prediction to be formatted
   * @param index number of top guesses
   * @return the formatted string
   */
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

  /**
   * This method identifies how close the game is to finishing, and updates the canvas frame to
   * reflect that
   *
   * @param position of the category word
   * @param confidenceDecimal percent of guess in decimal form
   */
  private void checkCategoryPosition(int position, double confidenceDecimal) {
    // this determines which style class to use
    String pseudoClass = null;
    String messageClass = null;
    // remove border color when cateogry is outside any ranking
    canvasPane.getStyleClass().clear();
    progressMessage.getStyleClass().clear();
    // update confidence to 0dp and in percentage form
    int confidencePercent = (int) Math.round(100 * confidenceDecimal);
    // change the border color depending on its ranking
    if ((position < accuracy) && (confidencePercent >= confidence)) {
      // finish the game if and only if the guess is within the accuracy range and the
      // confidence range
      endGame(true);
      // activate message background in zen mode
      if (zenWin) {
        progressMessage.getStyleClass().add("winMessage");
        canvasPane.getStyleClass().add("top3");
      }
      // highlight word in list view if not hidden
      lvwPredictions.getSelectionModel().select(position);
    } else if (position < 10) {
      pseudoClass = "top10";
      messageClass = "message10";
      progressMessage.setText("You're almost there!");
      // highlight word in list view if not hidden
      if (!isHidden) {
        lvwPredictions.getSelectionModel().select(position);
      }
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

    // set zen colors and message
    if (zenWin) {
      pseudoClass = "top3";
      messageClass = "winMessage";
      progressMessage.setText("You win and can keep drawing!");
    }

    // set color to border
    canvasPane.getStyleClass().add(pseudoClass);
    progressMessage.getStyleClass().add(messageClass);
  }

  /**
   * This method sets the scene to what it should look like when the game ends, regardless of win
   * status. Also checks for new badges, and will show a popup if the user unlocks one or more
   * badges.
   *
   * @param wonGame boolean. true if won, false otherwise
   */
  private void endGame(Boolean wonGame) {
    // cancel method if already won in zen
    if (zenWin) {
      return;
    }

    if (!isZen) {
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
      gameDao.setTime((time - Integer.valueOf(lblTimer.getText())), activeGameId);

      // check for badges
      UserModel activeUser = UserModel.getActiveUser();
      int newBadgeCount =
          BadgeManager.checkNewBadges(
              activeUser.getUsername(), gameDao.getGameById(activeGameId), actualDifficulty);

      // show pop up to display any new badge notifications
      if (newBadgeCount > 0) {
        showNewBadgePopup(newBadgeCount);
      }
    }

    if (isHidden) {
      // showing user what the word was
      hintMessage.setText("The word was " + category + "!");
    }

    // clear styles
    canvasPane.getStyleClass().clear();
    progressMessage.getStyleClass().clear();
    // set the label to win/lose event and use the tts
    if (wonGame) {
      progressMessage.setText("You Win!");
      TextToSpeech.main(new String[] {"You Win!"});
      progressMessage.getStyleClass().add("winMessage");
      canvasPane.getStyleClass().add("top3");
      SoundManager.playSound(SoundName.WIN_GAME);
      if (isZen) {
        zenWin = true;
      }
    } else {
      progressMessage.setText("You Lose!");
      TextToSpeech.main(new String[] {"You Lose!"});
      progressMessage.getStyleClass().add("lossMessage");
      canvasPane.getStyleClass().add("loss");
      SoundManager.playSound(SoundName.LOSE_GAME);
    }
  }

  /**
   * This method creates and formats a pop up dialog box that displays the number of new badges
   * received by the player. the pop up has the options to move to the statistics page or stay at
   * the current page.
   *
   * @param newBadgeCount
   */
  private void showNewBadgePopup(int newBadgeCount) {
    // play positive sound
    SoundManager.playSound(SoundName.LOG_IN);

    Dialog<Void> badgePopup = new Dialog<>();
    // identify whether 'badge' or 'badges' should be used
    String correctNoun = "";
    if (newBadgeCount > 1) {
      correctNoun = "s";
    }
    // build string and add to the alert box
    String str =
        "You have unlocked "
            + newBadgeCount
            + " new badge"
            + correctNoun
            + ". Check it out in the Statistics Page!\n\n\nTIP: Hover over the badges to find out more about them!";
    badgePopup.setContentText(str);
    // add buttons
    ButtonType btnViewBadges = new ButtonType("View Badges", ButtonData.OK_DONE);
    badgePopup.getDialogPane().getButtonTypes().add(btnViewBadges);
    ButtonType btnKeepDrawing = new ButtonType("Keep Drawing", ButtonData.CANCEL_CLOSE);
    badgePopup.getDialogPane().getButtonTypes().add(btnKeepDrawing);

    // change the top title
    badgePopup.setTitle("New Badge" + correctNoun + "!");
    // set size of dialog and buttons
    DialogPane popupPane = badgePopup.getDialogPane();
    popupPane.setPrefSize(550, 200);
    popupPane.getButtonTypes().stream()
        .map(popupPane::lookupButton)
        .forEach(btn -> ButtonBar.setButtonUniformSize(btn, false));
    // set css formatting for pane and buttons
    popupPane.getStylesheets().add("/css/style.css");

    // trigger view badges method if the view badges button is pressed
    badgePopup.setResultConverter(
        (Callback<ButtonType, Void>)
            new Callback<ButtonType, Void>() {
              @Override
              public Void call(ButtonType b) {

                // trigger event if it is this button
                if (b == btnViewBadges) {
                  onViewBadges();
                }

                // play sound regardless of button
                SoundManager.playSound();

                return null;
              }
            });

    badgePopup.show();
  }

  /** This method loads then takes the user to the statistics page */
  private void onViewBadges() {
    // get scene source from an arbitrary button on scene
    // get root and controller for statistics page
    Scene scene = btnSave.getScene();
    Parent statsRoot = SceneManager.getUiRoot(AppUi.USER_STATS);
    StatisticsController statisticsController =
        (StatisticsController) SceneManager.getController(statsRoot);

    // load the necessary stats and change the scene
    statisticsController.loadPage();
    scene.setRoot(statsRoot);
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
      // play positive sound on success
      SoundManager.playSound(SoundName.LOG_IN);
    } catch (Exception e) {
      // play negative sound on fail
      SoundManager.playSound(SoundName.LOG_OUT);
    }
  }

  /**
   * This method takes the user back to the category select, changing the scene where the button is
   *
   * @param event that triggers this call
   */
  @FXML
  private void onReturnToMenu(ActionEvent event) {
    timer.pause();
    isFinished = true;

    Scene scene = ((Button) event.getSource()).getScene();
    scene.setRoot(SceneManager.getUiRoot(AppUi.CATEGORY_SELECT));

    // repeat instructions
    TextToSpeech.main(new String[] {"Choose a difficulty"});

    SoundManager.playSound();
  }

  /** This method reverts everything to it's initial state as seen when first entering the scene */
  private void resetGame() {
    // reset timer and re-enable the drawing tools
    selectedPen = Color.BLACK;
    switchToPen();
    eraserMessage.setText("Eraser OFF");
    btnToggleEraser.setSelected(false);
    lblTimer.setText(String.valueOf(time));
    hbxGameEnd.setVisible(false);
    hbxDrawTools.setVisible(false);
    zenPicker.setVisible(false);
    hbxNewGame.setVisible(true);
    // reset zen win
    zenWin = false;
    // reset hint
    btnHint.setVisible(false);
    hintMessage.setText("Give me a hint!");
    // clear predictions and canvas for new game
    predictions.clear();
    canvas.setDisable(true);
    onClear(null);
    // reset conditional border color rendering
    canvasPane.getStyleClass().add("end-game");
    progressMessage.getStyleClass().clear();
    progressMessage.getStyleClass().add("defaultMessage");
  }

  /**
   * This method depends on the status of the togglable button it is assigned to. The first click
   * will set the cursor to the eraser, and the second will return to a pen. The label is updated as
   * suitable.
   */
  @FXML
  private void onToggleEraser() {

    SoundManager.playSound();

    // Switching between pen and eraser
    if (btnToggleEraser.isSelected()) {
      // Changing label
      eraserMessage.setText("Eraser ON");
      zenPicker.setDisable(true);
      switchToEraser();
    } else {
      // Changing label
      eraserMessage.setText("Eraser OFF");
      zenPicker.setDisable(false);
      switchToPen();
    }
  }

  /**
   * This method depends on the toggleable button it is assigned to. The first click will reset the
   * scene back to it's initial state as if seeing it for the first time, and the second will start
   * the game.
   */
  @FXML
  private void onNewGame() {
    lblTimer.getStyleClass().clear();
    if (btnNewGame.isSelected()) {
      // clear the canvas and timer
      resetGame();
      btnNewGame.setText("Start Game");
      progressMessage.setText("Get ready to start!");
      generateWord();

      // show hidden definition
      if (isHidden) {
        lblDefinition.setVisible(true);
      } else {
        lblDefinition.setVisible(false);
      }

    } else {
      TextToSpeech.main(new String[] {"Let's draw"});
      // start the game and hide the new game toolbar
      if (isZen) {
        hbxGameEnd.setVisible(true);
        btnNewGame.setText("New Word");
        zenPicker.setVisible(true);
      } else {
        hbxNewGame.setVisible(false);
        btnNewGame.setText("Play Again?");
      }

      startTimer();
      SoundManager.playSound();
    }
  }

  /**
   * This method will give the user a hint by telling them the first letter of the word they are
   * drawing
   */
  @FXML
  private void onHint() {
    if (!isFinished) {
      hintMessage.setText("It begins with: " + category.charAt(0));
      SoundManager.playSound();
    }
  }

  /** This method will change the color of the drawing tool */
  @FXML
  private void onColorPick() {
    selectedPen = zenPicker.getValue();
    SoundManager.playSound();
    switchToPen();
  }

  /** This method will generate a word or definition dependent on the selected gamemode */
  private void generateWord() {
    if (!isHidden) {
      // generate a new word
      actualDifficulty = CategorySelect.generateSetCategory();
      category = CategorySelect.getCategory();
      lblCategory.setText("Draw: " + category);
      TextToSpeech.main(new String[] {"Your word is:" + category});
    } else {
      // generate a new definition for hidden word mode
      UserModel activeUser = UserModel.getActiveUser();
      DictionaryLookup lookup = new DictionaryLookup(activeUser);
      actualDifficulty = CategorySelect.getWordDifficulty();
      WordInfo generatedWord = lookup.generateWordInLevel(actualDifficulty);
      // getting word and definition
      category = generatedWord.getWord();
      String definition = generatedWord.getMeaning().getDefinition();
      // add definition to canvas
      lblDefinition.setText("Draw: " + definition);
      TextToSpeech.main(new String[] {"The definition is:" + definition});
    }
  }

  /**
   * This method switches the cursor to a small paint-brush effect on the canvas with color
   * specified ink.
   */
  private void switchToPen() {
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = 5;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          Paint fill = Paint.valueOf(selectedPen.toString());
          graphic.setStroke(fill);
          graphic.setLineWidth(size);

          // Create a line that goes from the point (currentX, currentY) and (x,y)
          graphic.strokeLine(currentX, currentY, x, y);

          // update the coordinates
          currentX = x;
          currentY = y;
        });
  }

  /** This method changes the cursor to a large eraser like effect on the canvas */
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

  /**
   * This method runs the prediction service on a background task every second until the game is set
   * as finished.
   */
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
