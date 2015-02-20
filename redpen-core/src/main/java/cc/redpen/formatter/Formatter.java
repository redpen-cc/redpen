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
package cc.redpen.formatter;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formatter - Format and write RedPen errors
 */
public abstract class Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);


    /**
     * Format and print the errors for a set of documents
     *
     * @param printWriter  The printwriter destination for the errors
     * @param docErrorsMap a map of documents to the corresponding list of errors
     * @throws RedPenException
     * @throws IOException
     */
    public abstract void format(PrintWriter printWriter, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException;

    /**
     * Format a single error as a string
     *
     * @param document the document the error is for
     * @param error    the error to format
     * @return A formatted error
     * @throws RedPenException
     */
    public abstract String formatError(Document document, ValidationError error) throws RedPenException;

    /**
     * Format and print the errors for a set of documents
     *
     * @param outputStream the output stream destination for the errors
     * @param docErrorsMap a map of documents to the corresponding list of errors
     * @throws RedPenException
     * @throws IOException
     */
    public void format(OutputStream outputStream, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException {
        format(new PrintWriter(outputStream), docErrorsMap);
    }

    /**
     * Format errors for a set of documents as a String
     *
     * @param docErrorsMap a map of documents to the corresponding list of errors
     * @return appropriately formatted errors per document
     */
    public String format(Map<Document, List<ValidationError>> docErrorsMap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            format(new PrintWriter(baos), docErrorsMap);
        } catch (RedPenException | IOException e) {
            // writing to ByteArrayOutputStream shouldn't fail with IOException
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * Format the errors for a given document
     *
     * @param document the document
     * @param errors   the list of errors for the document
     * @return an appropriately formatted group of errors for the document
     */
    public String format(Document document, List<ValidationError> errors) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<Document, List<ValidationError>> docErrorsMap = new HashMap<>();
        docErrorsMap.put(document, errors);
        try {
            format(new PrintWriter(baos), docErrorsMap);
        } catch (RedPenException | IOException e) {
            // writing to ByteArrayOutputStream shouldn't fail with IOException
            throw new RuntimeException(e);
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
