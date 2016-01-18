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
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;

public class SuccessiveWordValidatorTest extends BaseValidatorTest {

    public SuccessiveWordValidatorTest() {
        super("SuccessiveWord");
    }

    @Test
    public void testDetectSuccessiveWord() throws RedPenException {
        Document document = prepareSimpleDocument("the item is is a good.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found word \"is\" repeated twice in succession.", errors.get(document).get(0).getMessage());
    }

    @Test
    public void testDetectSuccessiveWordWithDifferentCase() throws RedPenException {
        Document document = prepareSimpleDocument("Welcome welcome to Estonia.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found word \"welcome\" repeated twice in succession.", errors.get(document).get(0).getMessage());
    }

    @Test
    public void testDetectJapaneseSuccessiveWord() throws RedPenException {
        config = getConfiguration("ja");

        Document document = prepareSimpleDocument("私はは嬉しい.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(1, errors.get(document).size());
        assertEquals("Found word \"は\" repeated twice in succession.", errors.get(document).get(0).getMessage());
    }

    @Test
    public void testNonSuccessiveDoubledWord() throws RedPenException {
        Document document = prepareSimpleDocument("the item is a item good.");

        RedPen redPen = new RedPen(config);
        Map<Document, List<ValidationError>> errors = redPen.validate(singletonList(document));
        assertEquals(0, errors.get(document).size());
    }
}
