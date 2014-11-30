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
import cc.redpen.formatter.PlainFormatter;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * An implementation of ResultDistributor which flush the result into
 * given output stream.
 */
public class DefaultResultDistributor implements ResultDistributor {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultResultDistributor.class);
    private Formatter myFormatter;
    private BufferedWriter writer;

    /**
     * Constructor.
     *
     * @param os output stream
     */
    DefaultResultDistributor(OutputStream os) {
        super();
        if (os == null) {
            throw new IllegalArgumentException("argument OutputStream is null");
        }
        writer = new BufferedWriter(new PrintWriter(os));
        myFormatter = new PlainFormatter();
    }

    /**
     * Constructor.
     *
     * @param ps output stream
     */
    public DefaultResultDistributor(PrintStream ps) {
        if (ps == null) {
            throw new IllegalArgumentException("argument PrintStream is null");
        }
        writer = new BufferedWriter(new PrintWriter(ps));
        myFormatter = new PlainFormatter();
    }

    @Override
    public void setFormatter(Formatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("argument formatter is null");
        }
        this.myFormatter = formatter;
    }

    @Override
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
    void flushError(Document document, ValidationError err) {
        try {
            writer.write(myFormatter.convertError(document, err));
            writer.write("\n");
            writer.flush();
        } catch (IOException | RedPenException e) {
            LOG.error("failed to flush header", e);
        }
    }

    void flushHeader() {
        if (myFormatter.header().isPresent()) {
            try {
                writer.write(myFormatter.header().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to flush header", e);
            }
        }
    }

    void flushFooter() {
        if (myFormatter.footer().isPresent()) {
            try {
                writer.write(myFormatter.footer().get());
                writer.write("\n");
            } catch (IOException e) {
                LOG.error("failed to flush header", e);
            }
        }
    }

}
