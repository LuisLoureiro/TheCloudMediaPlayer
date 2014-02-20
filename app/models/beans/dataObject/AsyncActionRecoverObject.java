package models.beans.dataObject;


public class AsyncActionRecoverObject<T>
{
	private T data;
	private boolean error;
	private AsyncActionRecoverErrorObject actionError;
	
	public T getData()
	{
		return data;
	}
	public void setData(T data)
	{
		this.data = data;
	}
	public boolean isError()
	{
		return error;
	}
	public void setError(boolean error)
	{
		this.error = error;
	}
	public AsyncActionRecoverErrorObject getActionError()
	{
		return actionError;
	}
	public void setActionError(AsyncActionRecoverErrorObject actionError)
	{
		this.actionError = actionError;
	}
}
