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

import java.util.*;

/**
 * Spelling utility
 */
public class SpellingUtils {

    private static final String DEFAULT_LANGUAGE = "en";

    // a map of dictionaries per language
    private static Map<String, Set<String>> dictionaries = new HashMap<>();
    private static final String DEFAULT_RESOURCE_PATH = "default-resources/spellchecker";

    protected static void loadDictionary(String lang) {
        if (dictionaries.get(lang) == null) {
            String defaultDictionaryFile = DEFAULT_RESOURCE_PATH + "/spellchecker-" + lang + ".dat";
            Set<String> dictionary = new DictionaryLoader<Set<String>>(HashSet::new, (set, line) -> set.add(line.toLowerCase()))
              .loadCachedFromResource(defaultDictionaryFile, "spell dictionary");
            dictionaries.put(lang, Collections.unmodifiableSet(dictionary));
        }
    }

    /**
     * Get the default dictionary entries for the specified language
     *
     * @param lang language to retrieve
     * @return default dictionary for the specified language
     */
    public static Set<String> getDictionary(String lang) {
        loadDictionary(lang == null ? DEFAULT_LANGUAGE : lang);
        return dictionaries.get(lang);
    }

}
