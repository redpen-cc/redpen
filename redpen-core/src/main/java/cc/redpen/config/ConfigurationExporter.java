package cc.redpen.config;

import cc.redpen.util.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class ConfigurationExporter {

  public void export(Configuration config, OutputStream out) {
    try {
      XMLOutputFactory sf = XMLOutputFactory.newInstance();
      XMLStreamWriter sax = new IndentingXMLStreamWriter(sf.createXMLStreamWriter(new OutputStreamWriter(out, UTF_8)));

      addRoot(config, sax);
      addValidators(config.getValidatorConfigs(), sax);
      addNonDefaultSymbols(config.getSymbolTable(), sax);

      sax.writeEndElement();
      sax.flush();
    }
    catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }

  private void addRoot(Configuration config, XMLStreamWriter sax) throws XMLStreamException {
    sax.writeStartElement("redpen-conf");
    sax.writeAttribute("lang", config.getLang());
    if (!config.getVariant().isEmpty())
      sax.writeAttribute("variant", config.getVariant());
  }

  private void addValidators(List<ValidatorConfiguration> validators, XMLStreamWriter sax) throws XMLStreamException {
    if (validators.isEmpty()) return;
    sax.writeStartElement("validators");

    for (ValidatorConfiguration v : validators) {
      if (v.getAttributes().isEmpty()) sax.writeEmptyElement("validator"); else sax.writeStartElement("validator");
      sax.writeAttribute("name", v.getConfigurationName());
      for (Map.Entry<String, String> attr : v.getAttributes().entrySet()) {
        sax.writeEmptyElement("property");
        sax.writeAttribute("name", attr.getKey());
        sax.writeAttribute("value", attr.getValue());
      }
      if (!v.getAttributes().isEmpty()) sax.writeEndElement();
    }

    sax.writeEndElement();
  }

  private void addNonDefaultSymbols(SymbolTable symbolTable, XMLStreamWriter sax) throws XMLStreamException {
    Map<SymbolType, Symbol> defaults = symbolTable.getDefaultSymbols();
    List<Symbol> nonDefaultSymbols = symbolTable.getNames().stream()
      .map(symbolTable::getSymbol)
      .filter(s -> !s.equals(defaults.get(s.getType()))).collect(toList());

    if (nonDefaultSymbols.isEmpty()) return;

    sax.writeStartElement("symbols");
    for (Symbol symbol : nonDefaultSymbols) {
      sax.writeEmptyElement("symbol");
      sax.writeAttribute("name", symbol.getType().toString());
      sax.writeAttribute("value", String.valueOf(symbol.getValue()));
      if (symbol.getInvalidChars().length > 0) sax.writeAttribute("invalid-chars", String.valueOf(symbol.getInvalidChars()));
      if (symbol.isNeedBeforeSpace()) sax.writeAttribute("before-space", "true");
      if (symbol.isNeedAfterSpace()) sax.writeAttribute("after-space", "true");
    }
    sax.writeEndElement();
  }
}
