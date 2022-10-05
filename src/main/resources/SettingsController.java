package nz.ac.auckland.se206.controllers;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

public class SettingsController implements Controller {

    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    
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
