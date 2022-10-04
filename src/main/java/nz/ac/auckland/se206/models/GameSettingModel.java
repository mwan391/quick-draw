package nz.ac.auckland.se206.models;

public class GameSettingModel {

  private int id;
  private int userId;
  private String words;
  private String time;
  private String accuracy;
  private String confidence;

  public GameSettingModel(
      int id, int userId, String words, String time, String accuracy, String confidence) {
    this.id = id;
    this.userId = userId;
    this.words = words;
    this.time = time;
    this.accuracy = accuracy;
    this.confidence = confidence;
  }

  public int getId() {
    return id;
  }

  public int getUser() {
    return userId;
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
