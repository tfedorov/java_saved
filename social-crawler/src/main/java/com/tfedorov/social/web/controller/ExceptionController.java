package com.tfedorov.social.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.tfedorov.social.clustering.exception.ClusterValidationException;

public abstract class ExceptionController {

  private static final String PROD_MOD = "production";

  @Value("${aggregator.app.mode:production}")
  private String applicationMode;

  private static final String ERROR = "{\"status\": \"error\", \"message\": \"%1$s\"}";
  private final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

  @ExceptionHandler(JSONException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleJSONException(JSONException ex, HttpServletRequest request) {
    logger.error("JSONException", ex);
    return formatMessage(ex);
  }

  @ExceptionHandler(JsonMappingException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleJsonMappingException(JsonMappingException ex, HttpServletRequest request) {
    logger.error("JsonMappingException", ex);
    return formatMessage(ex);
  }

  @ExceptionHandler(JsonGenerationException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleJsonGenerationException(JsonGenerationException ex, HttpServletRequest request) {
    logger.error("JsonGenerationException", ex);
    return formatMessage(ex);
  }

  @ExceptionHandler(IOException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleIOException(IOException ex, HttpServletRequest request) {
    logger.error("IOException", ex);
    return formatMessage(ex);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleAllException(Exception ex, HttpServletRequest request) {
    logger.error("Exception", ex);
    return formatMessage(ex);
  }

  @ExceptionHandler(DataAccessException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
    logger.error("DataAccessException", ex);
    return formatMessage(ex);
  }

  @ExceptionHandler(NumberFormatException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleNumberFormatException(NumberFormatException ex, HttpServletRequest request) {
    logger.error("NumberFormatException", ex);
    return formatMessage(ex);
  }
  
  @ExceptionHandler(ClusterValidationException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String handleClusterValidationException(ClusterValidationException ex, HttpServletRequest request) {
    logger.error("ClusterValidationException", ex);
    return String.format(ERROR, ex.getMessage());
  }

  private String formatMessage(final Exception error) {
    if (PROD_MOD.equalsIgnoreCase(applicationMode)) {
      return String.format(ERROR, "Internal server error!");
    } else {
      return String.format(ERROR, error.getMessage());
    }
  }
}
