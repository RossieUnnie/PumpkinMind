package com.gfa.otocyonknowledgebase.globalExceptionHandler.exception;

public class SeedFileNotFoundException extends RuntimeException {
  public SeedFileNotFoundException(Long id) {
    super(String.valueOf(id));
  }
}
