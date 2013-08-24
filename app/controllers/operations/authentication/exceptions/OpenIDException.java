package controllers.operations.authentication.exceptions;

import controllers.operations.exceptions.ApplicationOperationException;

public class OpenIDException extends ApplicationOperationException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1129453715737235107L;
	
	public OpenIDException(String message)
	{
		super(message);
	}
	
	public OpenIDException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
