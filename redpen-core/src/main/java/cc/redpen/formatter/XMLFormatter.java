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
import cc.redpen.validator.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Optional;

/**
 * XML Output formatter.
 */
public class XMLFormatter extends Formatter {

    private static final Logger LOG = LoggerFactory.getLogger(XMLFormatter.class);
    private DocumentBuilder db;
    private final Transformer transformer;

    /**
     * Constructor.
     */
    public XMLFormatter() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            this.db = dbf.newDocumentBuilder();
            TransformerFactory tf = TransformerFactory.newInstance();
            this.transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerConfigurationException e) {
            LOG.error("Failed to create Transformer object");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String format(cc.redpen.model.Document document, ValidationError error) throws RedPenException {
        // create dom
        Document doc = db.newDocument();
        Element errorElement = doc.createElement("error");
        doc.appendChild(errorElement);

        Element validatorElement = doc.createElement("validator");
        errorElement.appendChild(validatorElement);
        Text validator = doc.createTextNode(error.getValidatorName());
        validatorElement.appendChild(validator);

        Element contentElement = doc.createElement("message");
        errorElement.appendChild(contentElement);
        Text content = doc.createTextNode(error.getMessage());
        contentElement.appendChild(content);

        document.getFileName().ifPresent(e -> {
            Element fileNameElement = doc.createElement("file");
            errorElement.appendChild(fileNameElement);
            Text fileName = doc.createTextNode(e);
            fileNameElement.appendChild(fileName);
        });

        Element lineNumberElement = doc.createElement("lineNum");
        errorElement.appendChild(lineNumberElement);
        Text lineNum = doc.createTextNode(Integer.toString(error.getLineNumber()));
        lineNumberElement.appendChild(lineNum);

        Element sentenceElement = doc.createElement("sentence");
        errorElement.appendChild(sentenceElement);
        sentenceElement.appendChild(doc.createTextNode(error.getSentence().content));

        // convert the result dom into a string
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(doc);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RedPenException(e);
        }
        return writer.toString();
    }

    @Override
    public Optional<String> header() {
        return Optional.of("<validation-result>");
    }

    @Override
    public Optional<String> footer() {
        return Optional.of("</validation-result>");
    }
}
