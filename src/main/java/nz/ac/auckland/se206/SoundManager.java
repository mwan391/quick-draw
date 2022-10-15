package nz.ac.auckland.se206;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {

  public enum SoundName {
    START_GAME
  }

  /**
   * This method takes the name of an mp3 file and plays that sound once
   *
   * @param soundName name of file
   */
  public static void playSound(SoundName soundName) {
    // get path address
    String pathName = "src/main/resources/sounds/" + soundName.toString().toLowerCase() + ".mp3";
    // set up player
    Media media = new Media(new File(pathName).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    // play sound
    mediaPlayer.play();
  }
}
