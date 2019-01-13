package pl.rwroblewski.collibrainterview.service;

import pl.rwroblewski.collibrainterview.exception.ValidationException;

public interface ValidationErrorHandler {

    String getErrorMessage(ValidationException e);

}
