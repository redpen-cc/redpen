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
 * ResultDistributor flush the errors reported from Validators.
 */
public abstract class Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);

    public abstract void format(PrintWriter pw, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException;

    public void format(OutputStream os, Map<Document, List<ValidationError>> docErrorsMap) throws RedPenException, IOException {
        format(new PrintWriter(os), docErrorsMap);
    }

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
