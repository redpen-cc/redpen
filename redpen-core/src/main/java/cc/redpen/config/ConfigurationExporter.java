package cc.redpen.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ConfigurationExporter {
  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  TransformerFactory tf = TransformerFactory.newInstance();

  public void export(Configuration config, ByteArrayOutputStream out) {
    try {
      Document xml = dbf.newDocumentBuilder().newDocument();
      Element root = createRoot(config, xml);
      addValidators(config, root);
      addNonDefaultSymbols(config, root);
      serialize(xml, out);
    }
    catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  private void addValidators(Configuration config, Element root) {
    Node validators = addElement(root, "validators");
    config.getValidatorConfigs().forEach(v -> {
      Element validator = addElement(validators, "validator");
      validator.setAttribute("name", v.getConfigurationName());
      v.getAttributes().forEach((name, value) -> {
        Element property = addElement(validator, "property");
        property.setAttribute("name", name);
        property.setAttribute("value", value);
      });
    });
  }

  private void addNonDefaultSymbols(Configuration config, Element root) {
    SymbolTable symbolTable = config.getSymbolTable();
    Map<SymbolType, Symbol> defaults = symbolTable.getDefaultSymbols();
    Node symbols = root.getOwnerDocument().createElement("symbols");
    symbolTable.getNames().forEach(n -> {
      Symbol symbol = symbolTable.getSymbol(n);
      if (symbol.equals(defaults.get(n))) return;
      Element node = addElement(symbols, "symbol");
      node.setAttribute("name", n.toString());
      node.setAttribute("value", String.valueOf(symbol.getValue()));
      if (symbol.getInvalidChars().length > 0) node.setAttribute("invalid-chars", String.valueOf(symbol.getInvalidChars()));
      if (symbol.isNeedAfterSpace()) node.setAttribute("space-after", "true");
    });
    if (symbols.hasChildNodes()) root.appendChild(symbols);
  }

  private Element addElement(Node parent, String name) {
    return (Element) parent.appendChild(parent.getOwnerDocument().createElement(name));
  }

  private Element createRoot(Configuration config, Document xml) {
    Element root = xml.createElement("redpen-conf");
    root.setAttribute("lang", config.getLang());
    if (!config.getVariant().isEmpty())
      root.setAttribute("variant", config.getVariant());
    xml.appendChild(root);
    return root;
  }

  private void serialize(Document xml, ByteArrayOutputStream out) throws TransformerException {
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(new DOMSource(xml), new StreamResult(out));
  }
}
