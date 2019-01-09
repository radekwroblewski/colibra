package pl.rwroblewski.collibrainterview.exception;

public class NotFoundException extends ValidationException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7961516731418230446L;

	public NotFoundException() {
		super("NODE NOT FOUND");
	}

}
