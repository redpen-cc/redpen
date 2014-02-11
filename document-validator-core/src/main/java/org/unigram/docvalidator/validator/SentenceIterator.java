/**
 * DocumentValidator
 * Copyright (c) 2013-, Takahiko Ito, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package org.unigram.docvalidator.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.ListBlock;
import org.unigram.docvalidator.store.ListElement;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.sentence.CommaNumberValidator;
import org.unigram.docvalidator.validator.sentence.InvalidCharacterValidator;
import org.unigram.docvalidator.validator.sentence.InvalidExpressionValidator;
import org.unigram.docvalidator.validator.sentence.SentenceLengthValidator;
import org.unigram.docvalidator.validator.sentence.SpaceBeginningOfSentenceValidator;
import org.unigram.docvalidator.validator.sentence.SuggestExpressionValidator;
import org.unigram.docvalidator.validator.sentence.SymbolWithSpaceValidator;
import org.unigram.docvalidator.validator.sentence.WordNumberValidator;
import org.unigram.docvalidator.validator.sentence.lang.ja.KatakanaEndHyphenValidator;
import org.unigram.docvalidator.validator.sentence.lang.ja.KatakanaSpellCheckValidator;

/**
 * Validator for input sentences. Sentence iterator calls appended
 * SentenceValidators and check the input using the validators.
 */
public class SentenceIterator implements Validator {
  /**
   * constructor.
   * @throws DocumentValidatorException
   */
  public SentenceIterator() {
    this.sentenceValidators = new Vector<SentenceValidator>();
  }

  public List<ValidationError> check(FileContent file,
      ResultDistributor distributor) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    for (SentenceValidator validator : this.sentenceValidators) {
      for (Iterator<Section> sectionIterator =
               file.getSections(); sectionIterator.hasNext();) {
        Section currentSection = sectionIterator.next();
        checkSection(distributor, errors, validator,
            currentSection, file.getFileName());
      }
    }
    return errors;
  }

  public boolean loadConfiguration(ValidatorConfiguration conf,
      CharacterTable charTable) throws DocumentValidatorException {
    for (Iterator<ValidatorConfiguration> confIterator =  conf.getChildren();
        confIterator.hasNext();) {
      ValidatorConfiguration currentConfiguration = confIterator.next();
      String confName = currentConfiguration.getConfigurationName();
      SentenceValidator validator;
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
      this.sentenceValidators.add(validator);
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
        currentSection.getParagraphs(); paraIterator.hasNext();) {
      Paragraph currentParagraph = paraIterator.next();
      for (Iterator<Sentence> lineIterator =
          currentParagraph.getSentences(); lineIterator.hasNext();) {
        applyValidator(distributor, errors, validator, fileName,
            lineIterator.next());
      }
    }
  }

  private void checkHeaders(ResultDistributor distributor,
      List<ValidationError> errors, SentenceValidator validator,
      Section currentSection, String fileName) {
    for (Iterator<Sentence> iterator = currentSection.getHeaderContents();
        iterator.hasNext();) {
      applyValidator(distributor, errors, validator, fileName, iterator.next());
    }
  }

  private void checkListElements(ResultDistributor distributor,
      List<ValidationError> errors, SentenceValidator validator,
      Section currentSection, String fileName) {
    for (Iterator<ListBlock> listBlockIterator = currentSection.getListBlocks();
        listBlockIterator.hasNext();) {
      ListBlock listBlock = listBlockIterator.next();
      for (Iterator<ListElement> listElementIterator =
          listBlock.getListElements(); listElementIterator.hasNext();) {
        ListElement listElemnt = listElementIterator.next();
        for (Iterator<Sentence> sentenceIterator = listElemnt.getSentences();
            sentenceIterator.hasNext();) {
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
          validator.check(sentence);
    } catch (Throwable e) {
      //TODO add validator type info
      LOG.error("Error in checking sentence: \"" + sentence.content +  "\"");
      return;
    }
    for (ValidationError validationError : validationErrors) {
      appendError(distributor, errors, fileName, validationError);
    }
  }

  protected void addSentenceValidator(
      SentenceValidator sentenceValidator) {
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

  private final Vector<SentenceValidator> sentenceValidators;

  private static final Logger LOG =
      LoggerFactory.getLogger(SentenceIterator.class);
}
