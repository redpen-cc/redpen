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


import java.util.List;

import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.ResultDistributor;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidationError;

/**
 * Validate input document.
 */
public interface Validator {
  /**
   * check the input document and returns the invalid points.
   * @param document input
   * @param distributor flush output
   * @return List of ValidationError
   */
  List<ValidationError> check(FileContent document,
      ResultDistributor distributor);

  /**
   * load configuration files.
   * @param conf validator configuration
   * @param charTable character configuration
   * @return true succeeded, otherwise false
   * @throws DocumentValidatorException
   */
  boolean loadConfiguration(ValidatorConfiguration conf,
      CharacterTable charTable) throws DocumentValidatorException;
}
