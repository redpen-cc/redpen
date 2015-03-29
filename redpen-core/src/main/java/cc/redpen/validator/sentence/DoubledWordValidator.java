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

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.util.ResourceExtractor;
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

    private Set<String> skipList;
    private Set<String> customSkipList;

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
        Set<String> surfaces = new HashSet<>();
        for (TokenElement token : sentence.getTokens()) {
            String currentSurface = token.getSurface();
            if (surfaces.contains(currentSurface) && !skipList.contains(currentSurface.toLowerCase())
                    && !customSkipList.contains(currentSurface.toLowerCase())) {
                errors.add(createValidationErrorFromToken(sentence, token));
            }
            surfaces.add(currentSurface);
        }
    }

    @Override
    protected void init() throws RedPenException {
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/doubled-word-skiplist-" + getSymbolTable().getLang() + ".dat";
        skipList = ResourceExtractor.WORD_LIST.loadCachedFromResource(defaultDictionaryFile, "doubled word skip list");

        customSkipList = new HashSet<>();
        Optional<String> skipListStr = getConfigAttribute("list");
        skipListStr.ifPresent(f -> {
            String normalized = f.toLowerCase();
            LOG.info("Found user defined skip list.");
            customSkipList.addAll(Arrays.asList(normalized.split(",")));
            LOG.info("Succeeded to add elements of user defined skip list.");
        });

        Optional<String> confFile = getConfigAttribute("dict");
        if (confFile.isPresent()) {
            LOG.info("user dictionary file is " + confFile.get());
            try {
                customSkipList.addAll(ResourceExtractor.WORD_LIST.load(new FileInputStream(confFile.get())));
            } catch (IOException e) {
                throw new RedPenException("Failed to load user dictionary.", e);
            }
            LOG.info("Succeeded to load specified user dictionary.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubledWordValidator that = (DoubledWordValidator) o;

        if (skipList != null ? !skipList.equals(that.skipList) : that.skipList != null) return false;
        return !(customSkipList != null ? !customSkipList.equals(that.customSkipList) : that.customSkipList != null);

    }

    @Override
    public int hashCode() {
        int result = skipList != null ? skipList.hashCode() : 0;
        result = 31 * result + (customSkipList != null ? customSkipList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DoubledWordValidator{" +
                "skipList=" + skipList +
                ", customSkipList=" + customSkipList +
                '}';
    }
}
