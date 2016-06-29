package com.seven10.update_guy.exceptions;

public class FatalClientException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3501811434054193762L;

	public FatalClientException(String msg, Object... args)
	{
		super(String.format(msg, args));
	}
}
