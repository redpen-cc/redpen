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
package org.bigram.docvalidator;

import org.bigram.docvalidator.model.Sentence;

/**
 * Error to report invalid point from Validators.
 */
public class ValidationError {

  /**
   * Constructor.
   *
   * @param validatorClass validator class
   * @param errorMessage   error message
   */
  public ValidationError(Class validatorClass, String errorMessage) {
    super();
    this.lineNumber = -1;
    this.message = errorMessage;
    this.fileName = "";
    this.sentence = null;
    this.validatorName = validatorClass.getSimpleName();
  }

  /**
   * Constructor.
   *
   * @param validatorClass  validator class
   * @param errorMessage    error message
   * @param errorLineNumber error position (line number)
   */
  public ValidationError(Class validatorClass,
                         String errorMessage, int errorLineNumber) {
    this(validatorClass, errorMessage);
    this.lineNumber = errorLineNumber;
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
    this.sentence = sentenceWithError;
  }

  /**
   * Constructor.
   *
   * @param validatorClass  validator class
   * @param errorMessage    error message
   * @param errorLineNumber error position (line number)
   * @param errorFileName   file name in which the error occurs
   */
  public ValidationError(Class validatorClass,
                         String errorMessage, int errorLineNumber,
                         String errorFileName) {
    this(validatorClass, errorMessage, errorLineNumber);
    this.fileName = errorFileName;
  }

  /**
   * Constructor.
   *
   * @param validatorClass    validator class
   * @param errorMessage      error message
   * @param sentenceWithError sentence containing validation error
   * @param errorFileName     file name in which the error occurs
   */
  public ValidationError(Class validatorClass, String errorMessage,
                         Sentence sentenceWithError, String errorFileName) {
    this(validatorClass, errorMessage, sentenceWithError.position);
    this.sentence = sentenceWithError;
    this.fileName = errorFileName;
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
   * Set the line number in which error occurs.
   *
   * @param errorLineNumber line number in which error occurs
   */
  public void setLineNumber(int errorLineNumber) {
    this.lineNumber = errorLineNumber;
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
   * Set error message.
   *
   * @param errorMessage error message
   */
  public void setMessage(String errorMessage) {
    this.message = errorMessage;
  }

  /**
   * Get file name.
   *
   * @return file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Set file name.
   *
   * @param errorFileName file name in which the error occurs
   */
  public void setFileName(String errorFileName) {
    this.fileName = errorFileName;
  }

  /**
   * Get sentence containing the error.
   *
   * @return sentence
   */
  public Sentence getSentence() {
    return sentence;
  }

  /**
   * Set sentenceWithError contains the error.
   *
   * @param sentenceWithError sentenceWithError containing validation error
   */
  public void setSentence(Sentence sentenceWithError) {
    this.sentence = sentenceWithError;
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

  private int lineNumber;

  private String message;

  private String fileName;

  private Sentence sentence;

  private final String validatorName;
}
