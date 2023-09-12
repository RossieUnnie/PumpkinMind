package com.gfa.otocyonknowledgebase.globalExceptionHandler.exception;

public class SeedNotFoundException extends RuntimeException {
  public SeedNotFoundException(Long id) {
    super(String.valueOf(id));
  }
}
