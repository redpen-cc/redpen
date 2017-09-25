package cc.redpen.config;


import org.junit.jupiter.api.Test;

import java.util.Optional;

import static cc.redpen.config.SymbolType.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class SymbolTableTest {
  @Test
  void englishDoesNotHaveAVariant() throws Exception {
    SymbolTable table = new SymbolTable("en", Optional.empty(), emptyList());
    assertEquals("", table.getVariant());
  }

  @Test
  void russianHasSomeDifferentSymbols() throws Exception {
    SymbolTable table = new SymbolTable("ru", Optional.empty(), emptyList());
    assertEquals("", table.getVariant());
    assertEquals('№', table.getSymbol(NUMBER_SIGN).getValue());
  }

  @Test
  void japaneseUsesZenkakuByDefault() throws Exception {
    SymbolTable table = new SymbolTable("ja", Optional.empty(), emptyList());
    assertEquals("zenkaku", table.getVariant());
    assertEquals('。', table.getSymbol(FULL_STOP).getValue());
    assertEquals('、', table.getSymbol(COMMA).getValue());
  }

  @Test
  void japaneseCanUseAVariationOfZenkaku() throws Exception {
    SymbolTable table = new SymbolTable("ja", Optional.of("zenkaku2"), emptyList());
    assertEquals("zenkaku2", table.getVariant());
    assertEquals('．', table.getSymbol(FULL_STOP).getValue());
    assertEquals('，', table.getSymbol(COMMA).getValue());
  }

  @Test
  void japaneseCanUseHankaku() throws Exception {
    SymbolTable table = new SymbolTable("ja", Optional.of("hankaku"), emptyList());
    assertEquals("hankaku", table.getVariant());
    assertEquals('.', table.getSymbol(FULL_STOP).getValue());
    assertEquals(',', table.getSymbol(COMMA).getValue());
  }

  @Test
  void canBeCloned() throws Exception {
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