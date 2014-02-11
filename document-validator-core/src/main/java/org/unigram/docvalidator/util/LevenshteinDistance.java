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

/**
 * Levenshtein Distance(a.k.a. Edit Distance)
 *
 * For given two strings, provide the minimum number
 * of single-character edits (i.e. insertions, deletions
 * or substitutions). The default cost for each edit
 * is 1, and each value is configurable.
 */
public final class LevenshteinDistance {

  private static int insertionCost = 1;
  private static int deletionCost = 1;
  private static int substitutionCost = 1;

  /**
   * Set the cost for "insertion"
   * @param const a cost for "insertion"
   */
  public static synchronized void setInsertionCost(int cost) {
    insertionCost = cost;
  }

  /**
   * Set the cost for "deletion"
   * @param const a cost for "deletio"
   */
  public static synchronized void setDeletionCost(int cost) {
    deletionCost = cost;
  }

  /**
   * Set the cost for "substitution"
   * @param const a cost for "substitution"
   */
  public static synchronized void setSubstitutionCost(int cost) {
    substitutionCost = cost;
  }

  /**
   * Get the cost for "insertion"
   * @return the cost for "insertion"
   */
  public static int getInsertionCost() {
    return insertionCost;
  }

  /**
   * Get the cost for "deletion".
   * @return the cost for "deletion"
   */
  public static int getDeletionCost() {
    return deletionCost;
  }

  /**
   * Get the cost for "substitution".
   * @return the cost for "substitution"
   */
  public static int getSubstitutionCost() {
    return substitutionCost;
  }

  /**
   * Get the Levenshtein distance for given two strings.
   *
   * @see http://en.wikipedia.org/wiki/Levenshtein_distance.
   * 
   * @param a a string.
   * @param b one another string.
   * @return Levenshtein distance.
   */
  public static int getDistance(CharSequence a, CharSequence b) {
    if (a == null && b == null) {
      return 0;
    }
    if (a == null && b != null) {
      return b.length() * insertionCost;
    }
    if (a != null && b == null) {
      return a.length() * insertionCost;
    }

    final int lengthA = a.length();
    final int lengthB = b.length();
    int[][] distance = new int[lengthA + 1][lengthB + 1];

    // Initialization
    for (int i = 0; i < lengthA + 1; i++) {
      distance[i][0] = i * deletionCost;
    }
    for (int j = 0; j < lengthB + 1; j++) {
      distance[0][j] = j * insertionCost;
    }

    for (int i = 1; i < lengthA + 1; i++) {
      for (int j = 1; j < lengthB + 1; j++) {
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
          distance[i][j] = distance[i - 1][j - 1];
        } else {
          distance[i][j] = Math.min(Math.min(
            distance[i - 1][j]     + deletionCost,
            distance[i][j - 1]     + insertionCost),
            distance[i - 1][j - 1] + substitutionCost);
       }
      }
    }

    return distance[lengthA][lengthB];
  }

  /**
   * Default Constructor.
   */
  private LevenshteinDistance() {
  }
}
