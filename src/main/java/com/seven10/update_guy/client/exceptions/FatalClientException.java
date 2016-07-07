package com.seven10.update_guy.client.exceptions;

import com.seven10.update_guy.common.exceptions.UpdateGuyException;

public class FatalClientException extends UpdateGuyException
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
