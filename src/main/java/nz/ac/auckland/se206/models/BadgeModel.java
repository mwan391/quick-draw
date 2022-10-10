package nz.ac.auckland.se206.models;

public class BadgeModel {

  private int id;
  private String name;
  private String description;
  private String imageUrl;

  public BadgeModel(int id, String name, String description, String imageUrl) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.imageUrl = imageUrl;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("ID: ");
    sb.append(id);
    sb.append(" Name: ").append(name);
    sb.append(" Description: ").append(description);
    return sb.toString();
  }
}
