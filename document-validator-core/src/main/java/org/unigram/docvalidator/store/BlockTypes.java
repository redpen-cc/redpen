/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
