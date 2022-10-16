package nz.ac.auckland.se206.dictionary;

public class Meaning {

  private String definition;

  /**
   * Contains the definition of the category, models how it is stored in JSON file
   *
   * @param definition
   */
  public Meaning(String definition) {
    this.definition = definition;
  }

  public String getDefinition() {
    return definition;
  }

  /** Converts the word's definition to a formatted string for printing */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(" Definition: ");
    sb.append(definition);
    return sb.toString();
  }
}
