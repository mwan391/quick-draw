package nz.ac.auckland.se206.controllers;

import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.CategorySelect.Difficulty;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.SoundManager;
import nz.ac.auckland.se206.SoundManager.SoundName;
import nz.ac.auckland.se206.daos.GameDao;
import nz.ac.auckland.se206.daos.GameProgressDao;
import nz.ac.auckland.se206.daos.GameSettingDao;
import nz.ac.auckland.se206.daos.UserDaoJson;
import nz.ac.auckland.se206.dictionary.DictionaryLookup;
import nz.ac.auckland.se206.models.UserModel;

public class ProfileSettingsController implements Controller {

  @FXML private ComboBox<String> picChooser;
  @FXML private ImageView picPreview;
  @FXML private ProgressBar pbarEasy;
  @FXML private ProgressBar pbarMedium;
  @FXML private ProgressBar pbarHard;
  @FXML private Tooltip ttpEasy;
  @FXML private Tooltip ttpMedium;
  @FXML private Tooltip ttpHard;

  private UserModel activeUser;
  private int totalCountEasy;
  private int totalCountMedium;
  private int totalCountHard;
  private double progressEasy;
  private double progressMedium;
  private double progressHard;

  /** This method loads default values and images upon scene load in the UI */
  public void initialize() {

    // Loading options for profile picture
    String[] picStrings = {"Boy", "Dad", "Girl", "Mother", "Woman"};
    ObservableList<String> picNames = FXCollections.observableArrayList(picStrings);
    picChooser.setItems(picNames);

    // get number of words in each difficulty
    DictionaryLookup dictionary = new DictionaryLookup(null);
    totalCountEasy = dictionary.getWordsOfThisDifficulty(Difficulty.EASY).size();
    totalCountMedium = dictionary.getWordsOfThisDifficulty(Difficulty.MEDIUM).size();
    totalCountHard = dictionary.getWordsOfThisDifficulty(Difficulty.HARD).size();
  }

  /** This method loads the user's profile pic and progress bars into the scene */
  public void loadProfileInfo() {

    // get user image and set it
    activeUser = UserModel.getActiveUser();
    // capitalise string
    String userPic = activeUser.getIcon();
    userPic = userPic.substring(0, 1).toUpperCase() + userPic.substring(1);
    picChooser.setValue(userPic);
    onChangePicture();

    // get progress dao variables
    String activeUserId = activeUser.getId();
    GameProgressDao progressDao = new GameProgressDao();
    String tooltipFormat = "You've seen %d words out of %d! (%.0f%%)";

    // set easy progress
    int playCount = progressDao.getNumberPlayedOfThisDifficulty(Difficulty.EASY, activeUserId);
    progressEasy = (double) playCount / totalCountEasy;
    pbarEasy.setProgress(progressEasy);
    ttpEasy.setText(String.format(tooltipFormat, playCount, totalCountEasy, progressEasy));

    // set medium progress
    playCount = progressDao.getNumberPlayedOfThisDifficulty(Difficulty.MEDIUM, activeUserId);
    progressMedium = (double) playCount / totalCountMedium;
    pbarMedium.setProgress(progressMedium);
    ttpMedium.setText(String.format(tooltipFormat, playCount, totalCountMedium, progressMedium));

    // set hard progress
    playCount = progressDao.getNumberPlayedOfThisDifficulty(Difficulty.HARD, activeUserId);
    progressHard = (double) playCount / totalCountHard;
    pbarHard.setProgress(progressHard);
    ttpHard.setText(String.format(tooltipFormat, playCount, totalCountHard, progressHard));
  }

  /**
   * This method changes the scene back to the category select and saves the selected user picture
   *
   * @param event, the button press used to determine current scene
   */
  @FXML
  private void onSeeMenu(ActionEvent event) {
    // get root
    Scene scene = ((Button) event.getSource()).getScene();
    Parent categoryRoot = SceneManager.getUiRoot(AppUi.CATEGORY_SELECT);

    // save image to database
    UserDaoJson userDao = new UserDaoJson();
    userDao.updateAvatar(activeUser, picChooser.getValue().toLowerCase());

    // change scene
    SoundManager.playSound();
    scene.setRoot(categoryRoot);
  }

  /** this method updates the image shown on screen to match the choice in the choice box */
  @FXML
  private void onChangePicture() {
    Image preview =
        new Image(
            getClass()
                .getResourceAsStream(
                    "/images/profileicons/" + picChooser.getValue().toLowerCase() + ".png"));
    // Changing the preview to the new selection
    picPreview.setImage(preview);
    SoundManager.playSound();
  }

  /**
   * This method will show a warning pop up. clicking delete will delete everything and take the
   * user back to the first screen, while clicking cancel will do nothing.
   */
  @FXML
  private void onDeleteX(ActionEvent event) {

    boolean isFromAccount = false;

    // create popup
    Dialog<ButtonType> warningPopup = new Dialog<>();

    // write main dialog
    String str =
        "WARNING!\nYou will lose all your word progress, and you cannot undo this.\nYour badges will still be here though!.\nAre you sure you want to delete your progress?";

    // identify if delete account or progress
    if (((Button) event.getSource()).getText().equals("Delete Your Account")) {
      isFromAccount = true;
      str =
          "WARNING!\nYou will lose all progress, and you cannot undo this.\n\nAre you sure you want to delete everything?";
    }

    warningPopup.setContentText(str);
    // add buttons
    ButtonType btnDelete = new ButtonType("Yes, Delete It", ButtonData.OK_DONE);
    warningPopup.getDialogPane().getButtonTypes().add(btnDelete);
    ButtonType btnCancel = new ButtonType("No, Go Back", ButtonData.CANCEL_CLOSE);
    warningPopup.getDialogPane().getButtonTypes().add(btnCancel);

    // change the top title
    warningPopup.setTitle("WARNING!");
    // set size of dialog
    DialogPane popupPane = warningPopup.getDialogPane();
    popupPane.setPrefSize(550, 200);
    popupPane.getButtonTypes().stream()
        .map(popupPane::lookupButton)
        .forEach(btn -> ButtonBar.setButtonUniformSize(btn, false));
    // set css formatting for popup
    popupPane.getStylesheets().add("/css/style.css");

    // show warning
    SoundManager.playSound(SoundName.LOSE_GAME);
    Optional<ButtonType> result = warningPopup.showAndWait();

    // format response
    switch (result.get().getButtonData()) {
      case OK_DONE:
        // play sad sound
        SoundManager.playSound(SoundName.LOG_OUT);
        if (isFromAccount) {
          // delete all saved user data
          deleteUser();
          // take user to first menu
          returnToFirstMenu();
        } else {
          // delete only game history for user
          GameDao gameDao = new GameDao();
          gameDao.removeGamesFromUser(activeUser);
          // reload page
          this.loadProfileInfo();
        }
        break;
      default:
        // play happy sound
        SoundManager.playSound(SoundName.LOG_IN);
    }
  }

  /**
   * This method takes the user back to the very first menu as if booting the game from start again
   */
  private void returnToFirstMenu() {
    // get scene from arbitrary element on stage
    Scene scene = picChooser.getScene();
    // get root
    Parent menuRoot = SceneManager.getUiRoot(AppUi.MAIN_MENU);

    // change scene
    scene.setRoot(menuRoot);
  }

  /**
   * This method deletes everything related to the given user, including settings profiles and game
   * history
   */
  private void deleteUser() {
    // delete game difficulty settings
    GameSettingDao difficultyDao = new GameSettingDao();
    difficultyDao.removeSettingsFromUser(activeUser);

    // delete game history for user
    GameDao gameDao = new GameDao();
    gameDao.removeGamesFromUser(activeUser);

    // delete user from json
    UserDaoJson userDao = new UserDaoJson();
    userDao.remove(activeUser);

    // set active user as null
    UserModel.setActiveUser(null);
  }
}
