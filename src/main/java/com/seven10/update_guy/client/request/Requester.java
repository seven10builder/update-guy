package com.seven10.update_guy.client.request;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;

import com.seven10.update_guy.exceptions.FatalClientException;

public class Requester
{
	static final Logger logger = LogManager.getFormatterLogger(Requester.class.getName());
	static final String defaultProtocol = "http";
	
	private WebTarget webTarget;
	
	public Requester(String url, String methodName)
	{
		if(StringUtils.isBlank(url))
		{
			throw new IllegalArgumentException("url must not be null or empty");
		}
		if(StringUtils.isBlank(methodName))
		{
			throw new IllegalArgumentException("methodName must not be null or empty");
		}
		Client client = ClientBuilder.newClient( new ClientConfig() );
		webTarget = client.target(url).path(methodName);
		logger.debug(".ctor(): Requester url = '%s', methodName = %s", url, methodName);
	}

	public void addQueryParam(String name, String value)
	{
		if(StringUtils.isBlank(name))
		{
			throw new IllegalArgumentException("name must not be null or empty");
		}
		if(StringUtils.isBlank(value))
		{
			throw new IllegalArgumentException("value must not be null or empty");
		}
		webTarget = webTarget.queryParam(name, value);
		logger.debug(".addQueryParam(): Adding query parameter (%s, %s)", name, value);
	}

	public <T> T get(Class<T> entityType) throws FatalClientException
	{
		logger.info(".get<%s>(): invoking request to webTarget '%s'", entityType.getName(), webTarget.toString());
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_XML);
		Response response = invocationBuilder.get();
		logger.info(".get<%s>(): request invoked", entityType.getName());
		Status statusCode = Status.fromStatusCode(response.getStatus());
		T result = RequesterUtils.evaluateResponse(response, statusCode, entityType);
		return result;
	}
	
	public void getFile(Path targetPath) throws FatalClientException
	{
		logger.info(".getFile(): invoking request to webTarget '%s'", webTarget.toString());
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_XML);
		Response response = invocationBuilder.get();
		logger.info(".getFile(): request invoked");
		Status statusCode = Status.fromStatusCode(response.getStatus());
		RequesterUtils.evaluateResponse(response, statusCode, String.class);
		InputStream is = response.readEntity(InputStream.class);
	    try
		{
	    	logger.info(".getFile(): storing file at '%s'", targetPath);
			Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
			logger.info(".getFile(): file '%s' copied", targetPath);
		}
		catch (IOException e)
		{
			throw new FatalClientException("The update-guy responded with an unexpected response: %s", statusCode.toString());
		}
	    finally
	    {
	    	IOUtils.closeQuietly(is);
	    }
	}
}
