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
package cc.redpen.validator.sentence;

import cc.redpen.RedPenException;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.ExpressionRule;
import cc.redpen.validator.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

// Checks if the Japanese input sentences contain the invalid Okurigana style.
public class OkuriganaValidator extends Validator {
    private static final Set<String> invalidOkurigana;
    private static final Set<ExpressionRule> invalidOkuriganaTokens;

    static {
        invalidOkuriganaTokens = new HashSet<>();
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("合さ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("合し", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("合す", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("合せ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("押え", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("押える", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("押えれ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("押えろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("押えよ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("起ら", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("起り", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("起る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("起れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("起ろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("起よ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("起", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("著かろ", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("著く", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("著かっ", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("著い", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("著けれ", asList("形容詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("試", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("試る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("試れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("試ろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("試よ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("恥しかろ", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("恥しく", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("恥しかっ", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("恥しい", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("恥しけれ", asList("形容詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("生れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("生れる", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("生れれ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("生れろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("生れよ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("妨", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("妨る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("妨れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("妨ろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("妨よ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("進", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("進る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("進れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("進ろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("進よ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("勧", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("勧る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("勧れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("勧ろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("勧よ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("考", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("考る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("考れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("考ろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("考よ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("行なわ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("行ない", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("行なう", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("行なえ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("行なお", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("表わさ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("表わし", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("表わす", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("表わせ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("表わそ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("現われ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("現われる", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("現われれ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("現われろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("現われよ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("断わら", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("断わり", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("断わる", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("断われ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("断わろ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("聞え", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("聞える", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("聞えれ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("聞えろ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("聞えよ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("当ら", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("当り", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("当る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("当れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("当ろ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("落さ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("落し", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("落す", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("落せ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("落そ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("終ら", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("終り", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("終る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("終れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("終ろ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("果さ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("果し", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("果す", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("果せ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("果そ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("変ら", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("変り", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("変る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("変れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("変ろ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("買", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("買る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("買れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("買よ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("上ら", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("上り", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("上る", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("上れ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("上ろ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("費さ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("費し", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("費す", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("費せ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("費そ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("危かろ", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("危く", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("危かっ", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("危い", asList("形容詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("危けれ", asList("形容詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("逸さ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("逸し", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("逸す", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("逸せ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("逸そ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("反さ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("反し", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("反す", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("反せ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("反そ", asList("動詞", "自立"), 0)));

        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("過さ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("過し", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("過す", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("過せ", asList("動詞", "自立"), 0)));
        invalidOkuriganaTokens.add(new ExpressionRule().addElement(new TokenElement("過そ", asList("動詞", "自立"), 0)));
    }

    static {
        invalidOkurigana = new HashSet<>();
        invalidOkurigana.add("恐し");
        invalidOkurigana.add("短か");
        invalidOkurigana.add("著るしい");
        invalidOkurigana.add("被ぶ");
        invalidOkurigana.add("紛わしい");
        invalidOkurigana.add("逆う");
        invalidOkurigana.add("悔ま");
        invalidOkurigana.add("陥いる");
        invalidOkurigana.add("恥かし");
        invalidOkurigana.add("憐ま");
        invalidOkurigana.add("憐み");
        invalidOkurigana.add("憐む");
        invalidOkurigana.add("憐め");
        invalidOkurigana.add("商なう");
        invalidOkurigana.add("美い");
        invalidOkurigana.add("荒ら");
        invalidOkurigana.add("輝し");
        invalidOkurigana.add("静ず");
        invalidOkurigana.add("明か");
        invalidOkurigana.add("必ら");
        invalidOkurigana.add("再たび");
        invalidOkurigana.add("著わ");
        invalidOkurigana.add("積る");
        invalidOkurigana.add("替る");
        invalidOkurigana.add("換る");
        invalidOkurigana.add("開らく");
        invalidOkurigana.add("甚し");
        invalidOkurigana.add("懐ろ");
    }

    @Override
    public void validate(Sentence sentence) {
        invalidOkurigana.stream().forEach(value -> {
                    int startPosition = sentence.getContent().indexOf(value);
                    if (startPosition != -1) {
                        addLocalizedErrorWithPosition(sentence,
                               startPosition,
                               startPosition + value.length(),
                                value);
                    }
                }
        );

        for (ExpressionRule rule : invalidOkuriganaTokens) {
            if (rule.match(sentence.getTokens())) {
                addLocalizedError(sentence, rule.toString());
            }
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }

    @Override
    protected void init() throws RedPenException {
        // TODO: user dictionary
    }
}
