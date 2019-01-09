package pl.rwroblewski.collibrainterview.exception;

public abstract class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5684120303550436909L;
	
	public ValidationException(String message) {
		super(message);
	}

}
