package org.unigram.docvalidator.util;

/**
 * Contains Settings used thoughout DocumentValidator.
 */
public final class DVResource {
  /**
   * constructor.
   * @param validatorConf settings of Validators
   */
  public DVResource(Configuration validatorConf) {
    super();
    this.configuration = validatorConf;
    this.characterTable = new CharacterTable();
  }

  /**
   * constructor.
   * @param validatorConf settings of Validators.
   * @param characterConf settings of characters and symbols
   */
  public DVResource(Configuration validatorConf,
      CharacterTable characterConf) {
    super();
    this.configuration = validatorConf;
    this.characterTable = characterConf;
  }

  /**
   * get Configuration.
   * @return Configuration
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * get CharacterTable.
   * @return CharacterTable
   */
  public CharacterTable getCharacterTable() {
    return characterTable;
  }

  private Configuration configuration;

  private CharacterTable characterTable;
}
