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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Stream.concat;

/**
 * Validate input sentences contain invalid expression.
 */
public final class InvalidExpressionValidator extends Validator {
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/invalid-expression";
    private Set<String> invalidExpressions;

    public InvalidExpressionValidator() {
        super("list", new HashSet<>());
    }

    @Override
    public void validate(Sentence sentence) {
        concat(invalidExpressions.stream(), getSetAttribute("list").stream()).forEach(value -> {
            int startPosition = sentence.getContent().indexOf(value);
            if (startPosition != -1) {
                addLocalizedErrorWithPosition(sentence, startPosition, startPosition + value.length(), value);
            }
        });
    }

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH + "/invalid-expression-" + lang + ".dat";
        invalidExpressions = WORD_LIST.loadCachedFromResource(defaultDictionaryFile, "invalid expression");

        Optional<String> confFile = getConfigAttribute("dict");
        if (confFile.isPresent()) {
            getSetAttribute("list").addAll(WORD_LIST.loadCachedFromFile(findFile(confFile.get()), "InvalidExpressionValidator user dictionary"));
        }
    }
}
