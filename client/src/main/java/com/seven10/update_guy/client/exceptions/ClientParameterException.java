package com.seven10.update_guy.client.exceptions;


public class ClientParameterException extends FatalClientException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7994738766964687035L;

	/**
	 * what is this even for? Eclipse complains. I'm not going to be streaming this exception though... 
	 */
	
	public ClientParameterException(String msg, Object... args)
	{
		super(String.format(msg, args));
	}
	
}
