/*
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
package cc.redpen.validator;

import cc.redpen.RedPenException;
import cc.redpen.util.DictionaryLoader;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class KeyValueDictionaryValidator extends Validator {
    protected DictionaryLoader<Map<String, String>> loader = KEY_VALUE;
    private String dictionaryPrefix;
    private Map<String, String> dictionary = emptyMap();

    public KeyValueDictionaryValidator() {
        super("map", new HashMap<>(), "dict", "");
    }

    public KeyValueDictionaryValidator(Object...keyValues) {
        this();
        addDefaultProperties(keyValues);
    }

    public KeyValueDictionaryValidator(String dictionaryPrefix) {
        this();
        this.dictionaryPrefix = dictionaryPrefix;
    }

    @Override
    protected void init() throws RedPenException {
        if (dictionaryPrefix != null) {
            String defaultDictionaryFile = "default-resources/" + dictionaryPrefix + "-" + getSymbolTable().getLang() + ".dat";
            dictionary = loader.loadCachedFromResource(defaultDictionaryFile, getClass().getSimpleName() + " default dictionary");
        }
        String confFile = getString("dict");
        if (isNotEmpty(confFile)) {
            getMap("map").putAll(loader.loadCachedFromFile(findFile(confFile), getClass().getSimpleName() + " user dictionary"));
        }
    }

    protected boolean inDictionary(String word) {
        Map<String, String> customDictionary = getMap("map");
        return dictionary.containsKey(word) || customDictionary != null && customDictionary.containsKey(word);
    }

    protected String getValue(String word) {
        Map<String, String> customDictionary = getMap("map");
        if (customDictionary != null && customDictionary.containsKey(word)) {
            return customDictionary.get(word);
        } else if (this.dictionary.containsKey(word)) {
            return dictionary.get(word);
        }
        return null;
    }
}
