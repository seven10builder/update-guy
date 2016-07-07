/**
 * 
 */
package com.seven10.update_guy.client.request;

import static org.junit.Assert.*;

import org.junit.Test;

import com.seven10.update_guy.client.request.Requester;

/**
 * @author kmm
 *
 */
public class RequesterTest
{
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRequester_valid()
	{
		String url = "localhost://some-path/";
		String methodName = "some-method";
		Requester requester = new Requester(url, methodName);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_null_url()
	{
		String url = null;
		String methodName = "some-method";
		Requester requester = new Requester(url, methodName);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_empty_url()
	{
		String url = "";
		String methodName = "some-method";
		Requester requester = new Requester(url, methodName);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_null_methodName()
	{
		String url = "localhost://some-path/";
		String methodName = null;
		Requester requester = new Requester(url, methodName);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_empty_methodName()
	{
		String url = "localhost://some-path/";
		String methodName = "";
		Requester requester = new Requester(url, methodName);
		assertNotNull(requester);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#addQueryParam(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddQueryParam_valid()
	{
		String url = "localhost://some-path/";
		String methodName = "some-method";
		Requester requester = new Requester(url, methodName);
		String name = "paramName";
		String value = "paramValue";
		requester.addQueryParam(name, value);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#addQueryParam(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddQueryParam_null_name()
	{
		String url = "localhost://some-path/";
		String methodName = null;
		Requester requester = new Requester(url, methodName);
		String name = null;
		String value = "paramValue";
		requester.addQueryParam(name, value);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#addQueryParam(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddQueryParam_empty_name()
	{
		String url = "localhost://some-path/";
		String methodName = "some-method";
		Requester requester = new Requester(url, methodName);
		String name = "";
		String value = "paramValue";
		requester.addQueryParam(name, value);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#addQueryParam(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddQueryParam_null_value()
	{
		String url = "localhost://some-path/";
		String methodName = null;
		Requester requester = new Requester(url, methodName);
		String name = "paramName";
		String value = null;
		requester.addQueryParam(name, value);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#addQueryParam(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddQueryParam_empty_value()
	{
		String url = "localhost://some-path/";
		String methodName = "some-method";
		Requester requester = new Requester(url, methodName);
		String name = "paramName";
		String value = "";
		requester.addQueryParam(name, value);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#get(java.lang.Class)}.
	 */
	@Test
	public void testGet()
	{
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#getFile(java.nio.file.Path)}.
	 */
	@Test
	public void testGetFile()
	{
		fail("Not yet implemented"); // TODO
	}
	
}
