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
package cc.redpen.validator.document;

import cc.redpen.model.Paragraph;
import cc.redpen.model.Document;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.TokenElement;
import cc.redpen.validator.Validator;

import java.util.*;

/**
 * GappedSectionValidator detects gaps among section levels.
 */
final public class GappedSectionValidator extends Validator {
    @Override
    public void validate(Document document) {
        int seen = 0;
        for (Section s: document) {
            final int current = s.getLevel();
            if (seen > 0) {
                if (current <= seen) {
                    seen = current;
                } else {
                    if (current == seen + 1) {
                        seen = current;
                    } else {
                        addLocalizedError(s.getJoinedHeaderContents(), s.getJoinedHeaderContents().getContent(), current, seen + 1);
                    }
                }
            } else {
                seen = current;
            }
        }
    }
}
