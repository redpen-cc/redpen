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
import cc.redpen.formatter.PlainFormatter;
import cc.redpen.model.Document;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultResultDistributorTest extends Validator {
    @Test
    public void testFlushHeaderWithPlainFormatter() throws RedPenException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        distributor.flushFooter();
        String result = new String(os.toByteArray(), StandardCharsets.UTF_8);
        assertEquals("", result);
    }

    @Test
    public void testFlushFooterWithPlainFormatter() throws RedPenException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        distributor.flushFooter();
        String result = new String(os.toByteArray(), StandardCharsets.UTF_8);
        assertEquals("", result);
    }

    @Test
    public void testFlushErrorWithPlainFormatter() throws RedPenException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        ValidationError error = createValidationError(new Sentence("sentence", 1));
        distributor.flushError(new Document.DocumentBuilder().build(), error);
        String result = new String(os.toByteArray(), StandardCharsets.UTF_8);
        Pattern p = Pattern.compile("foobar");
        Matcher m = p.matcher(result);
        assertTrue(m.find());
    }

    @Test(expected = NullPointerException.class)
    public void testFlushErrorWithPlainFormatterForNull() throws RedPenException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        distributor.flushError(new Document.DocumentBuilder().build(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePlainFormatterNullStream() {
        DefaultResultDistributor distributor = new DefaultResultDistributor(null);
        distributor.setFormatter(new PlainFormatter());
    }

    @Override
    public void validate(List<ValidationError> errors, Sentence sentence) {
    }
}
