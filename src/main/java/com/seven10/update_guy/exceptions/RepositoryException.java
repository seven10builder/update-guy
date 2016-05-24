package com.seven10.update_guy.exceptions;

public class RepositoryException extends Exception
{
	public RepositoryException(String msg, Object... args)
	{
		super(String.format(msg, args));
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3705235192507680435L;

}
