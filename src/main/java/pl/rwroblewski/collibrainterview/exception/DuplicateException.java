package pl.rwroblewski.collibrainterview.exception;

public class DuplicateException extends ValidationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateException() {
		super("NODE ALREADY EXISTS");
	}
}
