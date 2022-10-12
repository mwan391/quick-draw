package nz.ac.auckland.se206.models;

public class GameSettingModel {

  private int id;
  private String userId;
  private String words;
  private String time;
  private String accuracy;
  private String confidence;

  public GameSettingModel(
      int id, String userId, String words, String time, String accuracy, String confidence) {
    this.id = id;
    this.userId = userId;
    this.setWords(words);
    this.setTime(time);
    this.setAccuracy(accuracy);
    this.setConfidence(confidence);
  }

  public int getId() {
    return id;
  }

  public String getUser() {
    return userId;
  }

  public String getWords() {
    return words;
  }

  public void setWords(String words) {
    this.words = words;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(String accuracy) {
    this.accuracy = accuracy;
  }

  public String getConfidence() {
    return confidence;
  }

  public void setConfidence(String confidence) {
    this.confidence = confidence;
  }
}
