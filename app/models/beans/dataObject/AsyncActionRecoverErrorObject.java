package models.beans.dataObject;

import play.mvc.Content;

public class AsyncActionRecoverErrorObject
{
	private int responseStatus;
	private String message;
	
	public AsyncActionRecoverErrorObject() {}
	
	public AsyncActionRecoverErrorObject(Content content, int status, String message)
	{
		this.responseStatus = status;
		this.message = message;
	}
	
	public int getResponseStatus()
	{
		return responseStatus;
	}
	public void setResponseStatus(int responseStatus)
	{
		this.responseStatus = responseStatus;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
}
