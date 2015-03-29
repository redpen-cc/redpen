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
package cc.redpen.util;

import cc.redpen.RedPenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * WordListExtractor extracts word from a given line. This class is called from
 * FileLoader.
 */
public class WordListExtractor extends ResourceExtractor<Set<String>> {
    private static final Logger LOG = LoggerFactory.getLogger(WordListExtractor.class);

    /**
     * @param toLowerCase set to true if inputs need to be lowercased.
     */
    public WordListExtractor(boolean toLowerCase) {
        super(HashSet::new, (set, line) ->{
            if (toLowerCase) {
                line = line.toLowerCase();
            }
            set.add(line);
        });
    }

    public WordListExtractor() {
        this(false);
    }

    private final static Map<String, Set<String>> wordListCache = new HashMap<>();

    /**
     * returns word list loaded from resource
     * @param path resource path
     * @param dictionaryName name of the resource
     * @param toLowerCase words will be lowercased if set to true
     * @return word list
     * @throws RedPenException
     */
    public static Set<String> loadWordListFromResource(String path, String dictionaryName, boolean toLowerCase) throws RedPenException {
        Set<String> strings = wordListCache.computeIfAbsent(path, e -> {
            WordListExtractor extractor = new WordListExtractor(toLowerCase);
            try {
                return Collections.unmodifiableSet(extractor.loadFromResource(path));
            } catch (IOException ioe) {
                LOG.error(ioe.getMessage());
                return null;
            }
        });
        if (strings == null) {
            throw new RedPenException("Failed to load " + dictionaryName + ":" + path);
        }
        LOG.info("Succeeded to load " + dictionaryName + ".");
        return strings;
    }
}
