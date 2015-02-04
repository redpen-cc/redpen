/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.util;

/**
 * Levenshtein Distance(a.k.a. Edit Distance)
 * <p>
 * For given two strings, provide the minimum number
 * of single-character edits (i.e. insertions, deletions
 * or substitutions). The default cost for each edit
 * is 1, and each value is configurable.
 */
public final class LevenshteinDistance {
    /**
     * A constant holding the default insertion cost.
     */
    public static final int DEFAULT_INSERTION_COST = 1;

    /**
     * A constant holding the default deletion cost.
     */
    public static final int DEFAULT_DELETION_COST = 1;

    /**
     * A constant holding the default substitution cost.
     */
    public static final int DEFAULT_SUBSTITUTION_COST = 1;

    private static int INSERTION_COST;
    private static int DELETION_COST;
    private static int SUBSTITUTION_COST;

    static {
        INSERTION_COST = DEFAULT_INSERTION_COST;
        DELETION_COST = DEFAULT_DELETION_COST;
        SUBSTITUTION_COST = DEFAULT_SUBSTITUTION_COST;
    }

    /**
     * Default Constructor.
     */
    private LevenshteinDistance() {
    }

    /**
     * Get the cost for "insertion".
     *
     * @return the cost for "insertion"
     */
    public static int getInsertionCost() {
        return INSERTION_COST;
    }

    /**
     * Set the cost for "insertion".
     *
     * @param cost a cost for "insertion"
     */
    public static synchronized void setInsertionCost(int cost) {
        INSERTION_COST = cost;
    }

    /**
     * Get the cost for "deletion".
     *
     * @return the cost for "deletion"
     */
    public static int getDeletionCost() {
        return DELETION_COST;
    }

    /**
     * Set the cost for "deletion".
     *
     * @param cost a cost for "deletio"
     */
    public static synchronized void setDeletionCost(int cost) {
        DELETION_COST = cost;
    }

    /**
     * Get the cost for "substitution".
     *
     * @return the cost for "substitution"
     */
    public static int getSubstitutionCost() {
        return SUBSTITUTION_COST;
    }

    /**
     * Set the cost for "substitution".
     *
     * @param cost a cost for "substitution"
     */
    public static synchronized void setSubstitutionCost(int cost) {
        SUBSTITUTION_COST = cost;
    }

    /**
     * Get the Levenshtein distance for given two strings.
     *
     * @param a a string.
     * @param b one another string.
     * @return Levenshtein distance.
     * @see <a href="http://en.wikipedia.org/wiki/Levenshtein_distance">http://en.wikipedia.org/wiki/Levenshtein_distance</a>
     */
    public static int getDistance(CharSequence a, CharSequence b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null && b != null) {
            return b.length() * INSERTION_COST;
        }
        if (a != null && b == null) {
            return a.length() * INSERTION_COST;
        }

        final int lengthA = a.length();
        final int lengthB = b.length();
        int[][] distance = new int[lengthA + 1][lengthB + 1];

        // Initialization
        for (int i = 0; i < lengthA + 1; i++) {
            distance[i][0] = i * DELETION_COST;
        }
        for (int j = 0; j < lengthB + 1; j++) {
            distance[0][j] = j * INSERTION_COST;
        }

        for (int i = 1; i < lengthA + 1; i++) {
            for (int j = 1; j < lengthB + 1; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    distance[i][j] = distance[i - 1][j - 1];
                } else {
                    distance[i][j] = Math.min(Math.min(
                                    distance[i - 1][j] + DELETION_COST,
                                    distance[i][j - 1] + INSERTION_COST),
                            distance[i - 1][j - 1] + SUBSTITUTION_COST);
                }
            }
        }

        return distance[lengthA][lengthB];
    }
}
