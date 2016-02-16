package cc.redpen.config;

import org.junit.Test;

import java.util.Optional;

import static cc.redpen.config.SymbolType.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

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

  @Test
  public void canBeCloned() throws Exception {
    SymbolTable symbolTable = new SymbolTable("ja", Optional.of("hankaku"), singletonList(new Symbol(BACKSLASH, '+')));
    SymbolTable clone = symbolTable.clone();

    assertNotSame(symbolTable, clone);
    assertEquals(symbolTable, clone);
    assertEquals(symbolTable.getLang(), clone.getLang());
    assertEquals(symbolTable.getVariant(), clone.getVariant());

    symbolTable.overrideSymbol(new Symbol(AMPERSAND, ','));
    assertEquals('&', clone.getSymbol(AMPERSAND).getValue());
    assertEquals(COMMA, clone.getSymbolByValue(',').getType());
  }
}