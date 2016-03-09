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
        Sentence st0 = new Sentence("1, 10, 200, 1,000.00, and 10,000 and 10,000.00 and 100,000.00 and 10,000,000.0 should all be fine.", 0);
        Sentence st1 = new Sentence("10000 gives an error because it should really be formatted as 10,000.", 0);
        Sentence st2 = new Sentence("100,00 has a comma in the wrong position but 100.00 does not.", 0);
        Sentence st3 = new Sentence("10,0000,000 also has a comma in the wrong position.", 0);
        Sentence st4 = new Sentence("10000.0.0 has too many decimal places and requires a delimiter.", 0);
        Sentence st5 = new Sentence("100,00,000.0 has a sequence of digits that is too short.", 0);
        Sentence st6 = new Sentence("Years are ignored by default, like 1995 or 2016", 0);

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(st0);
        validator.validate(st1); // 1 error
        validator.validate(st2); // 1 error
        validator.validate(st3); // 1 error
        validator.validate(st4); // 2 errors
        validator.validate(st5); // 1 error
        validator.validate(st6); // 0 errors

        assertEquals(st1.toString(), 6, errors.size());
    }
}
