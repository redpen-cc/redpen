package org.unigram.docvalidator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.unigram.docvalidator.DefaultSymbols;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CharacterTableLoader {
  /**
   * load input character configuration.
   * @param stream input configuration
   * @param characterTable TODO
   * @return TODO
   */
  public final static boolean loadTable(InputStream stream,
      Map<String, DVCharacter> characterTable) {
    Document document = CharacterTableLoader.parseCharTableString(stream);
    if (document == null) {
      CharacterTable.LOG.error("Failed to parse character table");
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
            DVCharacter currentChar = CharacterTableLoader.createCharacter(element);
            characterTable.put(currentChar.getName(), currentChar);
          } else {
            CharacterTable.LOG.error("Invalid Node Name: " + element.getNodeName());
            return false;
          }
      }
    }
    CharacterTable.LOG.info("Succeeded to load character table");
    return false;
  }

  private static Document parseCharTableString(InputStream input) {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    Document doc = null;
    try {
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(input);
    } catch (SAXException e) {
      CharacterTable.LOG.error(e.getMessage());
    } catch (IOException e) {
      CharacterTable.LOG.error(e.getMessage());
    } catch (ParserConfigurationException e) {
      CharacterTable.LOG.error(e.getMessage());
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

  static public void loadDefaultCharacterTable(
      Map<String, DVCharacter> characterTable) {
    Iterator<String> characterNames =
        DefaultSymbols.getAllCharacterNames();
    while (characterNames.hasNext()) {
      String charName = characterNames.next();
      DVCharacter character = DefaultSymbols.get(charName);
      characterTable.put(charName, character);
    }
  }

}
