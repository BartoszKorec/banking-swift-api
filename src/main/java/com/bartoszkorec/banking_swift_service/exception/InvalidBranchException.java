package com.bartoszkorec.banking_swift_service.exception;

public class InvalidBranchException extends RuntimeException {
  public InvalidBranchException(String message) {
    super(message);
  }
}
