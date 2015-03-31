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

import cc.redpen.formatter.*;

/**
 * Formatter utilities
 */
public class FormatterUtils {
    /**
     * Return a new formatter instance from its simple (command line) name
     *
     * @param formatName the simple formatter name, ie: json, plain
     * @return a formatter that matches the name, or null if no such formatter is known
     */
    public static Formatter getFormatterByName(String formatName) {
        switch (formatName.toLowerCase()) {
            case "xml":
                return new XMLFormatter();
            case "plain":
                return new PlainFormatter();
            case "plain2":
                return new PlainBySentenceFormatter();
            case "json":
                return new JSONFormatter();
            case "json2":
                return new JSONBySentenceFormatter();
        }
        return null;
    }
}
