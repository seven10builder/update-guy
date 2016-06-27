package com.seven10.update_guy.exceptions;


public class ClientParameterException extends Exception
{

	/**
	 * what is this even for? Eclipse complains. I'm not going to be streaming this exception though... 
	 */
	private static final long serialVersionUID = 1L;
	
	public ClientParameterException(String msg, Object... args)
	{
		super(String.format(msg, args));
	}
	
}
