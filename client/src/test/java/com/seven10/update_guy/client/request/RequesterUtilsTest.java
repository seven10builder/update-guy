package com.seven10.update_guy.client.request;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.client.FunctionalInterfaces;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.common.ReleaseFamilyEntryHelpers;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.common.release_family.UpdateGuyRole.ClientRoleInfo;

/**
 * @author kmm
 *
 */
public class RequesterUtilsTest
{
	private final static int serverPort = 31337;
	private final static String serverAddress = "some-address";
	private final static String repoId = "some-repo-id";
	private final static String roleName = "some-rolename";
	private final static String releaseFamily = "someReleaseFamily";
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#RequesterUtils(com.seven10.update_guy.client.ClientSettings)}.
	 */
	@Test
	public void testRequesterUtils()
	{
		ClientSettings settings = new ClientSettings();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		assertNotNull(requesterUtils);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#RequesterUtils(com.seven10.update_guy.client.ClientSettings)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequesterUtils_null_settings()
	{
		ClientSettings settings = null;
		new RequesterUtils(settings);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#buildPrefix()}.
	 */
	@Test
	public void testBuildPrefix()
	{
		String expected = String.format("%s://%s:%d", Requester.defaultProtocol, serverAddress, serverPort);
		
		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		String actual = requesterUtils.buildPrefix();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#buildReleaseFamilyReq()}.
	 */
	@Test
	public void testBuildReleaseFamilyReq()
	{
		String expected = String.format("%s://%s:%d/release-family/%s", Requester.defaultProtocol, serverAddress, serverPort, repoId);
		
		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		settings.setRepoId(repoId);
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		String actual = requesterUtils.buildReleaseFamilyReq();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestActiveRelease()}.
	 * @throws FatalClientException 
	 * @throws IOException 
	 */
	@Test
	public void testRequestActiveRelease_valid() throws FatalClientException, IOException
	{
		String testName = "reqActiveRel";
		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		settings.setRepoId(repoId);
		
		Path rootFolder = folder.newFolder(testName).toPath();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		Requester requester = mock(Requester.class);
		
		ReleaseFamilyEntry expectedReleaseFamily = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		doReturn(expectedReleaseFamily).when(requester).get(any(), any());
		
		ReleaseFamilyEntry actualReleaseFamily = requesterUtils.requestActiveRelease((url, methodName)->requester);
		assertEquals(expectedReleaseFamily, actualReleaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestActiveRelease()}.
	 * @throws FatalClientException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestActiveRelease_null_factory() throws FatalClientException, IOException
	{
		ClientSettings settings = new ClientSettings();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		
		FunctionalInterfaces.RequesterFactory requestorFactory = null;
		requesterUtils.requestActiveRelease(requestorFactory);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#buildReleaseReq()}.
	 */
	@Test
	public void testBuildReleaseReq()
	{
		String expected = String.format("%s://%s:%d/release/%s/%s",
				 				Requester.defaultProtocol,
				 				serverAddress,
				 				serverPort,
								repoId,
								releaseFamily);
		
		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		settings.setRepoId(repoId);
		settings.setReleaseFamily(releaseFamily);
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		String actual = requesterUtils.buildReleaseReq();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.ReleaseFamilyEntry.ReleaseFamilyEntry, java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test
	public void testRequestDownloadRoleFile_valid() throws IOException, FatalClientException
	{
		String testName = "reqDownloadRoleFile";

		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		settings.setRepoId(repoId);
		settings.setRoleName(roleName);
		
		Path rootFolder = folder.newFolder(testName).toPath();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		Requester requester = mock(Requester.class);
		ReleaseFamilyEntry release = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		
		Path jarFilePath = Paths.get("some", "valid", "path");
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, (url, methodName)->requester);
		verify(requester, times(1)).addQueryParam("version", release.getVersion());	// make sure we're adding in the version
		verify(requester, times(1)).getFile(any(), any(), any(), any());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.ReleaseFamilyEntry.ReleaseFamilyEntry, java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestDownloadRoleFile_null_release() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		
		Requester requester = mock(Requester.class);
		ReleaseFamilyEntry release = null;		
		Path jarFilePath = Paths.get("some", "valid", "path");
		FunctionalInterfaces.RequesterFactory requestorFactory = (url, methodName)->requester;
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, requestorFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.ReleaseFamilyEntry.ReleaseFamilyEntry, java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestDownloadRoleFile_null_jarFilepath() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		
		Requester requester = mock(Requester.class);
		ReleaseFamilyEntry release = new ReleaseFamilyEntry();		
		Path jarFilePath = null;
		FunctionalInterfaces.RequesterFactory requestorFactory = (url, methodName)->requester;
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, requestorFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.ReleaseFamilyEntry.ReleaseFamilyEntry, java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestDownloadRoleFile_null_requesterFactory() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		
		ReleaseFamilyEntry release = new ReleaseFamilyEntry();		
		Path jarFilePath = Paths.get("some", "valid", "path");
		FunctionalInterfaces.RequesterFactory requestorFactory = null;
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, requestorFactory);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestRemoteRoleInfo(com.seven10.update_guy.ReleaseFamilyEntry.ReleaseFamilyEntry)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test
	public void testRequestRemoteRoleInfo_valid() throws IOException, FatalClientException
	{
		String testName = "reqRemCheck-v";
		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		settings.setRepoId(repoId);
		
		String expectedFingerPrint = "some-RoleInfo";
		List<String> expectedCmdList = Arrays.asList(new String[]{"cmd1", "cmd2"});
		ClientRoleInfo expectedInfo = new ClientRoleInfo(expectedFingerPrint, expectedCmdList);
		
		Path rootFolder = folder.newFolder(testName).toPath();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
	

		Requester requester = mock(Requester.class);
		doReturn(expectedInfo).when(requester).get(any(), any());		
		
		ReleaseFamilyEntry release = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		
		ClientRoleInfo actualRoleInfo = requesterUtils.requestRemoteClientRoleInfo(release, (url, methodName)->requester);
		assertEquals(expectedFingerPrint, actualRoleInfo.fingerPrint);
		verify(requester, times(1)).addQueryParam("version", release.getVersion());	// make sure we're adding in the version
		verify(requester,times(1)).get(any(), any());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestRemoteRoleInfo(com.seven10.update_guy.ReleaseFamilyEntry.ReleaseFamilyEntry)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestRemoteRoleInfo_null_release() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		ReleaseFamilyEntry release = null;
		FunctionalInterfaces.RequesterFactory requesterFactory = (url, methodName)->mock(Requester.class);
		requesterUtils.requestRemoteClientRoleInfo(release, requesterFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestRemoteRoleInfo(com.seven10.update_guy.ReleaseFamilyEntry.ReleaseFamilyEntry)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestRemoteRoleInfo_null_reqFactory() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		ReleaseFamilyEntry release = new ReleaseFamilyEntry();
		FunctionalInterfaces.RequesterFactory requesterFactory = null;
		requesterUtils.requestRemoteClientRoleInfo(release, requesterFactory);
	}
	
}
