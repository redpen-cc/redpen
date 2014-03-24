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
package org.unigram.docvalidator;

import org.apache.commons.io.IOUtils;
import org.unigram.docvalidator.parser.DocumentParserFactory;
import org.unigram.docvalidator.parser.Parser;
import org.unigram.docvalidator.model.DocumentCollection;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidatorConfiguration;

import java.io.InputStream;

/**
 * Generate DocumentCollection objects from String. This class are applied
 * only for testing purpose.
 */
public class SampleDocumentGenerator {
  /**
   * Given a string and the syntax type, build a DocumentCollection object.
   * This build method is made to write test easily, but this generator
   * class does not supports the configurations if the configurations are
   * needed please use DocumentGenerator class.
   *
   * @param docString input document string
   * @param type document syntax: wiki, markdown or plain
   * @return DocumentCollection object
   */
  public static DocumentCollection generateOneFileDocument(String docString,
      String type) throws DocumentValidatorException {
    DVResource resource = new DVResource(
        new ValidatorConfiguration("dummy"), new CharacterTable());
    Parser parser = DocumentParserFactory.generate(type, resource);

    InputStream stream = IOUtils.toInputStream(docString);
    DocumentCollection documentCollection = new DocumentCollection();
    documentCollection.addDocument(parser.generateDocument(stream));
    return documentCollection;
  }
}
