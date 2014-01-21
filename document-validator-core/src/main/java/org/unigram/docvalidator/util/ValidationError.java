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
   * @param sentence     sentence containing validation error
   */
  public ValidationError(String errorMessage,
                         Sentence sentence) {
    this(sentence.position, errorMessage);
    this.sentence = sentence;
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
   * @param sentence      sentence containing validation error
   * @param errorFileName file name in which the error occurs
   */
  public ValidationError(String errorMessage,
                         Sentence sentence, String errorFileName) {
    this(sentence.position, errorMessage);
    this.sentence = sentence;
    this.fileName = errorFileName;
  }

  /**
   * Get line number in which the error occurs.
   *
   * @return
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * Set the line number in which error occurs.
   *
   * @param errorLineNumber
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
   * @param errorMessage
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
   * Set file name
   *
   * @param errorFileName
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
   * Set sentence contains the error.
   *
   * @param sentence sentence containing validation error
   */
  public void setSentence(Sentence sentence) {
    this.sentence = sentence;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    if (this.fileName == null || this.fileName.equals("")) {
      str.append("ValidationError[").append(lineNumber).append(" (").append(message).append(")]");
    } else {
      str.append("ValidationError[").append(this.fileName).append(lineNumber).append(" (").append(message).append(")]");
    }
    if (this.sentence != null) {
      str.append(" at line: ").append(sentence.content);
    }
    return str.toString();
  }

  private int lineNumber;

  private String message;

  private String fileName;

  private Sentence sentence;
}
