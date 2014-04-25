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

import org.unigram.docvalidator.config.CharacterTable;
import org.unigram.docvalidator.DocumentValidatorException;
import org.unigram.docvalidator.config.ValidatorConfiguration;

/**
 * Loader for Validator Configuration.
 */
public interface ConfigurationLoader {

  /**
   * load configuration files.
   *
   * @param conf      validator configuration
   * @param charTable character configuration
   * @return true succeeded, otherwise false
   * @throws org.unigram.docvalidator.DocumentValidatorException
   */
  boolean loadConfiguration(ValidatorConfiguration conf,
                            CharacterTable charTable)
      throws DocumentValidatorException;

}
