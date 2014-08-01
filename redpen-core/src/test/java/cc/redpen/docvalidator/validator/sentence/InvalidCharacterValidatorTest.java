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
package cc.redpen.docvalidator.validator.sentence;

import cc.redpen.docvalidator.DocumentValidator;
import cc.redpen.docvalidator.DocumentValidatorException;
import cc.redpen.docvalidator.ValidationError;
import cc.redpen.docvalidator.config.Character;
import cc.redpen.docvalidator.config.Configuration;
import cc.redpen.docvalidator.config.ValidatorConfiguration;
import cc.redpen.docvalidator.distributor.FakeResultDistributor;
import cc.redpen.docvalidator.model.DocumentCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InvalidCharacterValidatorTest {
  @Test
  public void testWithInvalidCharacter() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("わたしはカラオケが大好き！", 1)
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidCharacter"))
        .setCharacterTable("en")
        .setCharacter(new Character("EXCLAMATION_MARK", "!", "！"))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(1, errors.size());
  }

  @Test
  public void testWithoutInvalidCharacter() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("I like Karaoke", 1)
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidCharacter"))
        .setCharacterTable("en")
        .setCharacter(new Character("EXCLAMATION_MARK", "!", "！"))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(0, errors.size());
  }

  @Test
  public void testWithoutMultipleInvalidCharacter() throws DocumentValidatorException {

    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("わたしは、カラオケが大好き！", 1) // NOTE: two invalid characters
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("InvalidCharacter"))
        .setCharacterTable("en")
        .setCharacter(new Character("EXCLAMATION_MARK", "!", "！"))
        .setCharacter(new Character("COMMA", ",", "、"))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(2, errors.size());
  }
}
