/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NumberFormatValidatorTest {
    @Test
    public void testSingleSentence() {
        NumberFormatValidator validator = new NumberFormatValidator();
        Sentence st = new Sentence("ハロー、20ハロ。あの10000インデクス200とこの10,000,0.0インデックス50100,0"
                , 0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(st);
        assertEquals(st.toString(), 3, errors.size());
    }

    @Test
    public void testMultiSentence() {
        NumberFormatValidator validator = new NumberFormatValidator();
        List<ValidationError> errors = new ArrayList<>();
        Sentence st;
        st = new Sentence("1000.029.00に１４３１１２３、00.00ある", 0);
        validator.setErrorList(errors);
        validator.validate(st);
        assertEquals(st.toString(), 3, errors.size());
        st = new Sentence("バグのあ100・00るフェーズ・12.0アナラ212イシ123123ス", 1);
        validator.validate(st);
        assertEquals(st.toString(), 4, errors.size());
    }

}
