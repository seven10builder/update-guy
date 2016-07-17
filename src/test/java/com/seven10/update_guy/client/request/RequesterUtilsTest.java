/**
 * 
 */
package com.seven10.update_guy.client.request;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.client.request.RequesterUtils.RequesterFactory;
import com.seven10.update_guy.common.ManifestEntryHelpers;
import com.seven10.update_guy.common.manifest.ManifestEntry;

/**
 * @author kmm
 *
 */
@RunWith(MockitoJUnitRunner.class)
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
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#buildManifestReq()}.
	 */
	@Test
	public void testBuildManifestReq()
	{
		String expected = String.format("%s://%s:%d/manifest/%s", Requester.defaultProtocol, serverAddress, serverPort, repoId);
		
		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		settings.setRepoId(repoId);
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		String actual = requesterUtils.buildManifestReq();
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
		
		ManifestEntry expectedManifest = ManifestEntryHelpers.create_valid_manifest_entry(testName, 1, rootFolder);
		doReturn(expectedManifest).when(requester).get(any(), any());
		
		ManifestEntry actualManifest = requesterUtils.requestActiveRelease((url, methodName)->requester);
		assertEquals(expectedManifest, actualManifest);
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
		
		RequesterFactory requestorFactory = null;
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
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.manifest.ManifestEntry, java.nio.file.Path)}.
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
		ManifestEntry release = ManifestEntryHelpers.create_valid_manifest_entry(testName, 1, rootFolder);
		
		Path jarFilePath = Paths.get("some", "valid", "path");
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, (url, methodName)->requester);
		verify(requester, times(1)).addQueryParam("version", release.getVersion());	// make sure we're adding in the version
		verify(requester, times(1)).getFile(any(), any(), any());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.manifest.ManifestEntry, java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestDownloadRoleFile_null_release() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		
		Requester requester = mock(Requester.class);
		ManifestEntry release = null;		
		Path jarFilePath = Paths.get("some", "valid", "path");
		RequesterFactory requestorFactory = (url, methodName)->requester;
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, requestorFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.manifest.ManifestEntry, java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestDownloadRoleFile_null_jarFilepath() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		
		Requester requester = mock(Requester.class);
		ManifestEntry release = new ManifestEntry();		
		Path jarFilePath = null;
		RequesterFactory requestorFactory = (url, methodName)->requester;
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, requestorFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestDownloadRoleFile(com.seven10.update_guy.manifest.ManifestEntry, java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestDownloadRoleFile_null_requesterFactory() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		
		ManifestEntry release = new ManifestEntry();		
		Path jarFilePath = Paths.get("some", "valid", "path");
		RequesterFactory requestorFactory = null;
		
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		requesterUtils.requestDownloadRoleFile(release, jarFilePath, requestorFactory);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestRemoteChecksum(com.seven10.update_guy.manifest.ManifestEntry)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test
	public void testRequestRemoteChecksum_valid() throws IOException, FatalClientException
	{
		String testName = "reqRemCheck-v";
		ClientSettings settings = new ClientSettings();
		settings.setServerAddress(serverAddress);
		settings.setServerPort(serverPort);
		settings.setRepoId(repoId);
		
		String expectedChecksum = "some-checksum";
		
		Path rootFolder = folder.newFolder(testName).toPath();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
	

		Requester requester = mock(Requester.class);
		doReturn(expectedChecksum).when(requester).get(any(), any());		
		
		ManifestEntry release = ManifestEntryHelpers.create_valid_manifest_entry(testName, 1, rootFolder);
		
		String actualChecksum = requesterUtils.requestRemoteChecksum(release, (url, methodName)->requester);
		assertEquals(expectedChecksum, actualChecksum);
		verify(requester, times(1)).addQueryParam("version", release.getVersion());	// make sure we're adding in the version
		verify(requester,times(1)).get(any(), any());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestRemoteChecksum(com.seven10.update_guy.manifest.ManifestEntry)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestRemoteChecksum_null_release() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		ManifestEntry release = null;
		RequesterFactory requesterFactory = (url, methodName)->mock(Requester.class);
		requesterUtils.requestRemoteChecksum(release, requesterFactory);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.request.RequesterUtils#requestRemoteChecksum(com.seven10.update_guy.manifest.ManifestEntry)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRequestRemoteChecksum_null_reqFactory() throws IOException, FatalClientException
	{
		ClientSettings settings = new ClientSettings();
		RequesterUtils requesterUtils = new RequesterUtils(settings);
		ManifestEntry release = new ManifestEntry();
		RequesterFactory requesterFactory = null;
		requesterUtils.requestRemoteChecksum(release, requesterFactory);
	}
	
}
