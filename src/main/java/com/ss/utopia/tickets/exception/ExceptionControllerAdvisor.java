package com.ss.utopia.tickets.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvisor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvisor.class);

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoSuchElementException.class)
  public Map<String, Object> noSuchElementException(NoSuchElementException ex) {
    LOGGER.error(ex.getMessage());
    var response = new HashMap<String, Object>();

    response.put("error", ex.getMessage());
    response.put("status", 404);

    return response;
  }
}
