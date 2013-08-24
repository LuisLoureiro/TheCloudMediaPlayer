package controllers.operations.persistence.exceptions;

import controllers.operations.exceptions.ApplicationOperationException;

public class UserPlaylistException extends ApplicationOperationException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3373783741756594573L;

	public UserPlaylistException(String message)
	{
		super(message);
	}
	
	public UserPlaylistException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
