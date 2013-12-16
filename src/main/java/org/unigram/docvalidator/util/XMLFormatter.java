package org.unigram.docvalidator.util;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

public class XMLFormatter implements Formatter {

  public XMLFormatter() throws DocumentValidatorException {
    super();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      this.db = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new DocumentValidatorException(e.getMessage());
    }
  }

  @Override
  public String convertError(ValidationError error) {
    // create dom
    Document doc = db.newDocument();
    Element errorElement = doc.createElement("error");
    doc.appendChild(errorElement);

    if (error.getMessage()!= null && !error.getMessage().equals("")) {
      Element contentElement = doc.createElement("content");
      errorElement.appendChild(contentElement);
      Text content = doc.createTextNode(error.getMessage());
      contentElement.appendChild(content);
    }

    if (error.getFileName() != null && !error.getFileName().equals("")) {
      Element fileNameElement = doc.createElement("file");
      errorElement.appendChild(fileNameElement);
      Text fileName = doc.createTextNode(error.getFileName());
      fileNameElement.appendChild(fileName);
    }

    Element lineNumberElement = doc.createElement("lineNum");
    errorElement.appendChild(lineNumberElement);
    Text lineNum = doc.createTextNode(Integer.toString(error.getLineNumber()));
    lineNumberElement.appendChild(lineNum);

    // create a transformer
    Transformer transformer = createTransformer();
    if (transformer == null) {
      throw new IllegalStateException("Failed to create XML Transformer");
    }

    // convert the result dom into a string
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    DOMSource source = new DOMSource(doc);
    try {
      transformer.transform(source, result);
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Transformer createTransformer()
      throws TransformerFactoryConfigurationError {
    TransformerFactory tf = TransformerFactoryImpl.newInstance();
    Transformer transformer = null;
    try {
      transformer = tf.newTransformer();
    } catch (TransformerConfigurationException e) {
      return null;
    }
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    return transformer;
  }

  @Override
  public String header() {
    return "<validation-result>";
  }

  @Override
  public String footer() {
    return "</validation-result>";
  }

  private DocumentBuilder db;

}
