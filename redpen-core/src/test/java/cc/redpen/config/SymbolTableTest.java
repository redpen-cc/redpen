package cc.redpen.config;

import org.junit.Test;

import java.util.Optional;

import static cc.redpen.config.SymbolType.COMMA;
import static cc.redpen.config.SymbolType.FULL_STOP;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class SymbolTableTest {
  @Test
  public void englishDoesNotHaveAType() throws Exception {
    SymbolTable table = new SymbolTable("en", Optional.empty(), emptyList());
    assertEquals("", table.getVariant());
  }

  @Test
  public void japaneseUsesZenkakuByDefault() throws Exception {
    SymbolTable table = new SymbolTable("ja", Optional.empty(), emptyList());
    assertEquals("zenkaku", table.getVariant());
    assertEquals('。', table.getSymbol(FULL_STOP).getValue());
    assertEquals('、', table.getSymbol(COMMA).getValue());
  }

  @Test
  public void japaneseCanUseAVariationOfZenkaku() throws Exception {
    SymbolTable table = new SymbolTable("ja", Optional.of("zenkaku2"), emptyList());
    assertEquals("zenkaku2", table.getVariant());
    assertEquals('．', table.getSymbol(FULL_STOP).getValue());
    assertEquals('，', table.getSymbol(COMMA).getValue());
  }

  @Test
  public void japaneseCanUseHankaku() throws Exception {
    SymbolTable table = new SymbolTable("ja", Optional.of("hankaku"), emptyList());
    assertEquals("hankaku", table.getVariant());
    assertEquals('.', table.getSymbol(FULL_STOP).getValue());
    assertEquals(',', table.getSymbol(COMMA).getValue());
  }
}