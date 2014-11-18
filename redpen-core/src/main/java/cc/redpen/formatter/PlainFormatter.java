/**
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
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
package cc.redpen.formatter;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;

import java.util.Optional;

/**
 * Format input error into a string message.
 */
public class PlainFormatter implements Formatter {
    private static final Optional<String> HEADER = Optional.empty();
    private static final Optional<String> FOOTER = Optional.empty();

    @Override
    public String convertError(Document document, ValidationError error) throws RedPenException {
        StringBuilder str = new StringBuilder();

        str.append("ValidationError[");
        str.append(error.getValidatorName());
        str.append("][");
        document.getFileName().ifPresent(e -> str.append(e).append(" : "));
        str.append(error.getLineNumber()).append(" (")
                .append(error.getMessage()).append(")]");
        str.append(" at line: ").append(error.getSentence().content);
        return str.toString();
    }

    @Override
    public Optional<String> header() {
        return HEADER;
    }

    @Override
    public Optional<String> footer() {
        return FOOTER;
    }

}
