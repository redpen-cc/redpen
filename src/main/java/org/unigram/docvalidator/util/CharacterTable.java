package org.unigram.docvalidator.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Configuration table of characters used in DocumentValidator.
 */
public final class CharacterTable {
  public CharacterTable(String fileName) {
    InputStream fis = null;
    try {
      fis = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    }
    characterDictionary = loadTable(fis);
  }

  /**
   * constructor.
   * @param stream input configuration
   */
  public CharacterTable(InputStream stream) {
    this();
    characterDictionary = loadTable(stream);
  }

  /**
   * constructor.
   */
  public CharacterTable() {
    super();
    characterDictionary = new HashMap<String, DVCharacter>();
  }

  /**
   * load input character configuration.
   * @param stream input configuration
   * @return Map from Character(String) to DVCharacter
   */
  public Map<String, DVCharacter> loadTable(InputStream stream) {
    Map<String, DVCharacter> charTable = new HashMap<String, DVCharacter>();
    Document document = parseCharTableString(stream);
    if (document == null) {
      LOG.error("Failed to parse character table");
      return null;
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
            charTable.put(currentChar.getName(), currentChar);
          } else {
            LOG.error("Invalid Node Name: " + element.getNodeName());
            return null;
          }
      }
    }
    LOG.info("Succeeded to load character table");
    return charTable;
  }

  public int getSizeDictionarySize() {
    return this.characterDictionary.size();
  }

  public Set<String> getNames() {
    return this.characterDictionary.keySet();
  }

  public DVCharacter getCharacter(String name) {
    return this.characterDictionary.get(name);
  }

  public boolean isContainCharacter(String name) {
    if (this.characterDictionary.get(name) != null) {
      return true;
    }
    return false;
  }

  private Document parseCharTableString(InputStream input) {
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

  private DVCharacter createCharacter(Element element) {
    DVCharacter character = new DVCharacter(
        element.getAttribute("name"),
        element.getAttribute("value"),
        element.getAttribute("invalid-chars"),
        element.getAttribute("before-space"),
        element.getAttribute("after-space"));
    return character;
  }

  private static Logger LOG = LoggerFactory.getLogger(CharacterTable.class);

  private Map<String, DVCharacter> characterDictionary;
}
