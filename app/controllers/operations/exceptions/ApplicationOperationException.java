package controllers.operations.exceptions;

public class ApplicationOperationException extends Exception
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 323917311994537359L;

	public ApplicationOperationException(String message)
	{
		super(message);
	}
	
	public ApplicationOperationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
