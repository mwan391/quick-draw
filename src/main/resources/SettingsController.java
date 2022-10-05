package nz.ac.auckland.se206.controllers;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class SettingsController implements Controller {

    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    //Accuracy buttons
    @FXML private Button btnAccuracyEasy;
    @FXML private Button btnAccuracyMedium;
    @FXML private Button btnAccuracyHard;
    //Time buttons
    @FXML private Button btnTimeEasy;
    @FXML private Button btnTimeMedium;
    @FXML private Button btnTimeHard;
    @FXML private Button btnTimeMaster;
    //confidence buttons
    @FXML private Button btnConfidenceEasy;
    @FXML private Button btnConfidenceMedium;
    @FXML private Button btnConfidenceHard;
    @FXML private Button btnConfidenceMaster;
    
    @FXML
    private void onCancel(ActionEvent event) {
        // TODO
        changeScene(event);
    }
    
    private void onSave(ActionEvent event) {
        // TODO
        changeScene(event);
    }
    
    private void changeScene(ActionEvent event) {
     // change the scene
        Scene scene = ((Button) event.getSource()).getScene();
        scene.setRoot(SceneManager.getUiRoot(AppUi.CATEGORY_SELECT));
    }
}
