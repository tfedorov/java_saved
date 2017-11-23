package com.tfedorov.social.clustering.exception;

public class ClusterValidationException extends RuntimeException {

  private static final long serialVersionUID = 3346299490373716016L;

  public ClusterValidationException() {}

  public ClusterValidationException(String message) {
    super(message);
  }

  public ClusterValidationException(Throwable cause) {
    super(cause);
  }

  public ClusterValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
