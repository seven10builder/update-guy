/**
 * 
 */
package com.seven10.update_guy.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.seven10.update_guy.client.FunctionalInterfaces.UpdateGuyClientFactory;
import com.seven10.update_guy.client.FunctionalInterfaces.JavaLauncherFactory;
import com.seven10.update_guy.client.FunctionalInterfaces.LocalCacheUtilsFactory;
import com.seven10.update_guy.client.FunctionalInterfaces.RequesterUtilsFactory;
import com.seven10.update_guy.client.cli.CliMgr;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.client.local.JavaLauncher;
import com.seven10.update_guy.client.local.LocalCacheUtils;
import com.seven10.update_guy.client.request.RequesterUtils;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.common.manifest.UpdateGuyRole.ClientRoleInfo;

/**
 * @author kmm
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateGuyClientTest
{
	private static final String expectedChecksum = "some-checksum";
	
	@Mock
	public ManifestEntry mockedManifestEntry;
	
	private static final String[] expectedParams = new String[]{"param1", "param2"};
	/**
	 * @param expectedChecksum
	 * @param mockedManifestEntry
	 * @return
	 * @throws FatalClientException
	 */
	private RequesterUtils createMockedRequesterUtils(String expectedChecksum, ManifestEntry mockedManifestEntry)
			throws FatalClientException
	{
		ClientRoleInfo clientRoleInfo = new ClientRoleInfo(expectedChecksum, Arrays.asList(expectedParams));
		RequesterUtils mockedRequestUtils = mock(RequesterUtils.class);
		doReturn(mockedManifestEntry).when(mockedRequestUtils).requestActiveRelease(any());
		doReturn(clientRoleInfo).when(mockedRequestUtils).requestRemoteClientRoleInfo(eq(mockedManifestEntry), any());
		return mockedRequestUtils;
	}
	/**
	 * @param localChecksum
	 * @param jarFilePath
	 * @param mockedManifestEntry
	 * @return
	 * @throws FatalClientException
	 */
	private LocalCacheUtils createMockedLocalCacheUtils(String localChecksum, Path jarFilePath,
			ManifestEntry mockedManifestEntry) throws FatalClientException
	{
		LocalCacheUtils mockedLocalCacheUtils = mock(LocalCacheUtils.class);
		doReturn(jarFilePath).when(mockedLocalCacheUtils).buildTargetPath(mockedManifestEntry);
		doReturn(localChecksum).when(mockedLocalCacheUtils).getLocalChecksum(jarFilePath);
		return mockedLocalCacheUtils;
	}
	/**
	 * @param expected
	 * @param processBuilder
	 * @return
	 * @throws FatalClientException
	 */
	private JavaLauncher createMockedLauncher(boolean expected, ProcessBuilder processBuilder)
			throws FatalClientException
	{
		JavaLauncher mockedLauncher = mock(JavaLauncher.class);
		doReturn(processBuilder).when(mockedLauncher).createProcessBuilder(any(), any());
		doReturn(expected).when(mockedLauncher).launchExecutable(processBuilder);
		return mockedLauncher;
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#main(java.lang.String[])}.
	 * @throws FatalClientException 
	 */
	@Test
	public void testDoClient_valid() throws FatalClientException
	{
		
		CliMgr cliMgr = mock(CliMgr.class);
		doReturn(true).when(cliMgr).parse(any());
		doReturn(new ClientSettings()).when(cliMgr).getClientSettings();
		doReturn(expectedParams).when(cliMgr).getRemainingParams();
		
		UpdateGuyClient mockedClient = mock(UpdateGuyClient.class);
		// we want to return true the first time then false the second time.
		doReturn(true).doReturn(false).when(mockedClient).executeClientLoop(any(), any(), any());

		UpdateGuyClientFactory clientFactory = (parms)->mockedClient;
		RequesterUtilsFactory requesterUtilsFactory = (settings)->mock(RequesterUtils.class);
		LocalCacheUtilsFactory localCacheUtilsFactory = (settings)->mock(LocalCacheUtils.class);
		JavaLauncherFactory javaLauncherFactory = (settings)->mock(JavaLauncher.class);
		
		UpdateGuyClient.doClient(cliMgr, requesterUtilsFactory, localCacheUtilsFactory, javaLauncherFactory, clientFactory);
		// The loop should only execute twice
		verify(mockedClient, times(2)).executeClientLoop(any(), any(), any());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#main(java.lang.String[])}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDoClient_null_cliMgr() throws FatalClientException
	{
		
		CliMgr cliMgr = null;
		UpdateGuyClientFactory clientFactory = (params)->mock(UpdateGuyClient.class);
		RequesterUtilsFactory requesterUtilsFactory = (settings)->mock(RequesterUtils.class);
		LocalCacheUtilsFactory localCacheUtilsFactory = (settings)->mock(LocalCacheUtils.class);
		JavaLauncherFactory javaLauncherFactory = (settings)->mock(JavaLauncher.class);
		
		UpdateGuyClient.doClient(cliMgr, requesterUtilsFactory, localCacheUtilsFactory, javaLauncherFactory, clientFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#main(java.lang.String[])}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDoClient_null_clientFactory() throws FatalClientException
	{
		
		CliMgr cliMgr = mock(CliMgr.class);
		UpdateGuyClientFactory clientFactory = null;
		RequesterUtilsFactory requesterUtilsFactory = (settings)->mock(RequesterUtils.class);
		LocalCacheUtilsFactory localCacheUtilsFactory = (settings)->mock(LocalCacheUtils.class);
		JavaLauncherFactory javaLauncherFactory = (settings)->mock(JavaLauncher.class);
		
		UpdateGuyClient.doClient(cliMgr, requesterUtilsFactory, localCacheUtilsFactory, javaLauncherFactory, clientFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#main(java.lang.String[])}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDoClient_null_requesterUtilsFactory() throws FatalClientException
	{
		
		CliMgr cliMgr = mock(CliMgr.class);
		UpdateGuyClientFactory clientFactory = (params)->mock(UpdateGuyClient.class);
		RequesterUtilsFactory requesterUtilsFactory = null;
		LocalCacheUtilsFactory localCacheUtilsFactory = (settings)->mock(LocalCacheUtils.class);
		JavaLauncherFactory javaLauncherFactory = (settings)->mock(JavaLauncher.class);
		
		UpdateGuyClient.doClient(cliMgr, requesterUtilsFactory, localCacheUtilsFactory, javaLauncherFactory, clientFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#main(java.lang.String[])}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDoClient_null_localCacheUtilsFactory() throws FatalClientException
	{
		
		CliMgr cliMgr = mock(CliMgr.class);
		UpdateGuyClientFactory clientFactory = (params)->mock(UpdateGuyClient.class);
		RequesterUtilsFactory requesterUtilsFactory = (settings)->mock(RequesterUtils.class);
		LocalCacheUtilsFactory localCacheUtilsFactory = null;
		JavaLauncherFactory javaLauncherFactory = (settings)->mock(JavaLauncher.class);
		
		UpdateGuyClient.doClient(cliMgr, requesterUtilsFactory, localCacheUtilsFactory, javaLauncherFactory, clientFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#main(java.lang.String[])}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDoClient_null_javaLauncherFactory() throws FatalClientException
	{
		
		CliMgr cliMgr = mock(CliMgr.class);
		UpdateGuyClientFactory clientFactory = (params)->mock(UpdateGuyClient.class);
		RequesterUtilsFactory requesterUtilsFactory = (settings)->mock(RequesterUtils.class);
		LocalCacheUtilsFactory localCacheUtilsFactory = (settings)->mock(LocalCacheUtils.class);
		JavaLauncherFactory javaLauncherFactory = null;
		
		UpdateGuyClient.doClient(cliMgr, requesterUtilsFactory, localCacheUtilsFactory, javaLauncherFactory, clientFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#UpdateGuyClient(java.lang.String[])}.
	 */
	@Test
	public void testUpdateGuyClient_valid()
	{
		UpdateGuyClient client = new UpdateGuyClient(expectedParams);
		assertNotNull(client);
		assertEquals(Arrays.asList(expectedParams), client.remainingParams);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#UpdateGuyClient(java.lang.String[])}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateGuyClient_null()
	{
		String []params = null;
		new UpdateGuyClient(params);

	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#executeClientLoop(com.seven10.update_guy.client.request.RequesterUtils, com.seven10.update_guy.client.local.LocalCacheUtils, com.seven10.update_guy.client.local.JavaLauncher)}.
	 * @throws FatalClientException 
	 */
	@Test
	public void testExecuteClientLoop_valid_no_download() throws FatalClientException
	{
		boolean expected = true;
		Path jarFilePath = Paths.get("this", "is", "the", "one", "true", "path");
		
		ManifestEntry mockedManifestEntry = mock(ManifestEntry.class);
		
		RequesterUtils mockedRequestUtils = createMockedRequesterUtils(expectedChecksum, mockedManifestEntry);
		
		LocalCacheUtils mockedLocalCacheUtils = createMockedLocalCacheUtils(expectedChecksum, jarFilePath,
				mockedManifestEntry);

		ProcessBuilder processBuilder = new ProcessBuilder();
		JavaLauncher mockedLauncher = createMockedLauncher(expected, processBuilder);
		
		
		UpdateGuyClient client = new UpdateGuyClient(expectedParams);
		boolean actual = client.executeClientLoop(mockedRequestUtils, mockedLocalCacheUtils, mockedLauncher);
		
		verify(mockedRequestUtils, times(0)).requestDownloadRoleFile(eq(mockedManifestEntry), eq(jarFilePath), any());		
		verify(mockedLauncher, times(1)).launchExecutable(processBuilder);
		assertTrue(actual);
	}

	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#executeClientLoop(com.seven10.update_guy.client.request.RequesterUtils, com.seven10.update_guy.client.local.LocalCacheUtils, com.seven10.update_guy.client.local.JavaLauncher)}.
	 * @throws FatalClientException 
	 */
	@Test
	public void testExecuteClientLoop_valid_with_download() throws FatalClientException
	{
		String localChecksum = "different-checksum";
		boolean expected = true;
		Path jarFilePath = Paths.get("this", "is", "the", "one", "true", "path");
		
		RequesterUtils mockedRequestUtils = createMockedRequesterUtils(expectedChecksum, mockedManifestEntry);
		
		LocalCacheUtils mockedLocalCacheUtils = createMockedLocalCacheUtils(localChecksum, jarFilePath,
				mockedManifestEntry);

		ProcessBuilder processBuilder = new ProcessBuilder();
		JavaLauncher mockedLauncher = createMockedLauncher(expected, processBuilder);
		
		UpdateGuyClient client = new UpdateGuyClient(expectedParams);
		boolean actual = client.executeClientLoop(mockedRequestUtils, mockedLocalCacheUtils, mockedLauncher);
		verify(mockedRequestUtils, times(1)).requestDownloadRoleFile(eq(mockedManifestEntry), eq(jarFilePath), any());		
		verify(mockedLauncher, times(1)).launchExecutable(processBuilder);
		assertTrue(actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#executeClientLoop(com.seven10.update_guy.client.request.RequesterUtils, com.seven10.update_guy.client.local.LocalCacheUtils, com.seven10.update_guy.client.local.JavaLauncher)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteClientLoop_null_requestUtils() throws FatalClientException
	{
		RequesterUtils requestUtils = null;
		LocalCacheUtils localCacheUtils = mock(LocalCacheUtils.class);
		JavaLauncher launcher = mock(JavaLauncher.class);
		
		UpdateGuyClient client = new UpdateGuyClient(expectedParams);
		client.executeClientLoop(requestUtils, localCacheUtils, launcher);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#executeClientLoop(com.seven10.update_guy.client.request.RequesterUtils, com.seven10.update_guy.client.local.LocalCacheUtils, com.seven10.update_guy.client.local.JavaLauncher)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteClientLoop_null_cacheUtils() throws FatalClientException
	{
		RequesterUtils requestUtils = mock(RequesterUtils.class);
		LocalCacheUtils localCacheUtils = null;
		JavaLauncher launcher = mock(JavaLauncher.class);
		
		UpdateGuyClient client = new UpdateGuyClient(expectedParams);
		client.executeClientLoop(requestUtils, localCacheUtils, launcher);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.UpdateGuyClient#executeClientLoop(com.seven10.update_guy.client.request.RequesterUtils, com.seven10.update_guy.client.local.LocalCacheUtils, com.seven10.update_guy.client.local.JavaLauncher)}.
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteClientLoop_null_launcher() throws FatalClientException
	{
		RequesterUtils requestUtils = mock(RequesterUtils.class);
		LocalCacheUtils localCacheUtils = mock(LocalCacheUtils.class);
		JavaLauncher launcher = null;
		
		UpdateGuyClient client = new UpdateGuyClient(expectedParams);
		client.executeClientLoop(requestUtils, localCacheUtils, launcher);
	}

}
