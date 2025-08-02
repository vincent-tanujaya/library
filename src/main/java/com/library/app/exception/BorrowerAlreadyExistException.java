package com.library.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BorrowerAlreadyExistException extends RuntimeException{

  public BorrowerAlreadyExistException() {
    super("Borrower already exist");
  }
}
