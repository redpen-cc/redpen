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
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.DictionaryValidator;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.util.Collections.singletonList;

public final class LongKanjiChainValidator extends DictionaryValidator {
    private final String shard = "[\\u4e00-\\u9faf]{%d,}";

    private Pattern pat;

    public LongKanjiChainValidator() {
        super("long-kanji-chain/long-kanji-chain-skiplist");
        addDefaultProperties("max_len", 5); // do not report words shorter than this
    }

    @Override protected void init() throws RedPenException {
        if (getSymbolTable().getLang().equals("ja") && !getConfigAttribute("max_len").isPresent())
            getProperties().put("max_len", 5);

        pat = Pattern.compile(String.format(shard, getInt("max_len") + 1));
    }

    @Override
    public void validate(Sentence sentence) {
        final Matcher m = pat.matcher(sentence.getContent());

        while (m.find()) {
            final String word = m.group(0);
            if (!inDictionary(word)) {
                addLocalizedError(sentence, word, word.length());
            }
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
