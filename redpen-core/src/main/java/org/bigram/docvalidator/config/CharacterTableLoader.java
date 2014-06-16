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
package org.bigram.docvalidator.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bigram.docvalidator.symbol.AbstractSymbols;
import org.bigram.docvalidator.symbol.JapaneseSymbols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bigram.docvalidator.symbol.DefaultSymbols;
import org.bigram.docvalidator.util.SAXErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Load CharacterTable from a file or stream.
 */
public final class CharacterTableLoader {

  /**
   * Load CharacterTable.
   *
   * Note: language is set to "en" with this constructor. When you want
   * to specify the language,use the constructor with language as the
   * parameter.
   *
   * @param fileName configuration file name
   * @return generated character table or null if loading was failed
   */
  public static CharacterTable load(String fileName) {
    InputStream fis;
    try {
      fis = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
      return null;
    }
    return load(fis, "en");
  }

  /**
   * Load CharacterTable.
   *
   * Note: language is set to "en" with this constructor. When you want
   * to specify the language,use the constructor with language as the
   * parameter.
   *
   * @param stream input stream for configuration settings
   * @return generated character table or null if loading was failed.
   */
  public static CharacterTable load(InputStream stream) {
    return load(stream, "en");
  }

  /**
   * Load CharacterTable from a given file.
   *
   * @param fileName configuration file name
   * @param lang target language
   * @return generated character table or null if loading was failed.
   */
  public static CharacterTable load(String fileName, String lang) {
    InputStream fis;
    try {
      fis = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
      return null;
    }
    return load(fis, lang);
  }

  /**
   * Load CharacterTable from a given stream.
   *
   * @param stream input configuration
   * @param lang target language
   * @return generated character table or null if loading was failed.
   */
  public static CharacterTable load(InputStream stream, String lang) {
    characterTable = new CharacterTable();
    loadDefaultCharacterTable(lang);
    if (loadTable(stream)) {
      return characterTable;
    } else {
      return null;
    }
  }

  /**
   * Replace the current character setting.
   * @param character symbol configuration
   */
  public static void override(Character character) {
    Map<String, Character> characterDictionary
        = characterTable.getCharacterDictionary();
    characterDictionary.put(character.getName(), character);
  }

  /**
   * Load input character configuration.
   *
   * @param stream         input configuration
   * @return true when the table is successfully loaded, false otherwise
   */
  private static boolean loadTable(InputStream stream) {
    Document document = parseCharTableString(stream);
    if (document == null) {
      LOG.error("Failed to parse character table");
      return false;
    }

    document.getDocumentElement().normalize();
    NodeList rootNodeList = document.getElementsByTagName("character-table");
    if (rootNodeList.getLength() == 0) {
      LOG.error("No \"character-table\" block found...");
      return false;
    } else if (rootNodeList.getLength() > 1) {
      LOG.warn("Found more than one \"character-table\" blocks.");
      LOG.warn("Use the first block ...");
    }
    Node root = rootNodeList.item(0);
    NodeList nodeList = root.getChildNodes();
    for (int temp = 0; temp < nodeList.getLength(); temp++) {
      Node nNode = nodeList.item(temp);
      if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) nNode;
        if (element.getNodeName().equals("character")) {
          Character currentChar = createCharacter(element);
          if (currentChar == null) {
            LOG.warn("Found a invalid character setting element.");
            LOG.warn("Skip this element...");
          } else {
            override(currentChar);
          }
        } else {
          LOG.error("Invalid Node Name \""
              + element.getNodeName() + "\" exist.");
          return false;
        }
      }
    }
    LOG.info("Succeeded to load character table");
    return true;
  }

  private static Document parseCharTableString(InputStream input) {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    try {
      dBuilder = dbFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      LOG.error("In parseCharTableString: " + e.getMessage());
      return null;
    }

    dBuilder.setErrorHandler(new SAXErrorHandler());
    Document doc = null;
    try {
      doc = dBuilder.parse(input);
    } catch (SAXException e) {
      LOG.error("In parseCharTableString: " + e.getMessage());
    } catch (IOException e) {
      LOG.error("In parseCharTableString: " + e.getMessage());
    } catch (Throwable t) {
      LOG.error("Unknown error");
    }
    return doc;
  }

  private static Character createCharacter(Element element) {
    if (!element.hasAttribute("name") || !element.hasAttribute("value")) {
      LOG.warn("Found element does not have name and value attribute...");
      return null;
    }
    return new Character(
        element.getAttribute("name"),
        element.getAttribute("value"),
        element.getAttribute("invalid-chars"),
        Boolean.parseBoolean(element.getAttribute("before-space")),
        Boolean.parseBoolean(element.getAttribute("after-space")));
  }

  private static void loadDefaultCharacterTable(
      String lang) {
    Map<String, Character> dictionary
        = characterTable.getCharacterDictionary();
    AbstractSymbols symbolSettings;
    if (lang.equals("ja")) {
      symbolSettings = JapaneseSymbols.getInstance();
    } else {
      symbolSettings = DefaultSymbols.getInstance();
    }

    Iterator<String> characterNames = symbolSettings.getAllCharacterNames();
    while (characterNames.hasNext()) {
      String charName = characterNames.next();
      Character character = symbolSettings.get(charName);
      dictionary.put(charName, character);
    }
  }

  private CharacterTableLoader() {
    // for safe
  }

  private static CharacterTable characterTable;

  private static final Logger LOG =
      LoggerFactory.getLogger(CharacterTableLoader.class);
}
