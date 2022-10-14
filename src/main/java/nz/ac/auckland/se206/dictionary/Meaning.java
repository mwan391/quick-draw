package nz.ac.auckland.se206.dictionary;

public class Meaning {

  private String definition;

  public Meaning(String definition) {

    this.definition = definition;
  }

  public String getDefinition() {
    return definition;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(" Definition: ");
    sb.append(definition);
    return sb.toString();
  }
}
