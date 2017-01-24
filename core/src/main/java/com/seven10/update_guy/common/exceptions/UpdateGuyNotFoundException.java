package com.seven10.update_guy.common.exceptions;


public class UpdateGuyNotFoundException extends UpdateGuyException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5114250936929720454L;

	public UpdateGuyNotFoundException(String msg, Object... args)
	{
		super(String.format(msg, args));
	}

}
