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
package cc.redpen.validator.sentence;

import cc.redpen.DocumentValidator;
import cc.redpen.DocumentValidatorException;
import cc.redpen.ValidationError;
import cc.redpen.config.Character;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.distributor.FakeResultDistributor;
import cc.redpen.model.DocumentCollection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SpaceWithSymbolValidatorTest {
  @Test
  public void testNotNeedSpace() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("I like apple/orange", 1)
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
        .setCharacterTable("en")
        .setCharacter(new Character("SLASH", "/"))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(0, errors.size());
  }

  @Test
  public void testNeedAfterSpace() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("I like her:yes it is", 1)
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
        .setCharacterTable("en")
        .setCharacter(new Character("COLON", ":", "", false, true))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(1, errors.size());
  }

  @Test
  public void testNeedBeforeSpace() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("I like her(Nancy)very much.", 1)
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
        .setCharacterTable("en")
        .setCharacter(new Character("LEFT_PARENTHESIS", "(", "", true, false))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(1, errors.size());
  }

  @Test
  public void testNeedSpaceInMultiplePosition() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("I like her(Nancy)very much.", 1)
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
        .setCharacterTable("en")
        .setCharacter(new Character("LEFT_PARENTHESIS", "(", "", true, false))
        .setCharacter(new Character("RIGHT_PARENTHESIS", ")", "", false, true))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(2, errors.size());
  }

  @Test
  public void testReturnOnlyOneForHitBothBeforeAndAfter() throws DocumentValidatorException {
    DocumentCollection documents = new DocumentCollection.Builder()
        .addDocument("")
        .addSection(1, new ArrayList<>())
        .addParagraph()
        .addSentence("I like 1*10.", 1)
        .build();

    Configuration conf = new Configuration.Builder()
        .addValidatorConfig(new ValidatorConfiguration("SymbolWithSpace"))
        .setCharacterTable("en")
        .setCharacter(new Character("ASTARISK", "*", "", true, true))
        .build();

    DocumentValidator validator = new DocumentValidator.Builder()
        .setConfiguration(conf)
        .setResultDistributor(new FakeResultDistributor())
        .build();

    List<ValidationError> errors = validator.check(documents);
    assertEquals(1, errors.size());
  }
}
