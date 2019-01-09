package pl.rwroblewski.collibrainterview.service;

import pl.rwroblewski.collibrainterview.exception.ValidationException;

public interface NodeValidator {

	void validateNotExists(String nodeName) throws ValidationException;

	void validateExists(String nodeName) throws ValidationException;

}
