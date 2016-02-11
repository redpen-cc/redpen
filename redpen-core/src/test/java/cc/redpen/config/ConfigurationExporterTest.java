package cc.redpen.config;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;

import static cc.redpen.config.SymbolType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ConfigurationExporterTest {
  ConfigurationExporter exporter = new ConfigurationExporter();
  ByteArrayOutputStream out = new ByteArrayOutputStream();

  @Test
  public void emptyConfig() throws Exception {
    Configuration config = new Configuration.ConfigurationBuilder().build();
    exporter.export(config, out);
    assertEquals(
      "<redpen-conf lang=\"en\"></redpen-conf>", new String(out.toByteArray()));
  }

  @Test
  public void emptyConfigForJapaneseLanguage() throws Exception {
    Configuration config = new Configuration.ConfigurationBuilder().setLanguage("ja").build();
    exporter.export(config, out);
    assertEquals(
      "<redpen-conf lang=\"ja\" variant=\"zenkaku\"></redpen-conf>", new String(out.toByteArray()));
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
      "</redpen-conf>", new String(out.toByteArray()));
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
      "</redpen-conf>", new String(out.toByteArray()));
  }
}