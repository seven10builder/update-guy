package com.seven10.update_guy.common.exceptions;

public class UpdateGuyException extends Exception
{
	public UpdateGuyException(String msg, Object... args)
	{
		super(String.format(msg, args));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052009607494265532L;

}
