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

import com.seven10.update_guy.client.FunctionalInterfaces;
import com.seven10.update_guy.client.FunctionalInterfaces.ResponseToFileMgr;
import com.seven10.update_guy.client.FunctionalInterfaces.WebReqFactory;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.common.manifest.ClientRoleInfo;

public class Requester
{
	static final Logger logger = LogManager.getFormatterLogger(Requester.class.getName());
	static final String defaultProtocol = "http";
	
	private WebTarget webTarget;
	
	/**
	 * @param targetPath
	 * @param response
	 * @param statusCode
	 * @throws FatalClientException
	 */
	public static void copyFileFromResponse(Path targetPath, Response response, Status statusCode) throws FatalClientException
	{
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
	
	public <T> T get(FunctionalInterfaces.WebReqFactory webReqFactory, ResponseEvaluator<T> evaluator) throws FatalClientException
	{
		if(webReqFactory == null)
		{
			throw new IllegalArgumentException("webReqFactory must not be null");
		}
		if(evaluator == null)
		{
			throw new IllegalArgumentException("evaluator must not be null");
		}
		String typeName = evaluator.getEntityType().getName();
		logger.info(".get<%s>(): invoking request to webTarget '%s'", typeName, webTarget.toString());
		Invocation.Builder invocationBuilder =  webReqFactory.buildRequest();
		Response response = invocationBuilder.get();
		logger.info(".get<%s>(): request invoked", typeName);
		Status statusCode = Status.fromStatusCode(response.getStatus());
		T result = evaluator.evaluateResponse(response, statusCode);
		return result;
	}
	
	public void getFile(Path targetPath, WebReqFactory webReqFactory, ResponseEvaluator<ClientRoleInfo> evaluator, ResponseToFileMgr respMgr) throws FatalClientException
	{
		if(targetPath == null)
		{
			throw new IllegalArgumentException("targetPath must not be null");
		}
		if(webReqFactory == null)
		{
			throw new IllegalArgumentException("webReqFactory must not be null");
		}
		if(evaluator == null)
		{
			throw new IllegalArgumentException("evaluator must not be null");
		}
		if(respMgr == null)
		{
			throw new IllegalArgumentException("respMgr must not be null");
		}
		logger.info(".getFile(): invoking request to webTarget '%s'", webTarget.toString());
		Invocation.Builder invocationBuilder =  webReqFactory.buildRequest();
		Response response = invocationBuilder.get();
		logger.info(".getFile(): request invoked");
		Status statusCode = Status.fromStatusCode(response.getStatus());
		evaluator.evaluateResponse(response, statusCode);
		respMgr.copy(targetPath, response, statusCode);
	}
	
	public Invocation.Builder buildRequest()
	{
		return webTarget.request(MediaType.APPLICATION_JSON);
	}
}
