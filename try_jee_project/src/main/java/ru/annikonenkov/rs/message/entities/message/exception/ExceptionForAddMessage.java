package ru.annikonenkov.rs.message.entities.message.exception;

public class ExceptionForAddMessage extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExceptionForAddMessage(String message) {
		super(message);
	}

	public ExceptionForAddMessage(String message, Throwable thorwable) {
		super(message, thorwable);
	}
}
