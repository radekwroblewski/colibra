package pl.rwroblewski.collibrainterview.service;

import org.springframework.stereotype.Service;

import pl.rwroblewski.collibrainterview.exception.ValidationException;

@Service
public class ValidationErrorHandlerImpl implements ValidationErrorHandler {

    @Override
    public String getErrorMessage(ValidationException e) {
        return "ERROR: " + e.getMessage();
    }

}
