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

import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParenthesizedSentenceValidatorTest {
    @Test
    public void testSingleSentence() throws RedPenException {
        ParenthesizedSentenceValidator validator = new ParenthesizedSentenceValidator();
        validator.preInit(new ValidatorConfiguration("ParenthesizedSentence").addProperty("max_nesting_level", "2"), Configuration.builder().build());

        List<Document> documents = new ArrayList<>();
        documents.add(
                Document.builder(new WhiteSpaceTokenizer())
                        .addSection(1)
                        .addParagraph()
                        .addSentence(new Sentence("When it comes to the subject of cake (the sweet and delicious baked delicacy), one should" +
                                " always remember (or at least consider)" +
                                " this foodstuff's effect on one's ever-expanding waistline.", 1))
                        .addSentence(new Sentence(" I aimlessly wandered the streets near my abode (here, in the middle of (merry) England)" +
                                " and tripped over a (discarded) matchstick.", 2))
                        .build());

        Sentence st = documents.get(0).getLastSection().getParagraph(0).getSentence(0);
        List<ValidationError> errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(st);
        assertEquals(st.toString(), 3, errors.size());

        st = documents.get(0).getLastSection().getParagraph(0).getSentence(1);
        errors = new ArrayList<>();
        validator.setErrorList(errors);
        validator.validate(st);
        assertEquals(st.toString(), 2, errors.size());

    }
}
