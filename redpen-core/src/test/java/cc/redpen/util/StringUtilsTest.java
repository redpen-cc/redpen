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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {
    @Test
    void tesIsKatakanaWithHiraganaA() {
        assertFalse(StringUtils.isKatakana('あ'));
    }

    @Test
    void tesIsKatakanaWithKatakanaA() {
        assertTrue(StringUtils.isKatakana('ア'));
    }

    @Test
    void tesIsKatakanaWithHyphen() {
        assertTrue(StringUtils.isKatakana('ー'));
    }

    @Test
    void tesIsKatakanaWithKatakanaMiddleDot() {
        assertTrue(StringUtils.isKatakana('・'));
    }

    @Test
    void tesIsBasicLatinWithHiraganaA() {
        assertFalse(StringUtils.isBasicLatin('あ'));
    }

    @Test
    void tesIsBasicLatinWithKatakanaA() {
        assertFalse(StringUtils.isBasicLatin('ア'));
    }

    @Test
    void tesIsBasicLatinWithHyphen() {
        assertTrue(StringUtils.isBasicLatin('-'));
    }

    @Test
    void tesIsBasicLatinWithPeriod() {
        assertTrue(StringUtils.isBasicLatin('.'));
    }

    @Test
    void tesIsBasicLatinWithKatakanaMiddleDot() {
        assertFalse(StringUtils.isBasicLatin('・'));
    }

}
