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
 * This interface is for classes to define output format of
 * reported ValidationError objects.
 */
public interface Formatter {
    /**
     * Convert ValidationError into a string to flush a error message.
     *
     * @param document document associated with the validation error
     * @param error    object containing file and line number information.
     * @return error message
     */
    String format(Document document, ValidationError error) throws RedPenException;

    /**
     * Return the header block of semi-structured format. Returns empty by default.
     *
     * @return header block
     */
    default Optional<String> header() {
        return Optional.empty();
    }

    /**
     * Return the footer block of semi-structured format. Returns empty by default.
     *
     * @return footer block
     */
    default Optional<String> footer() {
        return Optional.empty();
    }

    static Formatter getFormatter(String type) {
        switch (Type.valueOf(type.toUpperCase())) {
            case XML:
                return new XMLFormatter();
            case PLAIN:
                return new PlainFormatter();
            default:
                throw new IllegalArgumentException("Unsupported format:" + type);
        }
    }

    /**
     * the type of formatter using ResultDistributorFactory.
     */
    enum Type {
        PLAIN,
        XML
    }
}
