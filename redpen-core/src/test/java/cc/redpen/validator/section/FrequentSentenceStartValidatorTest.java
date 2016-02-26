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
package cc.redpen.validator.section;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.ValidatorFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FrequentSentenceStartValidatorTest {

    @Test
    public void testDocument() throws RedPenException {
        FrequentSentenceStartValidator validator = (FrequentSentenceStartValidator) ValidatorFactory.getInstance("FrequentSentenceStart");

        Document document =
                Document.builder(new WhiteSpaceTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("When it comes to the Subject Of Cake (the sweet and delicious baked delicacy), one should" +
                                " always remember (or at least consider) this foodstuff's effect on one's ever-expanding waistline.", 1))
                        .addSentence(new Sentence("When it comes to fish, tuna is pretty nice.", 2))
                        .addSentence(new Sentence("When it comes to celery, the thing to consider is the crunch.", 3))
                        .addSentence(new Sentence("When it comes to how to start a sentence, variety is the key.", 4))
                        .addSentence(new Sentence("The acronym CPU stands for Central Processing Unit (CPU).", 5))
                        .addSentence(new Sentence("The acronym AAAS is the American Association for the Advancement of Science.", 6))
                        .build();

        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(document);

        assertEquals(5, errors.size());
    }
}
