package security.exceptions;

public class InvalidCSRFTokenException extends Exception
{
	private static final long	serialVersionUID	= -2371493385240750243L;

	public InvalidCSRFTokenException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidCSRFTokenException(String message)
	{
		super(message);
	}

	public InvalidCSRFTokenException(Throwable cause)
	{
		super(cause);
	}
}
