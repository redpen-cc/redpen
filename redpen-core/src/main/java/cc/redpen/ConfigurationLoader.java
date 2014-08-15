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
package cc.redpen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cc.redpen.config.Symbol;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cc.redpen.config.Configuration;
import cc.redpen.util.SAXErrorHandler;
import cc.redpen.config.ValidatorConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static cc.redpen.config.Configuration.Builder;

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
   * load DocumentValidator configuration.
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

    configBuilder = new Configuration.Builder();
    Element rootElement = getRootNode(doc, "redpen-conf");

    // extract validator configurations
    NodeList validatorConfigElementList =
        getSpecifiedNodeList(rootElement, "validator-list");
    if (validatorConfigElementList == null) {
      LOG.error("There is no validator-list block");
      return null;
    }
    NodeList validatorElementList =
        validatorConfigElementList.item(0).getChildNodes();
    if (validatorElementList == null) {
      LOG.error("There is no validator block");
      return null;
    }
    extractValidatorConfigurations(validatorElementList);

    // extract symbol configurations
    NodeList symbolTableConfigElementList =
        getSpecifiedNodeList(rootElement, "symbol-table");
    if (symbolTableConfigElementList == null) {
      configBuilder.setSymbolTable("en");
    } else {
      extractSymbolConfig(symbolTableConfigElementList);
    }
    return configBuilder.build();
  }

  private void extractValidatorConfigurations(NodeList validatorElementList) {
    ValidatorConfiguration currentConfiguration = null;
    for (int i = 0; i < validatorElementList.getLength(); i++) {
      Node nNode = validatorElementList.item(i);
      if (nNode.getNodeType() != Node.ELEMENT_NODE) { continue; }
      Element element = (Element) nNode;
      if (element.getNodeName().equals("validator")) {
        currentConfiguration =
            new ValidatorConfiguration(element.getAttribute("name"), null);
        configBuilder.addValidatorConfig(currentConfiguration);
        NodeList propertyElementList = nNode.getChildNodes();
        extractProperties(currentConfiguration, propertyElementList);
      } else {
        LOG.warn("Invalid block: \"" + element.getNodeName() + "\"");
        LOG.warn("Skip this block ...");
      }
    }
  }

  private void extractProperties(ValidatorConfiguration currentConfiguration,
      NodeList propertyElementList) {
    for (int j = 0; j < propertyElementList.getLength(); j++) {
      Node pNode = propertyElementList.item(j);
      if (pNode.getNodeType() != Node.ELEMENT_NODE) { continue; }
      Element propertyElement = (Element) pNode;
      if (propertyElement.getNodeName().equals("property")
          && currentConfiguration != null) {
        currentConfiguration.addAttribute(
            propertyElement.getAttribute("name"),
            propertyElement.getAttribute("value"));
      }
    }
  }

  private void extractSymbolConfig(NodeList symbolTableConfigElementList) {
    String language
        = symbolTableConfigElementList.item(0).
        getAttributes().getNamedItem("lang").getNodeValue();
    configBuilder.setSymbolTable(language);

    NodeList symbolTableElementList =
        getSpecifiedNodeList((Element)
            symbolTableConfigElementList.item(0), "character");
    if (symbolTableElementList == null) {
      LOG.warn("there is no character block");
      return;
    }
    for (int temp = 0; temp < symbolTableElementList.getLength(); temp++) {
      Node nNode = symbolTableElementList.item(temp);
      if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) nNode;
        Symbol currentSymbol = createSymbol(element);
        configBuilder.setSymbol(currentSymbol);
      }
    }
  }

  private static Symbol createSymbol(Element element) {
    if (!element.hasAttribute("name") || !element.hasAttribute("value")) {
      throw new IllegalStateException("Found element does not have name and value attribute...");
    }
    return new Symbol(
        element.getAttribute("name"),
        element.getAttribute("value"),
        element.getAttribute("invalid-chars"),
        Boolean.parseBoolean(element.getAttribute("before-space")),
        Boolean.parseBoolean(element.getAttribute("after-space")));
  }

  private NodeList getSpecifiedNodeList(Element rootElement, String elementName) {
    NodeList elementList =
        rootElement.getElementsByTagName(elementName);
    if (elementList.getLength() == 0) {
      LOG.info("No \"" +
          elementName + "\" block found in the configuration");
      return null;
    } else if (elementList.getLength() > 1) {
      LOG.info("More than one \"" + elementName + " \" blocks in the configuration");
    }
    return elementList;
  }

  private Element getRootNode(Document doc, String rootTag) {
    doc.getDocumentElement().normalize();
    NodeList rootConfigElementList =
        doc.getElementsByTagName(rootTag);
    if (rootConfigElementList.getLength() == 0) {
      throw new IllegalStateException("No \"" + rootTag
          + "\" block found in the configuration");
    } else if (rootConfigElementList.getLength() > 1) {
      LOG.warn("More than one \"" +
          rootTag + "\" blocks in the configuration");
    }
    Node root = rootConfigElementList.item(0);
    Element rootElement = (Element) root;
    LOG.info("Succeeded to load configuration file");
    return rootElement;
  }

  protected static Document parseConfigurationString(InputStream input) {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    Document doc = null;
    try {
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      dBuilder.setErrorHandler(new SAXErrorHandler());
      doc = dBuilder.parse(input);
    } catch (SAXException | IOException | ParserConfigurationException e) {
      LOG.error(e.getMessage());
    }
    return doc;
  }

  private Builder configBuilder = new Builder();

  private static final Logger LOG =
      LoggerFactory.getLogger(ConfigurationLoader.class);
}
