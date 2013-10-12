package org.unigram.docvalidator.validator;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.validator.sentence.CommaNumberValidator;
import org.unigram.docvalidator.validator.sentence.InvalidExpressionValidator;
import org.unigram.docvalidator.validator.sentence.InvalidCharacterValidator;
import org.unigram.docvalidator.validator.sentence.MaxWordNumberValidator;
import org.unigram.docvalidator.validator.sentence.SentenceLengthValidator;
import org.unigram.docvalidator.validator.sentence.SpaceBegginingOfSentenceValidator;
import org.unigram.docvalidator.validator.sentence.SymbolWithSpaceValidator;
import org.unigram.docvalidator.validator.sentence.SuggestExpressionValidator;

/**
 * Validator for input sentences. Sentence iterator calls appended
 * SentenceValidators and check the input using the validators.
 */
public class SentenceIterator implements Validator {
  /**
   * constructor.
   * @throws DocumentValidatorException
   */
  public SentenceIterator() throws DocumentValidatorException {
    this.lineValidators = new Vector<SentenceValidator>();
  }

  public List<ValidationError> check(FileContent file,
      ResultDistributor distributor) {
    Vector<ValidationError> errors = new Vector<ValidationError>();
    for (Iterator<SentenceValidator> iterator =
        this.lineValidators.iterator(); iterator.hasNext();) {
      SentenceValidator validator = iterator.next();
      for (Iterator<Section> sectionIterator =
            file.getChilds(); sectionIterator.hasNext();) {
        Section currentSection = sectionIterator.next();
        checkSection(distributor, errors, validator,
            currentSection, file.getFileName());
      }
    }
    return errors;
  }

  public boolean loadConfiguration(Configuration conf,
      CharacterTable charTable) throws DocumentValidatorException {
    for (Iterator<Configuration> confIterator =  conf.getChildren();
        confIterator.hasNext();) {
      Configuration currentConfiguration = confIterator.next();
      String confName = currentConfiguration.getConfigurationName();
      SentenceValidator validator = null;
      if (confName.equals("SentenceLength")) {
        validator = (SentenceValidator) new SentenceLengthValidator();
      } else if (confName.equals("InvalidExpression")) {
        validator = (SentenceValidator) new InvalidExpressionValidator();
      } else if (confName.equals("SpaceAfterPeriod")) {
        validator = (SentenceValidator) new SpaceBegginingOfSentenceValidator();
      } else if (confName.equals("MaxCommaNumber")) {
        validator = (SentenceValidator) new CommaNumberValidator();
      } else if (confName.equals("MaxWordNumber")) {
        validator = (SentenceValidator) new MaxWordNumberValidator();
      } else if (confName.equals("SuggestExpression")) {
        validator = (SentenceValidator) new SuggestExpressionValidator();
      } else if (confName.equals("InvalidCharacter")) {
          validator = (SentenceValidator) new InvalidCharacterValidator();
      } else if (confName.equals("SpaceWithSymbol")) {
        validator = (SentenceValidator) new SymbolWithSpaceValidator();
      } else {
        throw new DocumentValidatorException(
            "There is no validator like " + confName);
      }
      validator.initialize(currentConfiguration, charTable);
      this.lineValidators.add(validator);
    }
    return true;
  }

  // @TODO reduce the number of parameters (need refactoring)
  private void checkSection(ResultDistributor distributor,
      Vector<ValidationError> errors, SentenceValidator validator,
      Section currentSection, String fileName) {
    for (Iterator<Paragraph> paraIterator =
        currentSection.getParagraph(); paraIterator.hasNext();) {
      Paragraph currentParagraph = paraIterator.next();
      for (Iterator<Sentence> lineIterator =
          currentParagraph.getChilds(); lineIterator.hasNext();) {
        List<ValidationError> validationErrors =
            validator.process(lineIterator.next());
        for (Iterator<ValidationError> errorIterator =
            validationErrors.iterator();
            errorIterator.hasNext();) {
          appendError(distributor, errors, fileName, errorIterator.next());
        }
      }
    }
  }

  private void appendError(ResultDistributor distributor,
      Vector<ValidationError> errors, String fileName, ValidationError e) {
    if (e != null) {
      //NOTE: fileName is not specified in validators to reduce the task of them
      e.setFileName(fileName);
      distributor.flushResult(e);
      errors.add(e);
    }
  }

 private Vector<SentenceValidator> lineValidators;

}
