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
package org.unigram.docvalidator.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.DocumentValidatorException;
import org.unigram.docvalidator.ValidationError;
import org.unigram.docvalidator.model.Document;
import org.unigram.docvalidator.model.ListBlock;
import org.unigram.docvalidator.model.ListElement;
import org.unigram.docvalidator.model.Paragraph;
import org.unigram.docvalidator.model.Section;
import org.unigram.docvalidator.model.Sentence;
import org.unigram.docvalidator.util.*;
import org.unigram.docvalidator.validator.sentence.CommaNumberValidator;
import org.unigram.docvalidator.validator.sentence.InvalidCharacterValidator;
import org.unigram.docvalidator.validator.sentence.InvalidExpressionValidator;
import org.unigram.docvalidator.validator.sentence.KatakanaEndHyphenValidator;
import org.unigram.docvalidator.validator.sentence.KatakanaSpellCheckValidator;
import org.unigram.docvalidator.validator.sentence.SentenceLengthValidator;
import org.unigram.docvalidator.validator.sentence.SentenceValidator;
import org.unigram.docvalidator.validator.sentence
  .SpaceBeginningOfSentenceValidator;
import org.unigram.docvalidator.validator.sentence.SuggestExpressionValidator;
import org.unigram.docvalidator.validator.sentence.SymbolWithSpaceValidator;
import org.unigram.docvalidator.validator.sentence.WordNumberValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Validator for input sentences. Sentence iterator calls appended
 * SentenceValidators and validate the input using the validators.
 */
public class SentenceIterator implements Validator {
  /**
   * constructor.
   *
   * @throws org.unigram.docvalidator.DocumentValidatorException
   */
  public SentenceIterator() throws DocumentValidatorException {
    this.sentenceValidators = new ArrayList<SentenceValidator>();
    this.distributor = new FakeResultDistributor();
  }

  public SentenceIterator(ValidatorConfiguration conf, CharacterTable
    charTable) throws DocumentValidatorException {
    this();
    loadConfiguration(conf, charTable);
  }

  public void setResultDistributor(ResultDistributor distributor) {
    this.distributor = distributor;
  }

  public List<ValidationError> validate(Document file) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    for (SentenceValidator validator : this.sentenceValidators) {
      for (Section section : file) {
        checkSection(distributor, errors, validator, section, file.getFileName());
      }
    }
    return errors;
  }

  private boolean loadConfiguration(ValidatorConfiguration conf,
                                    CharacterTable charTable)
    throws DocumentValidatorException {
    for (ValidatorConfiguration currentConfiguration : conf.getChildren()) {
      SentenceValidator validator = loadValidator(charTable, currentConfiguration);
      this.sentenceValidators.add(validator);
    }
    return true;
  }

  private SentenceValidator loadValidator(CharacterTable charTable,
                                          ValidatorConfiguration
                                            currentConfiguration) throws
    DocumentValidatorException {
    String confName = currentConfiguration.getConfigurationName();

    DVResource resource = new DVResource(currentConfiguration, charTable);
    SentenceValidator validator;
    if (confName.equals("SentenceLength")) {
      validator = new SentenceLengthValidator(resource);
    } else if (confName.equals("InvalidExpression")) {
      validator = new InvalidExpressionValidator(resource);
    } else if (confName.equals("SpaceAfterPeriod")) {
      validator = new SpaceBeginningOfSentenceValidator(resource);
    } else if (confName.equals("CommaNumber")) {
      validator = new CommaNumberValidator(resource);
    } else if (confName.equals("WordNumber")) {
      validator = new WordNumberValidator(resource);
    } else if (confName.equals("SuggestExpression")) {
      validator = new SuggestExpressionValidator(resource);
    } else if (confName.equals("InvalidCharacter")) {
      validator = new InvalidCharacterValidator(resource);
    } else if (confName.equals("SpaceWithSymbol")) {
      validator = new SymbolWithSpaceValidator(resource);
    } else if (confName.equals("KatakanaEndHyphen")) {
      validator = new KatakanaEndHyphenValidator(resource);
    } else if (confName.equals("KatakanaSpellCheckValidator")) {
      validator = new KatakanaSpellCheckValidator(resource);
    } else {
      throw new DocumentValidatorException(
        "There is no validator like " + confName);
    }

    return validator;
  }

  private void checkSection(ResultDistributor distributor,
                            List<ValidationError> errors,
                            SentenceValidator validator,
                            Section currentSection, String fileName) {
    checkParagraphs(distributor, errors, validator, currentSection, fileName);
    checkHeaders(distributor, errors, validator, currentSection, fileName);
    checkListElements(distributor, errors, validator, currentSection, fileName);
  }

  private void checkParagraphs(ResultDistributor distributor,
                               List<ValidationError> errors,
                               SentenceValidator validator,
                               Section currentSection, String fileName) {
    for (Paragraph paragraph : currentSection.getParagraphs()) {
      for (Sentence sentence : paragraph.getSentences()) {
        applyValidator(distributor, errors, validator, fileName, sentence);
      }
    }
  }

  private void checkHeaders(ResultDistributor distributor,
                            List<ValidationError> errors,
                            SentenceValidator validator,
                            Section currentSection, String fileName) {
    for (Iterator<Sentence> iterator = currentSection.getHeaderContents();
         iterator.hasNext(); ) {
      applyValidator(distributor, errors, validator, fileName, iterator.next());
    }
  }

  private void checkListElements(ResultDistributor distributor,
                                 List<ValidationError> errors,
                                 SentenceValidator validator,
                                 Section currentSection, String fileName) {

    for (ListBlock listBlock : currentSection.getListBlocks()) {
      for (ListElement listElement : listBlock.getListElements()) {
        for (Sentence sentence : listElement.getSentences()) {
          applyValidator(distributor, errors, validator, fileName, sentence);
        }
      }
    }
  }

  private void applyValidator(ResultDistributor distributor,
                              List<ValidationError> errors,
                              SentenceValidator validator,
                              String fileName, Sentence sentence) {
    List<ValidationError> validationErrors;
    try {
      validationErrors =
        validator.validate(sentence);
    } catch (Throwable e) {
      //TODO add validator type info
      LOG.error("Error in checking sentence: \"" + sentence.content + "\"");
      return;
    }
    for (ValidationError validationError : validationErrors) {
      appendError(distributor, errors, fileName, validationError);
    }
  }

  protected void addSentenceValidator(SentenceValidator sentenceValidator) {
    this.sentenceValidators.add(sentenceValidator);
  }

  private void appendError(ResultDistributor distributor,
                           List<ValidationError> errors, String fileName,
                           ValidationError e) {
    if (e != null) {
      //NOTE: fileName is not specified in validators to reduce the task of them
      e.setFileName(fileName);
      distributor.flushResult(e);
      errors.add(e);
    }
  }

  private final List<SentenceValidator> sentenceValidators;

  private ResultDistributor distributor;

  private static final Logger LOG =
    LoggerFactory.getLogger(SentenceIterator.class);
}
