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
package cc.redpen;

import cc.redpen.model.Sentence;

import java.util.Optional;

/**
 * Error to report invalid point from Validators.
 */
public class ValidationError {

  /**
   * Constructor.
   *
   * @param validatorClass  validator class
   * @param errorMessage    error message
   * @param errorLineNumber error position (line number)
   */
  public ValidationError(Class validatorClass,
                         String errorMessage, int errorLineNumber) {
    this.lineNumber = errorLineNumber;
    this.message = errorMessage;
    this.validatorName = validatorClass.getSimpleName();
  }

  /**
   * Constructor.
   *
   * @param validatorClass    validator class
   * @param errorMessage      error message
   * @param sentenceWithError sentence containing validation error
   */
  public ValidationError(Class validatorClass,
                         String errorMessage,
                         Sentence sentenceWithError) {
    this(validatorClass, errorMessage, sentenceWithError.position);
    this.sentence = Optional.of(sentenceWithError);
  }

  /**
   * Get line number in which the error occurs.
   *
   * @return the number of line
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * Get error message.
   *
   * @return error message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get file name.
   *
   * @return file name
   */
  public Optional<String> getFileName() {
    return fileName;
  }

  /**
   * Set file name.
   *
   * @param errorFileName file name in which the error occurs
   */
  public void setFileName(String errorFileName) {
    this.fileName = Optional.of(errorFileName);
  }

  /**
   * Get sentence containing the error.
   *
   * @return sentence
   */
  public Optional<Sentence> getSentence() {
    return sentence;
  }

  /**
   * Set sentenceWithError contains the error.
   *
   * @param sentenceWithError sentenceWithError containing validation error
   */
  public void setSentence(Sentence sentenceWithError) {
    this.sentence = Optional.of(sentenceWithError);
  }

  /**
   * Get validator name.
   *
   * @return validator name
   */
  public String getValidatorName() {
    if (validatorName.endsWith("Validator")) {
      return validatorName
          .substring(0, validatorName.length() - "Validator".length());
    } else {
      return validatorName;
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ValidationError{");
    sb.append("lineNumber=").append(lineNumber);
    sb.append(", message='").append(message).append('\'');
    sb.append(", fileName='").append(fileName).append('\'');
    sb.append(", sentence=").append(sentence);
    sb.append(", validatorName='").append(validatorName).append('\'');
    sb.append('}');
    return sb.toString();
  }

  private final int lineNumber;

  private final String message;

  private Optional<String> fileName = Optional.empty();

  private Optional<Sentence> sentence = Optional.empty();

  private final String validatorName;
}
