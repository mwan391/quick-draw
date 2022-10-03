package nz.ac.auckland.se206.models;

public class GameSettingModel {

  private int id;
  private String username;
  private String words;
  private String time;
  private String accuracy;
  private String confidence;

  public GameSettingModel(
      int id, String username, String words, String time, String accuracy, String confidence) {
    this.id = id;
    this.username = username;
    this.words = words;
    this.time = time;
    this.accuracy = accuracy;
    this.confidence = confidence;
  }

  public int getId() {
    return id;
  }

  public String getUser() {
    return username;
  }

  public String getWords() {
    return words;
  }

  public String getTime() {
    return time;
  }

  public String getAccuracy() {
    return accuracy;
  }

  public String getConfidence() {
    return confidence;
  }
}
