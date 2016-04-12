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
import cc.redpen.util.StringUtils;
import cc.redpen.validator.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static cc.redpen.config.SymbolType.*;
import static java.util.Collections.singletonList;

public class JapaneseNumberExpressionValidator extends Validator {
    private final List<Pattern> patternsNumeric = Arrays.asList(
        Pattern.compile("[一二三四五六七八九０-９][一二三四五六七八九０-９.．〜、]*[つの]"),
        Pattern.compile("(ひと|ふた|[みよむや]っ|いつ|ここの)つ")
    );
    private final List<Pattern> patternsNumericZenkaku = Arrays.asList(
        Pattern.compile("[一二三四五六七八九0-9][一二三四五六七八九0-9.．〜、]*[つの]"),
        Pattern.compile("(ひと|ふた|[みよむや]っ|いつ|ここの)つ")
    );
    private final List<Pattern> patternsKansuji = Arrays.asList(
        Pattern.compile("[0-9０-９][0-9０-９.．〜、]*[つの]"),
        Pattern.compile("(ひと|ふた|[みよむや]っ|いつ|ここの)つ")
    );
    private final List<Pattern> patternsCounting = Arrays.asList(
        Pattern.compile("[一二三四五六七八九0-9０-９][一二三四五六七八九0-9０-９.．〜、]*[つの]")
    );

    public JapaneseNumberExpressionValidator() {
        super("mode", "numeric");
    }

    @Override public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }

    @Override
    public void validate(Sentence sentence) {
        for (Pattern pat : patternsOfCurrentMode()) {
            final Matcher m = pat.matcher(sentence.getContent());
            while (m.find()) {
                addLocalizedError(sentence, m.group(0));
            }
        }
    }

    private List<Pattern> patternsOfCurrentMode() {
        switch (getString("mode")) {
        case "numeric":
            return patternsNumeric;
        case "numeric-zenkaku":
            return patternsNumericZenkaku;
        case "kansuji":
            return patternsKansuji;
        case "counting":
            return patternsCounting;
        default:
            throw new RuntimeException(String.format("unknown mode: %s", getString("mode")));
        }
    }
}
