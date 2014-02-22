package security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

import models.beans.dataBinding.CSRFData;
import security.annotations.CheckCSRFToken;
import security.exceptions.InvalidCSRFTokenException;

public final class CSRF
{
	private static final String STATE = "state";
	private static final SecureRandom RANDOM = new SecureRandom();
	/**
	 * Create a unique token to prevent request forgery attacks.
	 * @param session where to store the CSRF token for later validation.
	 * @return the generated CSRF token.
	 */
	public static String addAndReturnToken(Map<String, String> session)
	{
		String state = new BigInteger(130, RANDOM).toString(32);
		session.put(STATE, state);
		return state;
	}

	/**
	 * 
	 * @param data object containing the request state parameter.
	 * @param session the session where to search for a state token.
	 * @throws InvalidCSRFTokenException if {@code session} has no state token or CSRF tokens don't match.
	 * @see CheckCSRFToken
	 */
	public static void checkAndRemoveToken(CSRFData data, Map<String, String> session) throws InvalidCSRFTokenException
	{
		if(session.containsKey(STATE) && session.get(STATE).equals(data.getState())) 
		{
			session.remove(STATE);
			return;
		}
		
		throw new InvalidCSRFTokenException("The session doesn't contain a state token or the session state doesn't match the request state parameter.");
	}
}
