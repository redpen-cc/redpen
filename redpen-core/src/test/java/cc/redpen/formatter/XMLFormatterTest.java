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
import cc.redpen.model.Sentence;
import cc.redpen.tokenizer.WhiteSpaceTokenizer;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class XMLFormatterTest extends Validator {

    @Test
    public void testConvertValidationError() throws RedPenException {
        ValidationError error =createValidationError(new Sentence("This is a sentence", 0));
        XMLFormatter formatter = createXMLFormatter();
        cc.redpen.model.Document document1 = new cc.redpen.model.Document.DocumentBuilder(new WhiteSpaceTokenizer())
                .setFileName("foobar.md").build();
        String resultString = formatter.writeError(document1, error);

        Document document = extractDocument(resultString);
        assertEquals(1, document.getElementsByTagName("error").getLength());
        assertEquals(1, document.getElementsByTagName("message").getLength());
        assertEquals("Fatal Error",
                document.getElementsByTagName("message").item(0).getTextContent());
        assertEquals(1, document.getElementsByTagName("file").getLength());
        assertEquals("foobar.md",
                document.getElementsByTagName("file").item(0).getTextContent());
        assertEquals(1, document.getElementsByTagName("lineNum").getLength());
        assertEquals("0",
                document.getElementsByTagName("lineNum").item(0).getTextContent());
        assertEquals(1, document.getElementsByTagName("sentence").getLength());
        assertEquals("This is a sentence",
                document.getElementsByTagName("sentence").item(0).getTextContent());
        assertEquals(1, document.getElementsByTagName("validator").getLength());
        assertEquals(this.getClass().getSimpleName(),
                document.getElementsByTagName("validator").item(0).getTextContent());
    }

    @Test
    public void testConvertValidationErrorWithoutFileName() throws RedPenException {
        ValidationError error =createValidationError(new Sentence("text", 0));
        XMLFormatter formatter = createXMLFormatter();
        String resultString = formatter.writeError(
                new cc.redpen.model.Document.DocumentBuilder(new WhiteSpaceTokenizer()).build(), error);
        Document document = extractDocument(resultString);
        assertEquals(1, document.getElementsByTagName("error").getLength());
        assertEquals(1, document.getElementsByTagName("message").getLength());
        assertEquals("Fatal Error",
                document.getElementsByTagName("message").item(0).getTextContent());
        assertEquals(0, document.getElementsByTagName("file").getLength());
        assertEquals(1, document.getElementsByTagName("lineNum").getLength());
        assertEquals("0",
                document.getElementsByTagName("lineNum").item(0).getTextContent());
        assertEquals(1, document.getElementsByTagName("validator").getLength());
        assertEquals(this.getClass().getSimpleName(),
                document.getElementsByTagName("validator").item(0).getTextContent());
    }

    @Test
    public void testConvertValidationErrorWithoutLineNumAndFileName() throws RedPenException {
        ValidationError error =createValidationError(new Sentence("text", -1));
        XMLFormatter formatter = createXMLFormatter();
        String resultString = formatter.writeError(
                new cc.redpen.model.Document.DocumentBuilder(new WhiteSpaceTokenizer()).build(), error);

        Document document = extractDocument(resultString);
        assertEquals(1, document.getElementsByTagName("error").getLength());
        assertEquals(1, document.getElementsByTagName("message").getLength());
        assertEquals("Fatal Error",
                document.getElementsByTagName("message").item(0).getTextContent());
        assertEquals(0, document.getElementsByTagName("file").getLength());
        assertEquals(1, document.getElementsByTagName("lineNum").getLength());
        assertEquals("-1",
                document.getElementsByTagName("lineNum").item(0).getTextContent());
        assertEquals(1, document.getElementsByTagName("validator").getLength());
        assertEquals(this.getClass().getSimpleName(),
                document.getElementsByTagName("validator").item(0).getTextContent());
    }

    private Document extractDocument(String resultString) {
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            fail();
            e.printStackTrace();
        }

        Document document = null;
        try {
            document = docBuilder.parse(new ByteArrayInputStream(resultString.getBytes(StandardCharsets.UTF_8)));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            fail();
        }
        return document;
    }

    private XMLFormatter createXMLFormatter() {
        return new XMLFormatter();
    }

}
