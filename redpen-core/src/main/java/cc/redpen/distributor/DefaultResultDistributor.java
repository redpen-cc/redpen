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

import java.io.*;

/**
 * An implementation of ResultDistributor which flush the result into
 * given output stream.
 */
public class DefaultResultDistributor implements ResultDistributor {
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

    /**
     * Output given validation error.
     *
     * @param err validation error
     */
    public void flushError(Document document, ValidationError err) throws RedPenException {
        try {
            writer.write(myFormatter.convertError(document, err));
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RedPenException(e);
        }
    }

    @Override
    public void flushHeader() throws RedPenException{
        if (myFormatter.header().isPresent()) {
            try {
                writer.write(myFormatter.header().get());
                writer.write("\n");
            } catch (IOException e) {
                throw new RedPenException(e);
            }
        }
    }

    @Override
    public void flushFooter() throws RedPenException {
        if (myFormatter.footer().isPresent()) {
            try {
                writer.write(myFormatter.footer().get());
                writer.write("\n");
            } catch (IOException e) {
                throw new RedPenException(e);
            }
        }
    }

    @Override
    public void setFormatter(Formatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("argument formatter is null");
        }
        this.myFormatter = formatter;
    }

}
