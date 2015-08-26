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
package cc.redpen.util;

import cc.redpen.RedPenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Spelling utility
 */
public class SpellingUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SpellingUtils.class);
    private static Set<String> dictionary = null;
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/spellchecker";


    @Override
    protected void init() throws RedPenException {
        String defaultDictionaryFile = DEFAULT_RESOURCE_PATH
                + "/spellchecker-" + getSymbolTable().getLang() + ".dat";
        dictionary = WORD_LIST_LOWERCASED.loadCachedFromResource(defaultDictionaryFile, "spell dictionary");

        Optional<String> listStr = getConfigAttribute("list");
        listStr.ifPresent(f -> {
            LOG.info("User defined valid word list found.");
            dictionary.addAll(Arrays.asList(f.split(",")));
            LOG.info("Succeeded to add elements of user defined list.");
        });

        Optional<String> userDictionaryFile = getConfigAttribute("dict");
        if (userDictionaryFile.isPresent()) {
            String f = userDictionaryFile.get();
            dictionary.addAll(WORD_LIST_LOWERCASED.loadCachedFromFile(new File(f), "SpellingValidator user dictionary"));
        }
    }

}
