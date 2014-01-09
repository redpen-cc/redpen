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
import org.unigram.docvalidator.util.CharacterTableLoader;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.SAXErrorHandler;
import org.unigram.docvalidator.util.ValidationConfigurationLoader;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Load the central configuration of DocumentValidator
 */
public class ConfigurationLoader {
  /**
   * load DocumentValidator settings.
   * @param configFileName input configuration settings
   * @return Validator configuration resources
   */
  public DVResource loadConfiguration(String configFileName) {
    InputStream fis = null;
    try {
      fis = new FileInputStream(configFileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    }
    DVResource resource = this.loadConfiguraiton(fis);
    IOUtils.closeQuietly(fis);
    return resource;
  }

  /**
   * load DocumentValidator settings.
   * @param stream input configuration settings
   * @return Configuration loaded from input stream
   * NOTE: return null when failed to create DVResource
   */
  public DVResource loadConfiguraiton(InputStream stream) {
    Document doc = parseConfigurationString(stream);
    if (doc == null) {
      LOG.error("Failed to parse configuration string");
      return null;
    }

    // Get root node
    doc.getDocumentElement().normalize();
    NodeList rootConfigElementList =
        doc.getElementsByTagName("configuration");
    if (rootConfigElementList.getLength() == 0) {
      LOG.error("No \"configuration\" block found in the configuration");
      return null;
    } else if (rootConfigElementList.getLength() > 1) {
      LOG.warn("More than one \"configuration\" blocks in the configuration");
    }
    Node root = rootConfigElementList.item(0);
    Element rootElement = (Element) root;
    LOG.info("Succeeded to load configuration file");

    // Load ValidatorConfiguraiton
    NodeList validatorConfigElementList =
        rootElement.getElementsByTagName("validator");
    if (validatorConfigElementList.getLength() == 0) {
      LOG.error("No \"validator\" block found in the configuration");
      return null;
    } else if (validatorConfigElementList.getLength() > 1) {
      LOG.warn("More than one \"validator\" blocks in the configuration");
    }
    ValidatorConfiguration validatorConfiguration =
        extractValidatorConfiguration(
            (Element) validatorConfigElementList.item(0));
    if (validatorConfiguration == null) {
      LOG.error("Failed to create Validator Configuration Object.");
    }
    LOG.info("Succeeded to load validator configuration setting");

    // Load CharacterTable
    NodeList characterTableElementList =
        rootElement.getElementsByTagName("character-table");
    if (characterTableElementList.getLength() == 0) {
      LOG.error("No \"character-table\" block found in the configuration");
      return null;
    } else if (characterTableElementList.getLength() > 1) {
      LOG.warn("More than one \"character-table\" blocks in the configuration");
    }
    CharacterTable characterTable =
        extractCharacterTable((Element) characterTableElementList.item(0));
    LOG.info("Succeeded to load character configuration setting");
    // TODO load other configurations

    // Create DVResource
    return new DVResource(validatorConfiguration, characterTable);
  }

  protected CharacterTable extractCharacterTable(
      Element characterTableElement) {
    String characterConfigurationPath = characterTableElement.getTextContent();
    LOG.info("Symbol setting file: " + characterConfigurationPath);
    return CharacterTableLoader.load(characterConfigurationPath);
  }

  protected ValidatorConfiguration extractValidatorConfiguration(
      Element validatorElement) {
    String validatorConfigurationPath = validatorElement.getTextContent();
    LOG.info("Validation Setting file: " + validatorConfigurationPath);
    return ValidationConfigurationLoader.loadConfiguration(validatorConfigurationPath);
  }

  static private Document parseConfigurationString(InputStream input) {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    Document doc = null;
    try {
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      dBuilder.setErrorHandler(new SAXErrorHandler());
      doc = dBuilder.parse(input);
    } catch (SAXException e) {
      LOG.error(e.getMessage());
    } catch (IOException e) {
      LOG.error(e.getMessage());
    } catch (ParserConfigurationException e) {
      LOG.error(e.getMessage());
    } catch (Throwable e) {
      LOG.error(e.getMessage());
    }
    return doc;
  }

  private static final Logger LOG =
      LoggerFactory.getLogger(ConfigurationLoader.class);
}
