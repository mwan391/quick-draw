package nz.ac.auckland.se206.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class ComboBoxRender {

  /**
   * Increases the font size for text inside the combobox, in both the list of options and the
   * selected option, uses customised cell factory to access its root styling
   *
   * @param comboBox the combobox component
   */
  public static void increaseFontSize(ComboBox<String> comboBox) {

    // increase size of font for the selected input cell
    comboBox.setButtonCell(
        new ListCell<String>() {
          // process the table cell
          @Override
          protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setStyle("-fx-font-size:18");
            if (!(empty || item == null)) {
              setText(item.toString());
            }
          }
        });
    // increase size of font for options
    comboBox.setCellFactory(
        l ->
            new ListCell<String>() {
              // process the table cell
              @Override
              protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setStyle("-fx-font-size:18");
                } else {
                  setStyle("-fx-font-size:18");
                  setText(item.toString());
                }
              }
            });
  }
}
