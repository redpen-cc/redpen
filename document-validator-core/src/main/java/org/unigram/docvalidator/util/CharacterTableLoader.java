package org.unigram.docvalidator.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.DefaultSymbols;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CharacterTableLoader {

  /**
   * Load CharacterTable.
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
    return load(fis);
  }

  /**
   * Load CharacterTable.
   *
   * @param stream input stream for configuration settings
   * @return generated character table or null if loading was failed.
   */
  public static CharacterTable load(InputStream stream) {
    CharacterTable characterTable = new CharacterTable();
    Map<String, DVCharacter> characterDictionary =
        characterTable.getCharacterDictionary();
    loadDefaultCharacterTable(characterDictionary);
    if (loadTable(stream, characterDictionary)) {
      return characterTable;
    } else {
      return null;
    }

  }

  /**
   * Load input character configuration.
   *
   * @param stream         input configuration
   * @param characterTable character settings
   * @return true when the table is successfully loaded, false otherwise
   */
  private static boolean loadTable(InputStream stream,
                                   Map<String, DVCharacter> characterTable) {
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
          DVCharacter currentChar = createCharacter(element);
          if (currentChar == null) {
            LOG.warn("Found a invalid character setting element.");
            LOG.warn("Skip this element...");
          } else {
            characterTable.put(currentChar.getName(), currentChar);
          }
        } else {
          LOG.error("Invalid Node Name \"" +
              element.getNodeName() + "\" exist.");
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

  private static DVCharacter createCharacter(Element element) {
    if (!element.hasAttribute("name") || !element.hasAttribute("value")) {
      LOG.warn("Found element does not have name and value attribute...");
      return null;
    }
    return new DVCharacter(
        element.getAttribute("name"),
        element.getAttribute("value"),
        element.getAttribute("invalid-chars"),
        Boolean.parseBoolean(element.getAttribute("before-space")),
        Boolean.parseBoolean(element.getAttribute("after-space")));
  }


  private static void loadDefaultCharacterTable(
      Map<String, DVCharacter> characterTable) {
    Iterator<String> characterNames =
        DefaultSymbols.getAllCharacterNames();
    while (characterNames.hasNext()) {
      String charName = characterNames.next();
      DVCharacter character = DefaultSymbols.get(charName);
      characterTable.put(charName, character);
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(CharacterTableLoader.class);
}
