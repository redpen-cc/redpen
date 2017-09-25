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
package cc.redpen.validator.sentence;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.validator.BaseValidatorTest;
import cc.redpen.validator.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SuccessiveWordValidatorTest extends BaseValidatorTest {

    SuccessiveWordValidatorTest() {
        super("SuccessiveWord");
    }

    @Test
    void detectSuccessiveWord() throws RedPenException {
        Document document = prepareSimpleDocument("the item is is a good.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found word \"is\" repeated twice in succession.", errors.get(document).get(0).getMessage());
    }

    @Test
    void detectSuccessiveWordWithDifferentCase() throws RedPenException {
        Document document = prepareSimpleDocument("Welcome welcome to Estonia.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found word \"welcome\" repeated twice in succession.", errors.get(document).get(0).getMessage());
    }

    @Test
    void detectJapaneseSuccessiveWord() throws RedPenException {
        config = getConfiguration("ja");

        Document document = prepareSimpleDocument("私はは嬉しい.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found word \"は\" repeated twice in succession.", errors.get(document).get(0).getMessage());
    }

    @Test
    void nonSuccessiveDoubledWord() throws RedPenException {
        Document document = prepareSimpleDocument("the item is a item good.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }

    @Test
    void ignoreNumbers() throws Exception {
        Document document = prepareSimpleDocument("Amount is $123,456,789.45");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }
}
