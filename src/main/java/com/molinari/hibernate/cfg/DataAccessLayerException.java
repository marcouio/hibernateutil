package com.molinari.hibernate.cfg;

/**
 * Represents Exceptions thrown by the Data Access Layer.
 */
public class DataAccessLayerException extends RuntimeException {

	private static final long	serialVersionUID	= 1L;

	public DataAccessLayerException() {
	}

	public DataAccessLayerException(final String message) {
		super(message);
	}

	public DataAccessLayerException(final Throwable cause) {
		super(cause);
	}

	public DataAccessLayerException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
