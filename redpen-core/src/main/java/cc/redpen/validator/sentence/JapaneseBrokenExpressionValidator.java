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

import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.tokenizer.NeologdJapaneseTokenizer;
import cc.redpen.validator.Validator;

import java.util.List;
import java.util.Locale;

import static java.util.Collections.singletonList;

/**
 * JapaneseBrokenExpressionValidator detects certain forms of "broken" japanese expressions.
 * <br>
 * Note: this validator works only for Japanese texts.
 */
public class JapaneseBrokenExpressionValidator extends Validator {
    @Override
    public void validate(Sentence sentence) {
        final List<TokenElement> tokens = sentence.getTokens();

        for (int i = 0; i < (tokens.size() - 1); ++i) {
            final TokenElement p = tokens.get(i);
            final List<String> ptags = p.getTags();
            System.out.println( p.getSurface() );
            System.out.println( ptags );
            if (ptags.get(0).equals("動詞") && ptags.get(1).equals("自立") && ptags.get(2).equals("一段") ) {
                if( ptags.get(3).equals("未然形") )
                {
                    final TokenElement q = tokens.get(i+1);
                    final List<String> qtags = q.getTags();
                    System.out.println(qtags);
                    System.out.println(q.getBaseForm());
                    if (qtags.get(0).equals("動詞") && qtags.get(1).equals("接尾") && q.getBaseForm().equals("れる")) {
                        addLocalizedError(sentence, p.getSurface());
                        continue;
                    }
                }
                System.out.println( p.getBaseForm() );
                if( p.getBaseForm().endsWith("れる") )
                {
                    if( p.getBaseForm().endsWith("られる") )
                    {
                        // need to check "Ra-Ire" error
                        continue;
                    }
                    else
                    {
                        String nverb = p.getBaseForm().replaceFirst("れる$","る");
                        System.out.println(nverb);
                        NeologdJapaneseTokenizer tokenizer = new NeologdJapaneseTokenizer();
                        List<TokenElement> t = tokenizer.tokenize(nverb);
                        String inflectionType = t.get(0).getTags().get(2);
                        if( inflectionType.startsWith("一段") || inflectionType.startsWith("カ変") )
                        {
                            addLocalizedError(sentence, p.getSurface());
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> getSupportedLanguages() {
        return singletonList(Locale.JAPANESE.getLanguage());
    }
}
