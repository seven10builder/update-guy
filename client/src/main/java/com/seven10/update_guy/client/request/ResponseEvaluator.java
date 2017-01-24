package com.seven10.update_guy.client.request;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.exceptions.FatalClientException;

public class ResponseEvaluator<T>
{
	private static final Logger logger = LogManager.getFormatterLogger(ResponseEvaluator.class.getName());
	
	private final Class<T> respItemType;
	
	public ResponseEvaluator(Class<T> respItemType)
	{
		this.respItemType = respItemType;
	}
	public T evaluateResponse(Response response, Status statusCode)	throws FatalClientException
	{
		if(response == null)
		{
			throw new IllegalArgumentException("response must not be null");
		}
		if(statusCode == null)
		{
			throw new IllegalArgumentException("statusCode must not be null");
		}
		T rval;
		logger.info(".evaluateResponse(): processing response. statusCode = %s", statusCode.name());
		switch(statusCode)
		{
		case OK:
			rval = response.readEntity(respItemType);
			logger.info(".evaluateResponse(): response entity (type '%s') = '%s'", respItemType.getName(), rval.toString());
			break;
		case INTERNAL_SERVER_ERROR:
			throw new FatalClientException("There was an internal problem with the update-guy. Cannot connect.");
		case NOT_FOUND:
			throw new FatalClientException("The update-guy cannot find the requested resource");
		default:
			throw new FatalClientException("The update-guy responded with an unexpected response: %s", statusCode.toString());
		}
		return rval;
	}
	public Class<T> getEntityType()
	{
		return respItemType;
	}
}
