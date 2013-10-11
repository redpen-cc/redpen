package org.unigram.docvalidator.store;

import static org.junit.Assert.*;
import org.unigram.docvalidator.store.BlockTypes; 

import org.junit.Test;

public class BlockTypesTest {

  @Test
  public void test() {
    assertEquals(1, BlockTypes.getTokenId("LINE"));
    assertEquals(4, BlockTypes.getTokenId("LIST"));
  }

}
