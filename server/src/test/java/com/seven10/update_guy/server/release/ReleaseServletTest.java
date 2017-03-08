/**
 * 
 */
package com.seven10.update_guy.server.release;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.common.FileFingerPrint;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.ReleaseFamilyHelpers;
import com.seven10.update_guy.server.helpers.RepoInfoHelpers;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.common.release_family.UpdateGuyRole;
import com.seven10.update_guy.common.release_family.UpdateGuyRole.ClientRoleInfo;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.server.release.ReleaseServlet;

import junitx.framework.FileAssert;

/**
 * @author kmm
 *
 */
public class ReleaseServletTest extends JerseyTest
{
	/**
	 * @param testName
	 * @param entry
	 * @param downloadPath
	 * @param roleName
	 * @param resp
	 * @throws IOException
	 */
	private void validateDownloadedFile(String testName, ReleaseFamilyEntry entry, File downloadPath, String roleName,
			Response resp) throws IOException
	{
		File actualFile = getFileFromDownload(testName, resp, downloadPath);
		File expectedFile = entry.getRoleInfo(roleName).getFilePath().toFile();
		FileAssert.assertBinaryEquals(expectedFile, actualFile);
	}
	
	/**
	 * @param testName
	 * @param resp
	 * @param downloadFile 
	 * @return 
	 * @throws IOException
	 */
	private File getFileFromDownload(String testName, Response resp, File downloadFile) throws IOException
	{
		InputStream is = resp.readEntity(InputStream.class);
		Files.copy(is, downloadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		IOUtils.closeQuietly(is);
		return downloadFile;
	}
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Override
	protected Application configure()
	{
		ResourceConfig resourceConfig = new ResourceConfig(ReleaseServlet.class);
		return resourceConfig;
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#ReleaseServlet(com.seven10.update_guy.release.CacheManager)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testReleaseServlet_valid() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "ctor-v";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		List<ReleaseFamily> releaseFamilyList = ReleaseFamilyHelpers.load_releaseFamily_list_from_path(repoInfo.getRemoteReleaseFamilyPath());
		for(ReleaseFamily releaseFamily: releaseFamilyList)
		{
			String relFamilyName = releaseFamily.getReleaseFamily();
			ReleaseServlet releaseServlet = new ReleaseServlet(repoInfo.getShaHash(), relFamilyName);
			assertNotNull(releaseServlet);
		}
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#ReleaseServlet(com.seven10.update_guy.release.CacheManager)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test(expected=RepositoryException.class)
	public void testReleaseServlet_repoId_notFound() throws RepositoryException, IOException, UpdateGuyException
	{
		String testName = "repoId-nf";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		List<ReleaseFamily> releaseFamilyList = ReleaseFamilyHelpers.load_releaseFamily_list_from_path(repoInfo.getRemoteReleaseFamilyPath());
		// calc an invalid repoId
		String repoId = repoInfo.getShaHash() + "breakrepoid";
	    
		ReleaseFamily releaseFamily = releaseFamilyList.get(0);
		String relFamilyName = releaseFamily.getReleaseFamily();
		new ReleaseServlet(repoId, relFamilyName);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#ReleaseServlet(com.seven10.update_guy.release.CacheManager)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=RepositoryException.class)
	public void testReleaseServlet_rf_notFound() throws RepositoryException, IOException
	{
		String testName = "ctor-rfnf";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		String releaseFamily = "someunknownRelease";
		new ReleaseServlet(repoInfo.getShaHash(), releaseFamily);
	}
	
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#getRoles()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetRoles() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "getRoles-v";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		List<ReleaseFamily> releaseFamilyList = ReleaseFamilyHelpers.load_releaseFamily_list_from_path(repoInfo.getRemoteReleaseFamilyPath());
		String repoId = repoInfo.getShaHash();
		
		for(ReleaseFamily releaseFamily: releaseFamilyList)
		{
			String relFamilyName = releaseFamily.getReleaseFamily();
			for(ReleaseFamilyEntry entry: releaseFamily.getVersionEntries())
			{
				String version = entry.getVersion();
				List<String> expectedRoles = entry.getRoles();
				// do request
				String path = "/release/"+ repoId + "/" + relFamilyName + "/roles";
				Response resp = target(path).queryParam("version", version).request().get();
				
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				String json = resp.readEntity(String.class);
				Gson gson = GsonFactory.getGson();
				
				Type collectionType = new TypeToken<List<String>>()
				{
				}.getType();
				List<ReleaseFamily> actualRoles = gson.fromJson(json, collectionType);
				assertNotNull(actualRoles);
				assertNotEquals(0, actualRoles.size());
				assertTrue(expectedRoles.containsAll(actualRoles));
				assertTrue(actualRoles.containsAll(expectedRoles));
			}
		}
	}

	
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#getFingerprint(java.lang.String)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetFingerprint_valid() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "getFingerprint-v";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		List<ReleaseFamily> releaseFamilyList = ReleaseFamilyHelpers.load_releaseFamily_list_from_path(repoInfo.getRemoteReleaseFamilyPath());
		String repoId = repoInfo.getShaHash();
		
		for(ReleaseFamily releaseFamily: releaseFamilyList)
		{
			String relFamilyName = releaseFamily.getReleaseFamily();
			for(ReleaseFamilyEntry entry: releaseFamily.getVersionEntries())
			{
				String version = entry.getVersion();
				List<String> expectedRoles = entry.getRoles();
				// do request
				for(String roleName: expectedRoles)
				{
					String expectedFingerPrint = FileFingerPrint.create(entry.getRoleInfo(roleName).getFilePath());
					String path = "/release/"+ repoId + "/" + relFamilyName + "/roleInfo/" + roleName;
					Response resp = target(path).queryParam("version", version).request().get();
					assertEquals(Status.OK.getStatusCode(), resp.getStatus());
					String roleInfoString = resp.readEntity(String.class);
					ClientRoleInfo roleInfo = GsonFactory.getGson().fromJson(roleInfoString, ClientRoleInfo.class);
					
					assertEquals("(" + relFamilyName + "," + version + ", " + roleName + ")", 
							expectedFingerPrint, roleInfo.fingerPrint);
				}
			}
		}
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#getFile(java.lang.String)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetFile_valid() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "getFiles_v";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		List<ReleaseFamily> releaseFamilyList = ReleaseFamilyHelpers.load_releaseFamily_list_from_path(repoInfo.getRemoteReleaseFamilyPath());
		String repoId = repoInfo.getShaHash();
		
		for(ReleaseFamily releaseFamily: releaseFamilyList)
		{
			String relFamilyName = releaseFamily.getReleaseFamily();
			for(ReleaseFamilyEntry entry: releaseFamily.getVersionEntries())
			{
				String version = entry.getVersion();
				List<String> expectedRoles = entry.getRoles();
				// do request
				File downloadPath = folder.newFolder(testName+"-"+relFamilyName + "-" + version);
				for(String roleName: expectedRoles)
				{
					String path = "/release/"+ repoId + "/" + relFamilyName + "/download/" + roleName;
					Response resp = target(path).queryParam("version", version).request().get();
					assertEquals(Status.OK.getStatusCode(), resp.getStatus());
					
					validateDownloadedFile(testName, entry, downloadPath, roleName, resp);
				}
			}
		}
	}
	
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#doUpdateCache(javax.ws.rs.container.AsyncResponse)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testDoUpdateCache() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "doUpdate-cache";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		List<ReleaseFamily> releaseFamilyList = ReleaseFamilyHelpers.load_releaseFamily_list_from_path(repoInfo.getRemoteReleaseFamilyPath());
		String repoId = repoInfo.getShaHash();
		
		for(ReleaseFamily releaseFamily: releaseFamilyList)
		{
			for(ReleaseFamilyEntry releaseFamilyEntry: releaseFamily.getVersionEntries())
			{
				String version = releaseFamilyEntry.getVersion();
				String relFamilyName = releaseFamilyEntry.getReleaseFamily();
				assertNotEquals("unknown", relFamilyName);
				String path = "/release/"+ repoId + "/" + relFamilyName + "/update-cache";
				Response resp = target(path).queryParam("version", version).request().get();
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				
				// now test that each file was downloaded
				
				List<Entry<String, UpdateGuyRole>> expectedRoles = releaseFamilyEntry.getAllRoleInfos();
				for(Entry<String, UpdateGuyRole> roleEntry: expectedRoles)
				{
					Path expectedFile = releaseFamilyEntry.getRoleInfo(roleEntry.getKey()).getFilePath(); 
					Path actualFile = ServerGlobals.buildDownloadTargetPath(repoId, releaseFamilyEntry, roleEntry);
					FileAssert.assertBinaryEquals(expectedFile.toFile(), actualFile.toFile());
				}
			}
		}
	}
	
}
