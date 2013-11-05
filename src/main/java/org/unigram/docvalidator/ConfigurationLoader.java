/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Load configuration file of Validators.
 */
public final class ConfigurationLoader {

  /**
   * Default Constructor.
   */
  public ConfigurationLoader() { }

  /**
   * Constructor.
   * @param stream input configuration settings
   * @return Configuration loaded from input stream
   */
  public Configuration loadConfiguraiton(InputStream stream) {
    Document doc = parseConfigurationString(stream);
    if (doc == null) {
      LOG.error("Failed to parse configuration string");
      return null;
    }
    doc.getDocumentElement().normalize();
    Node root = doc.getElementsByTagName("component").item(0);
    Element rootElement = (Element) root;
    Configuration rootConfiguration =
        new Configuration(rootElement.getAttribute("name"));

    NodeList nodeList = root.getChildNodes();
    for (int temp = 0; temp < nodeList.getLength(); temp++) {
        Node nNode = nodeList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) nNode;
            if (element.getNodeName().equals("component")) {
              rootConfiguration.addChild(
                  this.createConfiguration(element, rootConfiguration));
            } else if  (element.getNodeName().equals("property")) {
              rootConfiguration.addAttribute(element.getAttribute("name"),
                  element.getAttribute("value"));
            }
        }
    }
    return rootConfiguration;
  }

  /**
   * Load Configuration settings from the specified file.
   * @param xmlFile configuration file (xml format)
   * @return Configuration object containing the settings written in input file
   * @throws DocumentValidatorException
   */
  public Configuration loadConfiguraiton(String xmlFile)
      throws DocumentValidatorException {
    InputStream fis = null;
    try {
      fis = new FileInputStream(xmlFile);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    }
    return this.loadConfiguraiton(fis);
  }

  private Document parseConfigurationString(InputStream input) {
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

  private Configuration createConfiguration(Element element,
      Configuration parent) {
    Configuration currentConfiguration =
        new Configuration(element.getAttribute("name"), parent);
    NodeList nodeList = element.getChildNodes();
    for (int temp = 0; temp < nodeList.getLength(); temp++) {
      Node childNode = nodeList.item(temp);
      if (childNode.getNodeName().equals("component")) {
        currentConfiguration.addChild(this.createConfiguration(
            (Element) childNode, currentConfiguration));
      } else if (childNode.getNodeName().equals("property")) {
        Element currentElement = (Element) childNode;
        currentConfiguration.addAttribute(currentElement.getAttribute("name"),
              currentElement.getAttribute("value"));
      }
    }
    return currentConfiguration;
  }

  private static Logger LOG =
      LoggerFactory.getLogger(ConfigurationLoader.class);
}
