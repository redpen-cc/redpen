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

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

final public class DoubledWordValidator extends Validator {
    private static final Logger LOG =
            LoggerFactory.getLogger(DoubledWordValidator.class);
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/doubled-word";

    public DoubledWordValidator() {
        this.skipList = new HashSet<>();
    }

    private Set<String> skipList;

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        Set<String> surfaces = new HashSet<>();
        for (TokenElement token : sentence.tokens) {
            String currentSurface = token.getSurface();
            if (surfaces.contains(currentSurface) && !skipList.contains(currentSurface.toLowerCase())) {
                errors.add(createValidationError(sentence, currentSurface));
            }
            surfaces.add(currentSurface);
        }
    }

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        WordListExtractor extractor = new WordListExtractor();
        LOG.info("Loading default doubled word skip list dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/doubled-word-skiplist-" + lang + ".dat";
        try {
            extractor.loadFromResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error("Failed to load default dictionary.");
            LOG.error("DoubledWordValidator does not support dictionary for "
                    + "\"" + lang + "\".");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> skipListStr = getConfigAttribute("list");
        skipListStr.ifPresent(f -> {
            String normalized = f.toLowerCase();
            LOG.info("Found user defined skip list.");
            skipList.addAll(Arrays.asList(normalized.split(",")));
            LOG.info("Succeeded to add elements of user defined skip list.");
        });

        Optional<String> confFile = getConfigAttribute("dict");
        confFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                extractor.load(new FileInputStream(f));
            } catch (IOException e) {
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });
        skipList.addAll(extractor.get());
    }

    @Override
    public String toString() {
        return "DoubledWordValidator{" +
                "skipList=" + skipList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubledWordValidator that = (DoubledWordValidator) o;

        if (skipList != null ? !skipList.equals(that.skipList) : that.skipList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return skipList != null ? skipList.hashCode() : 0;
    }
}
