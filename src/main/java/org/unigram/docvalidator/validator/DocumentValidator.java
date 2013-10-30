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
import org.unigram.docvalidator.store.Document;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.Configuration;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DefaultResultDistributor;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidationError;

/**
 * Validate all input files using appended Validators.
 */
public class DocumentValidator {

  public DocumentValidator(DVResource resource,
      ResultDistributor resultDistributor)
      throws DocumentValidatorException {
    this(resource);
    this.distributor = resultDistributor;
  }

  public DocumentValidator(DVResource resource)
      throws DocumentValidatorException {
    super();
    this.distributor = new DefaultResultDistributor(System.out);
    this.validators = new Vector<Validator>();
    this.conf = resource.getConfiguration();
    this.charTable = resource.getCharacterTable();
    if (!loadValidators()) {
      throw new DocumentValidatorException(
          "Failed to create DocumentValidator");
    }
  }

  public boolean loadValidators() {
    this.validators.clear();
     for (Iterator<Configuration> confIterator =
         this.conf.getChildren(); confIterator.hasNext();) {
       Configuration currentConfiguration = confIterator.next();
       String confName = currentConfiguration.getConfigurationName();
       Validator validator = null;
       try {
         validator =
             ValidatorFactory.createValidator(confName,
                 currentConfiguration, this.charTable);
       } catch (DocumentValidatorException e) {
         LOG.error("Failed to create validator \" "
             + confName + "\" : " + e.getMessage());
         return false;
      }

      if (validator != null) {
         this.validators.add(validator);
       } else {
         LOG.error("Failed to create validator \" " + confName + "\"");
         return false;
       }
     }
    return true;
  }

  public List<ValidationError> check(Document document) {
    List<ValidationError> errors = new ArrayList<ValidationError>();
    for (Iterator<Validator> checkIterator =
        this.validators.iterator(); checkIterator.hasNext();) {
        Validator validator = checkIterator.next();
        Iterator<FileContent> fileIterator = document.getFiles();
        while (fileIterator.hasNext()) {
          List<ValidationError> currentErrors =
              validator.check(fileIterator.next(), distributor);
          errors.addAll(currentErrors);
        }
    }
    return errors;
  }

  private Vector<Validator> validators;

  private Configuration conf;

  private CharacterTable charTable;

  private ResultDistributor distributor;

  private static Logger LOG = LoggerFactory.getLogger(DocumentValidator.class);
}
