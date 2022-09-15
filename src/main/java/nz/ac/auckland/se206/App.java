package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.util.SqliteConnection;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Returns the node associated to the input file and its controller to the designated hashmap. The
   * method expects that the file is located in "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {

    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    Parent uiRoot = loader.load();
    SceneManager.addController(uiRoot, loader.getController());

    return uiRoot;
  }

  /**
   * This method is invoked when the application starts. It loads all of the scenes, and shows the
   * "Menu" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If any of the fxml files for the scenes is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {

    SqliteConnection.start();

    SceneManager.addUi(AppUi.MAIN_MENU, loadFxml("menu"));
    SceneManager.addUi(AppUi.LOG_IN, loadFxml("login"));
    SceneManager.addUi(AppUi.CATEGORY_SELECT, loadFxml("category"));
    SceneManager.addUi(AppUi.CANVAS, loadFxml("canvas"));

    Scene scene = new Scene(SceneManager.getUiRoot(AppUi.MAIN_MENU), 640, 480);

    stage.setScene(scene);
    stage.show();

    CategorySelect.setCategories("category_difficulty");
  }
}
