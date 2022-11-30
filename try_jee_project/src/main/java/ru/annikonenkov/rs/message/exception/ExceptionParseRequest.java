package ru.annikonenkov.rs.message.exception;

public class ExceptionParseRequest extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExceptionParseRequest(String message) {
		super(message);
	}

	public ExceptionParseRequest(String message, Throwable thorwable) {
		super(message, thorwable);
	}

}
