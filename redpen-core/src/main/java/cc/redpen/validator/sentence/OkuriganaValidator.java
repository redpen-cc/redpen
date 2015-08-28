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
import cc.redpen.validator.Validator;

import java.util.*;

// Checks if the Japanese input sentences contain the invalid Okurigana style.
public class OkuriganaValidator extends Validator {
    private static final Set<String> invalidOkurigana;

    static {
        invalidOkurigana = new HashSet<>();
        invalidOkurigana.add("押え");
        invalidOkurigana.add("恐し");
        invalidOkurigana.add("短か");
        invalidOkurigana.add("起ら");
        invalidOkurigana.add("起り");
        invalidOkurigana.add("起る");
        invalidOkurigana.add("起れ");
        invalidOkurigana.add("起ろ");
        invalidOkurigana.add("著い");
        invalidOkurigana.add("著るしい");
        invalidOkurigana.add("試る");
        invalidOkurigana.add("被ぶ");
        invalidOkurigana.add("紛わしい");
        invalidOkurigana.add("逆う");
        invalidOkurigana.add("悔ま");
        invalidOkurigana.add("陥いる");
        invalidOkurigana.add("恥かし");
        invalidOkurigana.add("恥し");
        invalidOkurigana.add("費さ");
        invalidOkurigana.add("費し");
        invalidOkurigana.add("費す");
        invalidOkurigana.add("費せ");
        invalidOkurigana.add("費そ");
        invalidOkurigana.add("憐ま");
        invalidOkurigana.add("憐み");
        invalidOkurigana.add("憐む");
        invalidOkurigana.add("生れ");
        invalidOkurigana.add("商なう");
        invalidOkurigana.add("憐め");
        invalidOkurigana.add("妨ない");
        invalidOkurigana.add("妨る");
        invalidOkurigana.add("悔む");
        invalidOkurigana.add("悔め");
        invalidOkurigana.add("進る");
        invalidOkurigana.add("進ない");
        invalidOkurigana.add("進る");
        invalidOkurigana.add("勧ない");
        invalidOkurigana.add("美い");
        invalidOkurigana.add("著い");
        invalidOkurigana.add("荒ら");
        invalidOkurigana.add("考る");
        invalidOkurigana.add("輝し");
        invalidOkurigana.add("静ず");
        invalidOkurigana.add("明か");
        invalidOkurigana.add("必ら");
        invalidOkurigana.add("再たび");
        invalidOkurigana.add("行なう");
        invalidOkurigana.add("表わ");
        invalidOkurigana.add("現わ");
        invalidOkurigana.add("著わ");
        invalidOkurigana.add("断わ");
        invalidOkurigana.add("積る");
        invalidOkurigana.add("聞え");
        invalidOkurigana.add("当る");
        invalidOkurigana.add("落さ");
        invalidOkurigana.add("落し");
        invalidOkurigana.add("落す");
        invalidOkurigana.add("落せ");
        invalidOkurigana.add("終る");
        invalidOkurigana.add("合さ");
        invalidOkurigana.add("合し");
        invalidOkurigana.add("合す");
        invalidOkurigana.add("合せ");
        invalidOkurigana.add("果さ");
        invalidOkurigana.add("果し");
        invalidOkurigana.add("果す");
        invalidOkurigana.add("果せ");
        invalidOkurigana.add("果そ");
        invalidOkurigana.add("帰る");
        invalidOkurigana.add("変る");
        invalidOkurigana.add("替る");
        invalidOkurigana.add("買る");
        invalidOkurigana.add("換る");
        invalidOkurigana.add("上る");
        invalidOkurigana.add("費す");
        invalidOkurigana.add("開らく");
        invalidOkurigana.add("危い");
        invalidOkurigana.add("危く");
        invalidOkurigana.add("甚し");
        invalidOkurigana.add("逸す");
        invalidOkurigana.add("逸さ");
        invalidOkurigana.add("逸せ");
        invalidOkurigana.add("反さ");
        invalidOkurigana.add("反し");
        invalidOkurigana.add("反す");
        invalidOkurigana.add("反そ");
        invalidOkurigana.add("懐ろ");
        invalidOkurigana.add("恥かし");
        invalidOkurigana.add("過す");
    }

    @Override
    public void validate(Sentence sentence) {
        invalidOkurigana.stream().forEach(value -> {
                    int startPosition = sentence.getContent().indexOf(value);
                    if (startPosition != -1) {
                        addValidationErrorWithPosition(sentence,
                                sentence.getOffset(startPosition),
                                sentence.getOffset(startPosition + value.length()),
                                value);
                    }
                }
        );

    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(Locale.JAPANESE.getLanguage());
    }

    @Override
    protected void init() throws RedPenException {
        // TODO: user dictionary
    }
}
