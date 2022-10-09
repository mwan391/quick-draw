package nz.ac.auckland.se206.models;

public class BadgeModel {

  private int id;
  private String name;
  private String description;

  public BadgeModel(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
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

  public String getDescrip() {
    return description;
  }

  public void setDescrip(String descrip) {
    this.description = descrip;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("ID: ");
    sb.append(id);
    sb.append(" Name: ").append(name);
    sb.append(" Description: ").append(description);
    return sb.toString();
  }
}
