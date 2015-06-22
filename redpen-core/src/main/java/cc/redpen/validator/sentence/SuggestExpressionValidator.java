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
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * If input sentences contain invalid expressions, this validator
 * returns the errors with corrected expressions.
 */
final public class SuggestExpressionValidator extends Validator {

    private static final Logger LOG =
            LoggerFactory.getLogger(SuggestExpressionValidator.class);
    private Map<String, String> synonyms = new HashMap<>();

    @Override
    public void validate(Sentence sentence) {
        synonyms.keySet().stream().forEach(value -> {
                    int startPosition = sentence.getContent().indexOf(value);
                    if (startPosition != -1) {
                        addValidationErrorWithPosition(sentence,
                                sentence.getOffset(startPosition),
                                sentence.getOffset(startPosition + value.length()),
                                synonyms.get(value));
                    }
                }
        );
    }

    @Override
    protected void init() throws RedPenException {
        //TODO: support default dictionary.
        Optional<String> confFile = getConfigAttribute("dict");
        LOG.info("Dictionary file is " + confFile);
        if (!confFile.isPresent()) {
            LOG.error("Dictionary file is not specified");
            throw new RedPenException("dictionary file is not specified");
        } else {
            synonyms = KEY_VALUE.loadCachedFromFile(new File(confFile.get()), "SuggestExpressionValidator dictionary");
        }
    }

    protected void setSynonyms(Map<String, String> synonymMap) {
        this.synonyms = synonymMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuggestExpressionValidator that = (SuggestExpressionValidator) o;

        return !(synonyms != null ? !synonyms.equals(that.synonyms) : that.synonyms != null);

    }

    @Override
    public int hashCode() {
        return synonyms != null ? synonyms.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SuggestExpressionValidator{" +
                "synonyms=" + synonyms +
                '}';
    }
}
