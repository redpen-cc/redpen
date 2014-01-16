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
package org.unigram.docvalidator.store;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * BlockTypes provides the API to get id or name of the specified Block.
 */
public class BlockTypes {

    /** Support Block Types. */
    public static final int DOCUMENT = 0;
    public static final int LINE = 1;
    public static final int PARAGRAPH = 2;
    public static final int CHAPTER = 3;
    public static final int SECTION = 3;
    public static final int LIST = 4;
    public static final int COMMENT = 5;
    public static final int CONTENTS = 6;

    public static String getTokenName(int aID) {
        if (!BLOCK_ID_TO_NAME.containsKey(aID)) {
            throw new IllegalArgumentException("given id " + aID);
        }
        final String name = BLOCK_ID_TO_NAME.get(aID);
        if (name == null) {
            throw new IllegalArgumentException("given id " + aID);
        }
        return name;
    }

    public static int getTokenId(String aName) {
        final Integer id = BLOCK_NAME_TO_ID.get(aName);
        if (id == null) {
            throw new IllegalArgumentException("given name " + aName);
        }
        return id;
    }

    private BlockTypes() {
      super();
    }

    /** map from a block id to name. */
    private static final Map<Integer, String> BLOCK_ID_TO_NAME
      = new HashMap<Integer, String>();

    /** map from a block name to id. */
    private static final Map<String, Integer> BLOCK_NAME_TO_ID =
        new HashMap<String, Integer>();

    static {
        final Field[] fields = BlockTypes.class.getDeclaredFields();
        String[] tempTokenValueToName = new String[0];
      for (final Field f : fields) {
        if (f.getType() != Integer.TYPE) {
          continue;
        }
        final String name = f.getName();
        try {
          final Integer value = f.getInt(name);
          BLOCK_NAME_TO_ID.put(name, value);
          BLOCK_ID_TO_NAME.put(value, name);
          final int tokenValue = value;
          if (tokenValue > tempTokenValueToName.length - 1) {
            final String[] temp = new String[tokenValue + 1];
            System.arraycopy(tempTokenValueToName, 0,
                temp, 0, tempTokenValueToName.length);
            tempTokenValueToName = temp;
          }
          tempTokenValueToName[tokenValue] = name;
        } catch (final IllegalArgumentException e) {
          e.printStackTrace();
          System.exit(1);
        } catch (final IllegalAccessException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
    }
}
