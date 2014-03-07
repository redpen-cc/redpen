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
package org.unigram.docvalidator.util;

import org.unigram.docvalidator.store.Sentence;

/**
 * Error to report invalid point from Validators.
 */
public class ValidationError {

  /**
   * Constructor.
   *
   * @param errorMessage error message
   */
  public ValidationError(String errorMessage) {
    super();
    this.lineNumber = -1;
    this.message = errorMessage;
    this.fileName = "";
    this.sentence = null;
  }

  /**
   * Constructor.
   *
   * @param errorLineNumber error position (line number)
   * @param errorMessage    error message
   */
  public ValidationError(int errorLineNumber, String errorMessage) {
    this(errorMessage);
    this.lineNumber = errorLineNumber;
    this.fileName = "";
  }

  /**
   * Constructor.
   *
   * @param errorMessage error message
   * @param sentenceWithError sentence containing validation error
   */
  public ValidationError(String errorMessage,
                         Sentence sentenceWithError) {
    this(sentenceWithError.position, errorMessage);
    this.sentence = sentenceWithError;
  }

  /**
   * Constructor.
   *
   * @param errorLineNumber error position (line number)
   * @param errorMessage    error message
   * @param errorFileName   file name in which the error occurs
   */
  public ValidationError(int errorLineNumber, String errorMessage,
                         String errorFileName) {
    this(errorLineNumber, errorMessage);
    this.fileName = errorFileName;
  }

  /**
   * Constructor.
   *
   * @param errorMessage  error message
   * @param sentenceWithError sentence containing validation error
   * @param errorFileName file name in which the error occurs
   */
  public ValidationError(String errorMessage,
                         Sentence sentenceWithError, String errorFileName) {
    this(sentenceWithError.position, errorMessage);
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

  @Override
  public String toString() {
    return "ValidationError{" +
        "lineNumber=" + lineNumber +
        ", message='" + message + '\'' +
        ", fileName='" + fileName + '\'' +
        ", sentence=" + sentence +
        '}';
  }

  private int lineNumber;

  private String message;

  private String fileName;

  private Sentence sentence;
}
