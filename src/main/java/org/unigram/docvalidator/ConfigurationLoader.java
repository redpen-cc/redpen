package org.unigram.docvalidator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationConfigurationLoader;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Load the central configuration of DocumentValidator
 */
public class ConfigurationLoader {
  /**
   * load DocumentValidator settings.
   * @param confiFileName input configuration settings
   * @return DocumentVidator configuration resources
   */
  public DVResource loadConfiguraiton(String configFileName) {
    InputStream fis = null;
    try {
      fis = new FileInputStream(configFileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    }
    DVResource resorce = this.loadConfiguraiton(fis);
    IOUtils.closeQuietly(fis);
    return resorce;
    
  }

  /**
   * load DocumentValidator settings.
   * @param stream input configuration settings
   * @return Configuration loaded from input stream
   */
  public DVResource loadConfiguraiton(InputStream stream) {
    Document doc = parseConfigurationString(stream);
    if (doc == null) {
      LOG.error("Failed to parse configuration string");
      return null;
    }

    doc.getDocumentElement().normalize();
    Node root = doc.getElementsByTagName("configuration").item(0);
    Element rootElement = (Element) root;

    // Load ValidatorConfiguraiton
    Element Validator =
        (Element) rootElement.getElementsByTagName("validator-config").item(0);
    String ValidatorConfigurationPath = Validator.getTextContent();
    LOG.info("Validation Setting file: " + ValidatorConfigurationPath);
    ValidationConfigurationLoader validationLoader =
        new ValidationConfigurationLoader();
    ValidatorConfiguration validatorConfiguration = null;
    try {
      validatorConfiguration =
          validationLoader.loadConfiguraiton(ValidatorConfigurationPath);
    } catch (DocumentValidatorException e) {
      LOG.error(e.getLocalizedMessage());
      return null;
    }

    // Load CharacterTable
    Element characterTableElement =
        (Element) rootElement.getElementsByTagName("symbol-table").item(0);
    String CharacterConfigurationPath = characterTableElement.getTextContent();
    LOG.info("Symbol setting file: " + CharacterConfigurationPath);
    CharacterTable characterTable =
        new CharacterTable(CharacterConfigurationPath);

    // TODO load other configurations 

    // Create DVResource
    return new DVResource(validatorConfiguration, characterTable);
  }
  
  static private Document parseConfigurationString(InputStream input) {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    Document doc = null;
    try {
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(input);
    } catch (SAXException e) {
      LOG.error(e.getMessage());
    } catch (IOException e) {
      LOG.error(e.getMessage());
    } catch (ParserConfigurationException e) {
      LOG.error(e.getMessage());
    }
    return doc;
  }

  private static Logger LOG =
      LoggerFactory.getLogger(ConfigurationLoader.class);
}
