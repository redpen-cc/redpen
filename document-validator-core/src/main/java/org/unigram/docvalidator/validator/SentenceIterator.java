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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.model.Document;
import org.unigram.docvalidator.model.ListBlock;
import org.unigram.docvalidator.model.ListElement;
import org.unigram.docvalidator.model.Paragraph;
import org.unigram.docvalidator.model.Sentence;
import org.unigram.docvalidator.model.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.sentence.CommaNumberValidator;
import org.unigram.docvalidator.validator.sentence.InvalidCharacterValidator;
import org.unigram.docvalidator.validator.sentence.InvalidExpressionValidator;
import org.unigram.docvalidator.validator.sentence.SentenceLengthValidator;
import org.unigram.docvalidator.validator.sentence.SentenceValidatorInitializer;
import org.unigram.docvalidator.validator.sentence.SpaceBeginningOfSentenceValidator;
import org.unigram.docvalidator.validator.sentence.SuggestExpressionValidator;
import org.unigram.docvalidator.validator.sentence.SymbolWithSpaceValidator;
import org.unigram.docvalidator.validator.sentence.WordNumberValidator;
import org.unigram.docvalidator.validator.sentence.KatakanaEndHyphenValidator;
import org.unigram.docvalidator.validator.sentence.KatakanaSpellCheckValidator;

/**
 * Validator for input sentences. Sentence iterator calls appended
 * SentenceValidators and validate the input using the validators.
 */
public class SentenceIterator implements Validator {
  /**
   * constructor.
   *
   * @throws DocumentValidatorException
   */
  public SentenceIterator() {
    this.sentenceValidators = new ArrayList<SentenceValidator>();
  }

  public List<ValidationError> validate(Document file,
                                        ResultDistributor distributor) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    for (SentenceValidator validator : this.sentenceValidators) {
      for (Iterator<Section> sectionIterator =
               file.getSections(); sectionIterator.hasNext(); ) {
        Section currentSection = sectionIterator.next();
        checkSection(distributor, errors, validator,
            currentSection, file.getFileName());
      }
    }
    return errors;
  }

  public boolean loadConfiguration(ValidatorConfiguration conf,
                                   CharacterTable charTable)
      throws DocumentValidatorException {
    for (ValidatorConfiguration currentConfiguration : conf.getChildren()) {
      String confName = currentConfiguration.getConfigurationName();
      SentenceValidatorInitializer validator;
      if (confName.equals("SentenceLength")) {
        validator = new SentenceLengthValidator();
      } else if (confName.equals("InvalidExpression")) {
        validator = new InvalidExpressionValidator();
      } else if (confName.equals("SpaceAfterPeriod")) {
        validator = new SpaceBeginningOfSentenceValidator();
      } else if (confName.equals("CommaNumber")) {
        validator = new CommaNumberValidator();
      } else if (confName.equals("WordNumber")) {
        validator = new WordNumberValidator();
      } else if (confName.equals("SuggestExpression")) {
        validator = new SuggestExpressionValidator();
      } else if (confName.equals("InvalidCharacter")) {
        validator = new InvalidCharacterValidator();
      } else if (confName.equals("SpaceWithSymbol")) {
        validator = new SymbolWithSpaceValidator();
      } else if (confName.equals("KatakanaEndHyphen")) {
        validator = new KatakanaEndHyphenValidator();
      } else if (confName.equals("KatakanaSpellCheckValidator")) {
        validator = new KatakanaSpellCheckValidator();
      } else {
        throw new DocumentValidatorException(
            "There is no validator like " + confName);
      }
      validator.initialize(currentConfiguration, charTable);
      // FIXME: Rewrite this temporary cast
      this.sentenceValidators.add( (SentenceValidator) validator);
    }
    return true;
  }

  private void checkSection(ResultDistributor distributor,
                            List<ValidationError> errors, SentenceValidator validator,
                            Section currentSection, String fileName) {
    checkParagraphs(distributor, errors, validator, currentSection, fileName);
    checkHeaders(distributor, errors, validator, currentSection, fileName);
    checkListElements(distributor, errors, validator, currentSection, fileName);
  }

  private void checkParagraphs(ResultDistributor distributor,
                               List<ValidationError> errors, SentenceValidator validator,
                               Section currentSection, String fileName) {
    for (Iterator<Paragraph> paraIterator =
             currentSection.getParagraphs(); paraIterator.hasNext(); ) {
      Paragraph currentParagraph = paraIterator.next();
      for (Iterator<Sentence> lineIterator =
               currentParagraph.getSentences(); lineIterator.hasNext(); ) {
        applyValidator(distributor, errors, validator, fileName,
            lineIterator.next());
      }
    }
  }

  private void checkHeaders(ResultDistributor distributor,
                            List<ValidationError> errors, SentenceValidator validator,
                            Section currentSection, String fileName) {
    for (Iterator<Sentence> iterator = currentSection.getHeaderContents();
         iterator.hasNext(); ) {
      applyValidator(distributor, errors, validator, fileName, iterator.next());
    }
  }

  private void checkListElements(ResultDistributor distributor,
                                 List<ValidationError> errors, SentenceValidator validator,
                                 Section currentSection, String fileName) {
    for (Iterator<ListBlock> listBlockIterator = currentSection.getListBlocks();
         listBlockIterator.hasNext(); ) {
      ListBlock listBlock = listBlockIterator.next();
      for (Iterator<ListElement> listElementIterator =
               listBlock.getListElements(); listElementIterator.hasNext(); ) {
        ListElement listElemnt = listElementIterator.next();
        for (Iterator<Sentence> sentenceIterator = listElemnt.getSentences();
             sentenceIterator.hasNext(); ) {
          applyValidator(distributor, errors, validator, fileName,
              sentenceIterator.next());
        }
      }
    }
  }

  private void applyValidator(ResultDistributor distributor,
                              List<ValidationError> errors, SentenceValidator validator,
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
                           List<ValidationError> errors, String fileName, ValidationError e) {
    if (e != null) {
      //NOTE: fileName is not specified in validators to reduce the task of them
      e.setFileName(fileName);
      distributor.flushResult(e);
      errors.add(e);
    }
  }

  private final List<SentenceValidator> sentenceValidators;

  private static final Logger LOG =
      LoggerFactory.getLogger(SentenceIterator.class);
}
