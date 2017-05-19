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

import static java.lang.Character.UnicodeBlock.*;

public class StringUtils {
    public static boolean isKatakana(char c) {
        return Character.UnicodeBlock.of(c) == KATAKANA;
    }

    public static boolean isHiragana(char c) {
        return Character.UnicodeBlock.of(c) == HIRAGANA;
    }

    public static boolean isCJK(char c) {
        return Character.UnicodeBlock.of(c) == CJK_UNIFIED_IDEOGRAPHS;
    }

    public static boolean isProbablyJapanese(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == KATAKANA || block == HIRAGANA || block == CJK_UNIFIED_IDEOGRAPHS;
    }

    public static boolean isBasicLatin(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN;
    }

    public static boolean isCyrillic(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC;
    }

    public static boolean isKorean(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == HANGUL_SYLLABLES || block == HANGUL_JAMO;
    }
}
