package models.beans.dataBinding;

import play.data.validation.Constraints.Required;

/**
 * A simple data object used to carry a CSRF token.
 */
public class CSRFData
{
	@Required(message="State parameter is not present in the request. Ensure that you've sent a state value to the authorization server.")
	private String state;

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}
}
