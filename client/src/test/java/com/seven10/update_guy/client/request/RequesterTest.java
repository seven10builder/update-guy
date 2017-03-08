/**
 * 
 */
package com.seven10.update_guy.client.request;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.seven10.update_guy.client.FunctionalInterfaces;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.client.request.Requester;
import com.seven10.update_guy.common.release_family.UpdateGuyRole.ClientRoleInfo;

/**
 * @author kmm
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RequesterTest
{
	
	private static final String TEST_METHOD_NAME = "some-method";

	private static final String TEST_URL = "localhost://some-path/";

	@Mock
	public ResponseEvaluator<ClientRoleInfo> mockedRespEvaluator;
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testRequester_valid()
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_null_url()
	{
		String url = null;
		Requester requester = new Requester(url, TEST_METHOD_NAME);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_empty_url()
	{
		String url = "";
		Requester requester = new Requester(url, TEST_METHOD_NAME);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_null_methodName()
	{
		String methodName = null;
		Requester requester = new Requester(TEST_URL, methodName);
		assertNotNull(requester);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#Requester(java.lang.String, java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequester_empty_methodName()
	{
		String methodName = "";
		Requester requester = new Requester(TEST_URL, methodName);
		assertNotNull(requester);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#addQueryParam(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddQueryParam_valid()
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
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
		String methodName = null;
		Requester requester = new Requester(TEST_URL, methodName);
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
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
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
		String methodName = null;
		Requester requester = new Requester(TEST_URL, methodName);
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
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		String name = "paramName";
		String value = "";
		requester.addQueryParam(name, value);
	}
	

	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#get(java.lang.Class)}.
	 * @throws FatalClientException 
	 */
	@Test
	public void testGet_valid() throws FatalClientException
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		
		String expectedFingerprint = "this-got-through";
		List<String> expectedCmdList = Arrays.asList(new String[]{"cmd1", "cmd2"});
		ClientRoleInfo expectedInfo = new ClientRoleInfo(expectedFingerprint, expectedCmdList);
		
		Response mockedResponse = mock(Response.class);
		int mockStatusInt = 200;
		doReturn(mockStatusInt).when(mockedResponse).getStatus();
		
		Invocation.Builder mockedInvocationBuilder = mock(Invocation.Builder.class);
		doReturn(mockedResponse).when(mockedInvocationBuilder).get();

		doReturn(ClientRoleInfo.class).when(mockedRespEvaluator).getEntityType();
		doReturn(expectedInfo).when(mockedRespEvaluator).evaluateResponse(any(), any());
		
		FunctionalInterfaces.WebReqFactory webReqFactory = mock(FunctionalInterfaces.WebReqFactory.class);
		doReturn(mockedInvocationBuilder).when(webReqFactory).buildRequest();
		
		ClientRoleInfo actualInfo = requester.get(webReqFactory, mockedRespEvaluator);
		assertEquals(expectedFingerprint, actualInfo.fingerPrint);
		assertEquals(expectedCmdList, actualInfo.commandLine);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#get(java.lang.Class)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGet_null_webReqFactory() throws FatalClientException
	{
		
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		FunctionalInterfaces.WebReqFactory webReqFactory =  null;
		
		requester.get(webReqFactory, mockedRespEvaluator);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#get(java.lang.Class)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGet_null_respEvaluator() throws FatalClientException
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
					
		FunctionalInterfaces.WebReqFactory webReqFactory =  mock(FunctionalInterfaces.WebReqFactory.class);
		
		ResponseEvaluator<String> respEvaluator = null;
		requester.get(webReqFactory, respEvaluator);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#getFile(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 */
	@Test
	public void testGetFile_valid() throws FatalClientException
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		
		String expectedString = "this-got-through";
		
		Response mockedResponse = mock(Response.class);
		int mockStatusInt = 200;
		doReturn(mockStatusInt).when(mockedResponse).getStatus();
		
		Invocation.Builder mockedInvocationBuilder = mock(Invocation.Builder.class);
		doReturn(mockedResponse).when(mockedInvocationBuilder).get();

		doReturn(String.class).when(mockedRespEvaluator).getEntityType();
		doReturn(expectedString).when(mockedRespEvaluator).evaluateResponse(any(), any());
		
		FunctionalInterfaces.WebReqFactory webReqFactory = mock(FunctionalInterfaces.WebReqFactory.class);
		doReturn(mockedInvocationBuilder).when(webReqFactory).buildRequest();
		Path jarPath = Paths.get("this", "is", "a", "path") ;
		FunctionalInterfaces.ResponseToFileMgr respMgr = mock(FunctionalInterfaces.ResponseToFileMgr.class);
		requester.getFile(jarPath, webReqFactory, mockedRespEvaluator, respMgr);
		verify(respMgr, times(1)).copy(jarPath, mockedResponse, Status.OK);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#getFile(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetFile_null_jarPath() throws FatalClientException
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		
		Path jarPath = null;
		FunctionalInterfaces.WebReqFactory webReqFactory =  mock(FunctionalInterfaces.WebReqFactory.class);
		ResponseEvaluator<ClientRoleInfo> respEvaluator = mockedRespEvaluator;
		FunctionalInterfaces.ResponseToFileMgr respMgr = mock(FunctionalInterfaces.ResponseToFileMgr.class);
		requester.getFile(jarPath, webReqFactory, respEvaluator, respMgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#getFile(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetFile_null_weReqFactory() throws FatalClientException
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		
		Path jarPath = Paths.get("your", "mom");
		FunctionalInterfaces.WebReqFactory webReqFactory =  null;
		ResponseEvaluator<ClientRoleInfo> respEvaluator = mockedRespEvaluator;
		FunctionalInterfaces.ResponseToFileMgr respMgr = mock(FunctionalInterfaces.ResponseToFileMgr.class);
		requester.getFile(jarPath, webReqFactory, respEvaluator, respMgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#getFile(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetFile_null_respEval() throws FatalClientException
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		
		Path jarPath = Paths.get("your", "mom");
		FunctionalInterfaces.WebReqFactory webReqFactory =  mock(FunctionalInterfaces.WebReqFactory.class);
		ResponseEvaluator<ClientRoleInfo> respEvaluator = mockedRespEvaluator;
		FunctionalInterfaces.ResponseToFileMgr respMgr = null;
		requester.getFile(jarPath, webReqFactory, respEvaluator, respMgr);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.Requester#getFile(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetFile_null_respMgr() throws FatalClientException
	{
		Requester requester = new Requester(TEST_URL, TEST_METHOD_NAME);
		
		Path jarPath = Paths.get("your", "mom");
		FunctionalInterfaces.WebReqFactory webReqFactory =  mock(FunctionalInterfaces.WebReqFactory.class);
		ResponseEvaluator<ClientRoleInfo> respEvaluator = mockedRespEvaluator;
		FunctionalInterfaces.ResponseToFileMgr respMgr = null;
		requester.getFile(jarPath, webReqFactory, respEvaluator, respMgr);
	}
}
