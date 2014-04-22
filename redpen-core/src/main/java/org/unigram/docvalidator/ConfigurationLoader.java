/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.unigram.docvalidator.config.CharacterTable;
import org.unigram.docvalidator.config.CharacterTableLoader;
import org.unigram.docvalidator.config.Configuration;
import org.unigram.docvalidator.util.SAXErrorHandler;
import org.unigram.docvalidator.config.ValidationConfigurationLoader;
import org.unigram.docvalidator.config.ValidatorConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Load the central configuration of DocumentValidator.
 */
public class ConfigurationLoader {
  /**
   * load DocumentValidator settings.
   * @param configFileName input configuration settings
   * @return Validator configuration resources
   */
  public Configuration loadConfiguration(String configFileName) {
    InputStream fis = null;
    try {
      fis = new FileInputStream(configFileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    }
    Configuration configuration = this.loadConfiguration(fis);
    IOUtils.closeQuietly(fis);
    return configuration;
  }

  /**
   * load DocumentValidator settings.
   * @param stream input configuration settings
   * @return Configuration loaded from input stream
   * NOTE: return null when failed to create Configuration
   */
  public Configuration loadConfiguration(InputStream stream) {
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

    // Load ValidatorConfiguration
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

    // Load lang
    NodeList langElementList =
        rootElement.getElementsByTagName("lang");
    String lang = "en"; // default language
    String charTableFilePath = null;
    if (langElementList.getLength() == 0) {
      LOG.warn("No \"lang\" block found in the configuration");
    } else {
      Node langNode = langElementList.item(0);
      lang = langNode.getTextContent();
      NamedNodeMap langAttributes = langNode.getAttributes();
      if (langAttributes.getLength() > 0) {
        charTableFilePath =
            langAttributes.getNamedItem("char-conf").getNodeValue();
      }
    }
    LOG.info("Setting lang as \"" + lang + "\"");
    LOG.info("Setting character table setting file as \""
        + charTableFilePath + "\"");

    // Load CharacterTable
    // FIXME dv should work without character settings
    CharacterTable characterTable;
    if (charTableFilePath == null || charTableFilePath.equals("")) {
      LOG.error("No \"char-conf\" attribute found in the configuration");
      return null;
    }
    characterTable = extractCharacterTable(charTableFilePath, lang);

    LOG.info("Succeeded to load character configuration setting");

    // TODO load other configurations

    // Create Configuration
    return new Configuration(validatorConfiguration, characterTable);
  }

  protected CharacterTable extractCharacterTable(
      String characterConfigurationPath, String lang) {
    LOG.info("Symbol setting file: " + characterConfigurationPath);
    return CharacterTableLoader.load(characterConfigurationPath, lang);
  }

  protected ValidatorConfiguration extractValidatorConfiguration(
      Element validatorElement) {
    String validatorConfigurationPath = validatorElement.getTextContent();
    LOG.info("Validation Setting file: " + validatorConfigurationPath);
    return ValidationConfigurationLoader.loadConfiguration(
        validatorConfigurationPath);
  }

  protected static Document parseConfigurationString(InputStream input) {
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
