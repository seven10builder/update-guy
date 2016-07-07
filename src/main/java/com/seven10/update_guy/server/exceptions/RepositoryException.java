package com.seven10.update_guy.server.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class RepositoryException extends Exception implements ExceptionMapper<RepositoryException>
{
	private Status statusCode;
	
	public RepositoryException(Status code, String msg, Object... args)
	{
		super(String.format(msg, args));
		statusCode = code;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3705235192507680435L;
	public Status getStatusCode()
	{
		return statusCode;
	}
	
	@Override
	public Response toResponse(RepositoryException ex)
	{
	    return Response.status(ex.getStatusCode()).entity(ex.getMessage()).type("text/plain").build();
	}

}
