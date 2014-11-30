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
package cc.redpen.distributor;

import cc.redpen.RedPenException;
import cc.redpen.formatter.Formatter;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * ResultDistributor flush the errors reported from Validators.
 */
public class ResultDistributor {
    private static final Logger LOG = LoggerFactory.getLogger(ResultDistributor.class);
    private Formatter formatter;
    private BufferedWriter writer;

    /**
     * Constructor.
     *
     * @param os output stream
     */
    ResultDistributor(OutputStream os, Formatter formatter) {
        writer = new BufferedWriter(new PrintWriter(os));
        this.formatter = formatter;
    }

    /**
     * Constructor.
     *
     * @param ps output stream
     */
    public ResultDistributor(PrintStream ps, Formatter formatter) {
        if (ps == null) {
            throw new IllegalArgumentException("argument PrintStream is null");
        }
        writer = new BufferedWriter(new PrintWriter(ps));
        this.formatter = formatter;
    }

    public void distribute(Map<Document, List<ValidationError>> docErrorsMap) {
        flushHeader();

        docErrorsMap.keySet().forEach(document -> {
            List<ValidationError> errors = docErrorsMap.get(document);
            for (ValidationError error : errors) {
                flushError(document, error);
            }
        });
        flushFooter();
    }

    /**
     * Output given validation error.
     *
     * @param err validation error
     */
    private void flushError(Document document, ValidationError err) {
        try {
            writer.write(formatter.format(document, err));
            writer.write("\n");
            writer.flush();
        } catch (IOException | RedPenException e) {
            LOG.error("failed to flush error", e);
        }
    }

    private void flushHeader() {
        if (formatter.header().isPresent()) {
            try {
                writer.write(formatter.header().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to flush header", e);
            }
        }
    }

    private void flushFooter() {
        if (formatter.footer().isPresent()) {
            try {
                writer.write(formatter.footer().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to flush header", e);
            }
        }
    }
}
