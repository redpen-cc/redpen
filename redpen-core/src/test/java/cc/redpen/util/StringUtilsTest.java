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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {
    @Test
    public void tesIsKatakanaWithHiraganaA() {
        assertFalse(StringUtils.isKatakana('あ'));
    }

    @Test
    public void tesIsKatakanaWithKatakanaA() {
        assertTrue(StringUtils.isKatakana('ア'));
    }

    @Test
    public void tesIsKatakanaWithHyphen() {
        assertTrue(StringUtils.isKatakana('ー'));
    }

    @Test
    public void tesIsKatakanaWithKatakanaMiddleDot() {
        assertTrue(StringUtils.isKatakana('・'));
    }

    @Test
    public void tesIsBasicLatinWithHiraganaA() {
        assertFalse(StringUtils.isBasicLatin('あ'));
    }

    @Test
    public void tesIsBasicLatinWithKatakanaA() {
        assertFalse(StringUtils.isBasicLatin('ア'));
    }

    @Test
    public void tesIsBasicLatinWithHyphen() {
        assertTrue(StringUtils.isBasicLatin('-'));
    }

    @Test
    public void tesIsBasicLatinWithPeriod() {
        assertTrue(StringUtils.isBasicLatin('.'));
    }

    @Test
    public void tesIsBasicLatinWithKatakanaMiddleDot() {
        assertFalse(StringUtils.isBasicLatin('・'));
    }

}
