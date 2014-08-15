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
package cc.redpen.validator.sentence;

import cc.redpen.ValidationError;
import cc.redpen.model.Sentence;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class KatakanaSpellCheckValidatorTest {
    @Test
    public void testSingleSentence() {
        KatakanaSpellCheckValidator validator
                = new KatakanaSpellCheckValidator();
        Sentence st = new Sentence("ハロー、ハロ。"
                + "あのインデクスとこのインデックス"
                , 0);
        List<ValidationError> errors = validator.validate(st);
        // We do not detect "ハロー" and "ハロ" as a similar pair,
        // but "インデクス" and "インデックス".
        assertEquals(st.toString(), 1, errors.size());
    }

    @Test
    public void testMultiSentence() {
        KatakanaSpellCheckValidator validator
                = new KatakanaSpellCheckValidator();
        List<ValidationError> errors = new ArrayList<>();
        Sentence st;
        st = new Sentence("フレーズ・アナライズにバグがある", 0);
        errors.addAll(validator.validate(st));
        assertEquals(st.toString(), 0, errors.size());
        st = new Sentence("バグのあるフェーズ・アナライシス", 1);
        errors.addAll(validator.validate(st));
        // We detect a similar pair of "フレーズ・アナライズ"
        // and "フェーズ・アナライシス".
        assertEquals(st.toString(), 1, errors.size());
    }
}
