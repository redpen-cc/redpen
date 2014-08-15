/**
 * RedPen
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package cc.redpen.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LevenshteinDistanceTest {

  @Test
  public void testSetGetInsertionCost() {
    LevenshteinDistance.setInsertionCost(5);
    assertEquals(5, LevenshteinDistance.getInsertionCost());
    resetCost();
  }

  @Test
  public void testSetGetDeletionCost() {
    LevenshteinDistance.setDeletionCost(7);
    assertEquals(7, LevenshteinDistance.getDeletionCost());
    resetCost();
  }

  @Test
  public void testSetGetSubstitutionCost() {
    LevenshteinDistance.setSubstitutionCost(9);
    assertEquals(9, LevenshteinDistance.getSubstitutionCost());
    resetCost();
  }

  @Test
  public void testDistanceOfNullAndEmpty() {
    String a = null;
    String b = null;
    assertEquals(0, LevenshteinDistance.getDistance(a, b));

    a = "";
    b = "";
    assertEquals(0, LevenshteinDistance.getDistance(a, b));

    a = null;
    b = "";
    assertEquals(0, LevenshteinDistance.getDistance(a, b));

    a = "";
    b = null;
    assertEquals(0, LevenshteinDistance.getDistance(a, b));

    a = null;
    b = "x";
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = "x";
    b = null;
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = "";
    b = "x";
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = "x";
    b = "";
    assertEquals(1, LevenshteinDistance.getDistance(a, b));
  }

  @Test
  public void testDistanceOfInsertion() {
    String a;
    String b;

    LevenshteinDistance.setInsertionCost(TARGET_COST);
    LevenshteinDistance.setDeletionCost(ALTERNATE_COST);
    LevenshteinDistance.setSubstitutionCost(ALTERNATE_COST);

    a = "ab";
    b = "abc";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    a = "bc";
    b = "abc";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    a = "ac";
    b = "abc";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    resetCost();
  }

  @Test
  public void testDistanceOfDeletion() {
    String a;
    String b;

    LevenshteinDistance.setInsertionCost(ALTERNATE_COST);
    LevenshteinDistance.setDeletionCost(TARGET_COST);
    LevenshteinDistance.setSubstitutionCost(ALTERNATE_COST);

    a = "abc";
    b = "ab";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    a = "abc";
    b = "bc";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    a = "abc";
    b = "ac";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    resetCost();
  }

  @Test
  public void testDistanceOfSubstitution() {
    String a;
    String b;

    LevenshteinDistance.setInsertionCost(ALTERNATE_COST);
    LevenshteinDistance.setDeletionCost(ALTERNATE_COST);
    LevenshteinDistance.setSubstitutionCost(TARGET_COST);

    a = "abc";
    b = "xbc";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    a = "abc";
    b = "axc";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    a = "abc";
    b = "abx";
    assertEquals(TARGET_COST, LevenshteinDistance.getDistance(a, b));

    resetCost();
  }

  @Test
  public void testDistanceMixed() {
    int cost;
    String a = "kitten";
    String b = "sitting";
    cost = 3;
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    cost = 5;
    LevenshteinDistance.setInsertionCost(1);
    LevenshteinDistance.setDeletionCost(1);
    LevenshteinDistance.setSubstitutionCost(2);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();
  }

  public void resetCost() {
    LevenshteinDistance.setInsertionCost(1);
    LevenshteinDistance.setDeletionCost(1);
    LevenshteinDistance.setSubstitutionCost(1);
  }

  private static final int TARGET_COST = 1;
  private static final int ALTERNATE_COST = 100;
}
