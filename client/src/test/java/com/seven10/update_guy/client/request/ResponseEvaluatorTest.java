/**
 * 
 */
package com.seven10.update_guy.client.request;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import com.seven10.update_guy.client.exceptions.FatalClientException;

/**
 * @author dra
 *
 */
public class ResponseEvaluatorTest
{

	/**
	 * Test method for {@link com.seven10.update_guy.client.request.ResponseEvaluator#ResponseEvaluator(java.lang.Class)}.
	 */
	@Test
	public void testResponseEvaluator()
	{
		ResponseEvaluator<String> respEval = new ResponseEvaluator<String>(String.class);
		assertNotNull(respEval);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#evaluateResponse(javax.ws.rs.core.Response, javax.ws.rs.core.Response.Status, java.lang.Class)}.
	 * @throws FatalClientException 
	 */
	@Test
	public void testEvaluateResponse_ok() throws FatalClientException
	{
		String expected = "response-string";
		Status statusCode = Status.OK;
		Class<String> entityType = String.class;
		Response response = mock(Response.class);
		doReturn(expected).when(response).readEntity(entityType);
		ResponseEvaluator<String> respEval = new ResponseEvaluator<String>(String.class);
		String actual = respEval.evaluateResponse(response, statusCode);
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#evaluateResponse(javax.ws.rs.core.Response, javax.ws.rs.core.Response.Status, java.lang.Class)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=FatalClientException.class)
	public void testEvaluateResponse_INT_SERV_ERR() throws FatalClientException
	{
		Status statusCode = Status.INTERNAL_SERVER_ERROR;
		Response response = mock(Response.class);
		ResponseEvaluator<String> respEval = new ResponseEvaluator<String>(String.class);
		respEval.evaluateResponse(response, statusCode);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#evaluateResponse(javax.ws.rs.core.Response, javax.ws.rs.core.Response.Status, java.lang.Class)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=FatalClientException.class)
	public void testEvaluateResponse_NOT_FOUND() throws FatalClientException
	{
		Status statusCode = Status.NOT_FOUND;
		Response response = mock(Response.class);
		ResponseEvaluator<String> respEval = new ResponseEvaluator<String>(String.class);
		respEval.evaluateResponse(response, statusCode);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#evaluateResponse(javax.ws.rs.core.Response, javax.ws.rs.core.Response.Status, java.lang.Class)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testEvaluateResponse_null_response() throws FatalClientException
	{
		Status statusCode = Status.OK;
		Response response = null;
		ResponseEvaluator<String> respEval = new ResponseEvaluator<String>(String.class);
		respEval.evaluateResponse(response, statusCode);
	}

}
