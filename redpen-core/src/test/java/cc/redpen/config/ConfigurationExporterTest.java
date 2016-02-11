package cc.redpen.config;

import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;

import static cc.redpen.config.SymbolType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ConfigurationExporterTest {
  ConfigurationExporter exporter = new ConfigurationExporter();
  ByteArrayOutputStream out = new ByteArrayOutputStream();

  @After
  public void assertConfigIsLoadable() throws Exception {
    assertNotNull(new ConfigurationLoader().loadFromString(out.toString()));
  }

  @Test
  public void emptyConfig() throws Exception {
    Configuration config = new Configuration.ConfigurationBuilder().build();
    exporter.export(config, out);
    assertEquals("<redpen-conf lang=\"en\"></redpen-conf>", out.toString());
  }

  @Test
  public void emptyConfigForJapaneseLanguage() throws Exception {
    Configuration config = new Configuration.ConfigurationBuilder().setLanguage("ja").build();
    exporter.export(config, out);
    assertEquals("<redpen-conf lang=\"ja\" variant=\"zenkaku\"></redpen-conf>", out.toString());
  }

  @Test
  public void validators() throws Exception {
    Configuration config = new Configuration.ConfigurationBuilder()
      .addValidatorConfig(new ValidatorConfiguration("Mega"))
      .addValidatorConfig(new ValidatorConfiguration("Super").addAttribute("hello", "world")).build();
    exporter.export(config, out);
    assertEquals(
      "<redpen-conf lang=\"en\">\n" +
      "  <validators>\n" +
      "    <validator name=\"Mega\"/>\n" +
      "    <validator name=\"Super\">\n" +
      "      <property name=\"hello\" value=\"world\"/>\n" +
      "    </validator>\n" +
      "  </validators>\n" +
      "</redpen-conf>", out.toString());
  }

  @Test
  public void symbols() throws Exception {
    Configuration config = mock(Configuration.class, RETURNS_DEEP_STUBS);

    when(config.getLang()).thenReturn("en");
    when(config.getVariant()).thenReturn("");
    when(config.getValidatorConfigs()).thenReturn(emptyList());

    when(config.getSymbolTable().getSymbol(ASTERISK)).thenReturn(new Symbol(ASTERISK, 'X'));
    when(config.getSymbolTable().getSymbol(COLON)).thenReturn(new Symbol(COLON, ';', ":", false, true));
    when(config.getSymbolTable().getSymbol(SEMICOLON)).thenReturn(new Symbol(SEMICOLON, ':', ";&", true, false));
    when(config.getSymbolTable().getNames()).thenReturn(new LinkedHashSet<>(asList(ASTERISK, COLON, SEMICOLON)));

    exporter.export(config, out);

    assertEquals(
      "<redpen-conf lang=\"en\">\n" +
      "  <symbols>\n" +
      "    <symbol name=\"ASTERISK\" value=\"X\"/>\n" +
      "    <symbol name=\"COLON\" value=\";\" invalid-chars=\":\" after-space=\"true\"/>\n" +
      "    <symbol name=\"SEMICOLON\" value=\":\" invalid-chars=\";&amp;\" before-space=\"true\"/>\n" +
      "  </symbols>\n" +
      "</redpen-conf>", out.toString());
  }

  @Test
  public void generatedConfigIsLoadable() throws Exception {
    String config = "<redpen-conf lang=\"en\">\n" +
      "  <validators>\n" +
      "    <validator name=\"SentenceLength\">\n" +
      "      <property name=\"max_len\" value=\"100\"/>\n" +
      "    </validator>\n" +
      "    <validator name=\"InvalidSymbol\"/>\n" +
      "  </validators>\n" +
      "  <symbols>\n" +
      "    <symbol name=\"EXCLAMATION_MARK\" value=\"！\" invalid-chars=\"!\" after-space=\"true\"/>\n" +
      "  </symbols>\n" +
      "</redpen-conf>";
    Configuration configuration = new ConfigurationLoader().loadFromString(config);

    exporter.export(configuration, out);

    assertEquals(config, out.toString());
  }
}