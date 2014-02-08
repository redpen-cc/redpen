/**
 * DocumentValidator
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
package org.unigram.docvalidator.util;

import static org.junit.Assert.*;

import org.junit.Test;

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
  public void testDistance() {
    int cost;
    String a;
    String b;

    a = new String ("kitten");
    b = new String ("sitting");
    cost = 3;
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    cost = 5;
    LevenshteinDistance.setInsertionCost(1);
    LevenshteinDistance.setDeletionCost(1);
    LevenshteinDistance.setSubstitutionCost(2);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = null;
    b = null;
    assertEquals(0, LevenshteinDistance.getDistance(a, b));

    a = new String ("");
    b = new String ("");
    assertEquals(0, LevenshteinDistance.getDistance(a, b));

    a = new String ("x");
    b = null;
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = null;
    b = new String ("x");
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = new String ("x");
    b = new String ("x");
    assertEquals(0, LevenshteinDistance.getDistance(a, b));

    a = new String ("");
    b = new String ("x");
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = new String ("x");
    b = new String ("");
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = new String ("x");
    b = new String ("");
    assertEquals(1, LevenshteinDistance.getDistance(a, b));

    a = new String ("ab");
    b = new String ("abc");
    cost = 5;
    LevenshteinDistance.setInsertionCost(cost);
    LevenshteinDistance.setDeletionCost(100);
    LevenshteinDistance.setSubstitutionCost(100);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("bc");
    b = new String ("abc");
    cost = 5;
    LevenshteinDistance.setInsertionCost(cost);
    LevenshteinDistance.setDeletionCost(100);
    LevenshteinDistance.setSubstitutionCost(100);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("ac");
    b = new String ("abc");
    cost = 5;
    LevenshteinDistance.setInsertionCost(cost);
    LevenshteinDistance.setDeletionCost(100);
    LevenshteinDistance.setSubstitutionCost(100);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("abc");
    b = new String ("ab");
    cost = 7;
    LevenshteinDistance.setInsertionCost(100);
    LevenshteinDistance.setDeletionCost(cost);
    LevenshteinDistance.setSubstitutionCost(100);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("abc");
    b = new String ("bc");
    cost = 7;
    LevenshteinDistance.setInsertionCost(100);
    LevenshteinDistance.setDeletionCost(cost);
    LevenshteinDistance.setSubstitutionCost(100);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("abc");
    b = new String ("ac");
    cost = 7;
    LevenshteinDistance.setInsertionCost(100);
    LevenshteinDistance.setDeletionCost(cost);
    LevenshteinDistance.setSubstitutionCost(100);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("abc");
    b = new String ("xbc");
    cost = 9;
    LevenshteinDistance.setInsertionCost(100);
    LevenshteinDistance.setDeletionCost(100);
    LevenshteinDistance.setSubstitutionCost(cost);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("abc");
    b = new String ("axc");
    cost = 9;
    LevenshteinDistance.setInsertionCost(100);
    LevenshteinDistance.setDeletionCost(100);
    LevenshteinDistance.setSubstitutionCost(cost);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();

    a = new String ("abc");
    b = new String ("abx");
    cost = 9;
    LevenshteinDistance.setInsertionCost(100);
    LevenshteinDistance.setDeletionCost(100);
    LevenshteinDistance.setSubstitutionCost(cost);
    assertEquals(cost, LevenshteinDistance.getDistance(a, b));
    resetCost();
  }

  public void resetCost() {
    LevenshteinDistance.setInsertionCost(1);
    LevenshteinDistance.setDeletionCost(1);
    LevenshteinDistance.setSubstitutionCost(1);
  }
}
