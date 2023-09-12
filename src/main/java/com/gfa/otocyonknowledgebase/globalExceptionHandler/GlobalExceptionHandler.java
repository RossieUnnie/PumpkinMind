package com.gfa.otocyonknowledgebase.globalExceptionHandler;

import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.BadRequestException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedDoesntBelongToThisUserException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedFileNotFoundException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedNotFoundByTagsException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.SeedNotFoundException;
import com.gfa.otocyonknowledgebase.globalExceptionHandler.exception.TeapotException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(TeapotException.class)
  public RedirectView teapotException() {
    String errorImageUrl = "https://http.cat/418";
    RedirectView redirectView = new RedirectView();
    redirectView.setUrl(errorImageUrl);
    return redirectView;
  }

  @ExceptionHandler(SeedNotFoundException.class)
  public ResponseEntity<?> seedNotFoundException(SeedNotFoundException ex) {
    String errorMessage = "Seed not found for ID: " + ex.getMessage();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<?> badRequestException() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @ExceptionHandler(SeedNotFoundByTagsException.class)
  public ResponseEntity<?> seedNotFoundByTagsException() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No seed found for the given tags.");
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
    String errorMessage = "File size exceeded the maximum allowed limit (25MB) .";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }

  @ExceptionHandler(SeedDoesntBelongToThisUserException.class)
  public ResponseEntity<?> seedDoesntBelongToThisUserException(
      SeedDoesntBelongToThisUserException ex) {
    String errorMessage = "This seed does not belong to this user: " + ex.getMessage();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
  }

  @ExceptionHandler(SeedFileNotFoundException.class)
  public ResponseEntity<?> seedFileNotFoundException(SeedFileNotFoundException ex) {
    String errorMessage = "File not found for this ID: " + ex.getMessage();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
}
