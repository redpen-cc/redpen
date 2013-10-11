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
