package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;
import nz.ac.auckland.se206.controllers.Controller;

public class SceneManager {

  public enum AppUi {
    MAIN_MENU,
    LOG_IN,
    SIGN_UP,
    CATEGORY_SELECT,
    USER_STATS,
    CANVAS
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();
  private static HashMap<Parent, Controller> controllerMap = new HashMap<>();

  /**
   * This method adds the specified item into the ui and parent hashmap
   *
   * @param appUi name of fxml
   * @param uiRoot parent of fxml
   */
  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  /**
   * This method adds the specified item into the parent and controller hashmap
   *
   * @param uiRoot parent of the fxml
   * @param controller object of the fxml
   */
  public static void addController(Parent uiRoot, Controller controller) {
    controllerMap.put(uiRoot, controller);
  }

  public static Controller getController(Parent uiRoot) {
    return controllerMap.get(uiRoot);
  }
}
