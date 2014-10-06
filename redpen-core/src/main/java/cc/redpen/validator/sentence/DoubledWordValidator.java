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
import cc.redpen.util.ResourceLoader;
import cc.redpen.util.WordListExtractor;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

final public class DoubledWordValidator extends Validator<Sentence> {
    private static final Logger LOG =
            LoggerFactory.getLogger(DoubledWordValidator.class);
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/doubled-word";
    private Set<String> skipList;

    @Override
    public List<ValidationError> validate(Sentence block) {
        List<ValidationError> errors = new ArrayList<>();
        Set<String> surfaces = new HashSet<>();
        for (TokenElement token : block.tokens) {
            String currentSurface = token.getSurface();
            if (surfaces.contains(currentSurface) && !skipList.contains(currentSurface.toLowerCase())) {
                errors.add(createValidationError(block, currentSurface));
            }
            surfaces.add(currentSurface);
        }
        return errors;
    }

    @Override
    protected void init() throws RedPenException {
        String lang = getSymbolTable().getLang();
        WordListExtractor extractor = new WordListExtractor();
        ResourceLoader loader = new ResourceLoader(extractor);
        LOG.info("Loading default invalid expression dictionary for " +
                "\"" + lang + "\".");
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/doubled-word-skiplist-" + lang + ".dat";
        try {
            loader.loadInternalResource(defaultDictionaryFile);
        } catch (IOException e) {
            LOG.error("Failed to load default dictionary.");
            LOG.error("InvalidExpressionValidator does not support dictionary for "
                    + "\"" + lang + "\".");
            throw new RedPenException(e);
        }
        LOG.info("Succeeded to load default dictionary.");

        Optional<String> confFile = getConfigAttribute("dictionary");
        confFile.ifPresent(f -> {
            LOG.info("user dictionary file is " + f);
            try {
                loader.loadExternalFile(f);
            } catch (IOException e) {
                LOG.error("Failed to load user dictionary.");
                return;
            }
            LOG.info("Succeeded to load specified user dictionary.");
        });
        skipList = extractor.get();
    }
}
