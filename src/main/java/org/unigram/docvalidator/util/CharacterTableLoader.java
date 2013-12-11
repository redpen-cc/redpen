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
   * load CharacterTable.
   * @param fileName configuration file name
   * @return generated character table or null if loading was failed.
   */
  public static final CharacterTable load(String fileName){
    InputStream fis = null;
    try {
      fis = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    }
    return load(fis);
  }

  /**
   * load CharacterTable.
   * @param stream input stream for configuration settings
   * @return generated character table or null if loading was failed.
   */
  public static final CharacterTable load(InputStream stream){
    CharacterTable characterTable = new CharacterTable();
    Map<String, DVCharacter> characterDictionary =
        characterTable.getCharacterDictionary();
    loadDefaultCharacterTable(characterDictionary);
    loadTable(stream, characterDictionary);
    return characterTable;
  }

  /**
   * load input character configuration.
   * @param stream input configuration
   * @param characterTable TODO
   * @return TODO
   */
  private final static boolean loadTable(InputStream stream,
      Map<String, DVCharacter> characterTable) {
    Document document = parseCharTableString(stream);
    if (document == null) {
      LOG.error("Failed to parse character table");
      return false;
    }

    document.getDocumentElement().normalize();
    Node root = document.getElementsByTagName("character-table").item(0);
    NodeList nodeList = root.getChildNodes();
    for (int temp = 0; temp < nodeList.getLength(); temp++) {
      Node nNode = nodeList.item(temp);
      if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) nNode;
          if (element.getNodeName().equals("character")) {
            DVCharacter currentChar = createCharacter(element);
            characterTable.put(currentChar.getName(), currentChar);
          } else {
            LOG.error("Invalid Node Name: " + element.getNodeName());
            return false;
          }
      }
    }
    LOG.info("Succeeded to load character table");
    return false;
  }

  private static Document parseCharTableString(InputStream input) {
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

  private static DVCharacter createCharacter(Element element) {
    DVCharacter character = new DVCharacter(
        element.getAttribute("name"),
        element.getAttribute("value"),
        element.getAttribute("invalid-chars"),
        Boolean.parseBoolean(element.getAttribute("before-space")),
        Boolean.parseBoolean(element.getAttribute("after-space")));
    return character;
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

  static Logger LOG = LoggerFactory.getLogger(CharacterTableLoader.class);
}
