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

/**
 * Error to report invalid point from Validators.
 */
public class ValidationError {

  public ValidationError(String errorMessage) {
    super();
    this.lineNumber = -1;
    this.message = errorMessage;
    this.fileName = "";
  }

  public ValidationError(int errorLineNumber, String errorMessage) {
    super();
    this.lineNumber = errorLineNumber;
    this.message = errorMessage;
    this.fileName = "";
  }

  public ValidationError(int errorLineNumber, String errorMessage,
      String erorFileName) {
    super();
    this.lineNumber = errorLineNumber;
    this.message = errorMessage;
    this.fileName = erorFileName;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int errorLineNumber) {
    this.lineNumber = errorLineNumber;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String errorMessage) {
    this.message = errorMessage;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String erroFileName) {
    this.fileName = erroFileName;
  }

  @Override
  public String toString() {
    if (this.fileName == null || this.fileName.equals("")) {
      return "CheckError[" + lineNumber + "] = " + message;
    } else {
      return "CheckError[" + this.fileName + ": "
          + lineNumber + "] = " + message;
    }
  }

  private int lineNumber;

  private String message;

  private String fileName;
}
