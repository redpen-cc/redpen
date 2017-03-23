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
package cc.redpen.validator.section;

import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.validator.Validator;

import java.util.List;

public class HeaderLengthValidator extends Validator {

    public HeaderLengthValidator() {
        super("max_len", 20); // Default maximum length of section headers
    }

    @Override
    public void validate(Section section) {
        List<Sentence> headerContents = section.getHeaderContents();
        int headerLength = headerContents.stream().map(s -> s.getContent().length()).reduce(0, (s, r) -> s + r);
        if (headerLength > getInt("max_len")) {
            addLocalizedError(section.getJoinedHeaderContents(), getInt("max_num"));
        }
    }

}
