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

import cc.redpen.ValidationError;

/**
 * Format input error into a string message.
 */
public class PlainFormatter implements Formatter {

    @Override
    public String convertError(ValidationError error) {
        StringBuilder str = new StringBuilder();

        str.append("ValidationError[");
        str.append(error.getValidatorName());
        str.append("][");
        error.getFileName().ifPresent(e -> str.append(e).append(" : "));
        str.append(error.getLineNumber()).append(" (")
                .append(error.getMessage()).append(")]");
        error.getSentence().ifPresent(e -> str.append(" at line: ").append(e.content));
        return str.toString();
    }

    @Override
    public String header() {
        return null;
    }

    @Override
    public String footer() {
        return null;
    }

}
