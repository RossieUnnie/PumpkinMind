package com.gfa.otocyonknowledgebase.globalExceptionHandler.exception;

public class SeedDoesntBelongToThisUserException extends RuntimeException {
  public SeedDoesntBelongToThisUserException(Long id) {
    super(String.valueOf(id));
  }
}
