/**
 * 
 */
package com.seven10.update_guy.server.release;

import static com.seven10.update_guy.common.RepoInfoHelpers.*;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.common.FileFingerPrint;
import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.ManifestHelpers;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
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
	private void validateDownloadedFile(String testName, ManifestEntry entry, File downloadPath, String roleName,
			Response resp) throws IOException
	{
		File actualFile = getFileFromDownload(testName, resp, downloadPath);
		File expectedFile = entry.getPath(roleName).toFile();
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
		RepositoryInfo repoInfo = load_valid_repos_list().stream()
				.filter(repo-> repo.repoType == RepositoryType.local)
				.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, get_repos_path().toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, get_valid_repos_path().getFileName().toString());
		
		List<Manifest> manifestList = ManifestHelpers.load_manifest_list_from_path(repoInfo.getRemoteManifestPath());
		for(Manifest manifest: manifestList)
		{
			String releaseFamily = manifest.getReleaseFamily();
			ReleaseServlet releaseServlet = new ReleaseServlet(repoId, releaseFamily);
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
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.local);
		// calc an invalid repoId
		String repoId = repoInfo.getShaHash() + "breakrepoid";
		
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, get_repos_path().toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, get_valid_repos_path().getFileName().toString());
		
		List<Manifest> manifestList = ManifestHelpers.load_manifest_list_from_path(repoInfo.getRemoteManifestPath());
		
	    
		Manifest manifest = manifestList.get(0);
		String releaseFamily = manifest.getReleaseFamily();
		new ReleaseServlet(repoId, releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#ReleaseServlet(com.seven10.update_guy.release.CacheManager)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=RepositoryException.class)
	public void testReleaseServlet_rf_notFound() throws RepositoryException, IOException
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.local);
		// calc an invalid repoId
		String repoId = repoInfo.getShaHash();
		
		System.setProperty(Globals.SETTING_LOCAL_PATH, get_repos_path().toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, get_valid_repos_path().getFileName().toString());
		
		String releaseFamily = "someunknownRelease";
		new ReleaseServlet(repoId, releaseFamily);
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
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.local);
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		
		Path rootPath = folder.newFolder(testName).toPath();
		String fileName = get_valid_repos_path().getFileName().toString();
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, rootPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, fileName);
		
		FileUtils.copyFile(get_valid_repos_path().toFile(), rootPath.resolve(fileName).toFile());
		
		List<Manifest> manifestList = ManifestHelpers.load_manifest_list_from_path(repoInfo.getRemoteManifestPath());
		for(Manifest manifest: manifestList)
		{
			String releaseFamily = manifest.getReleaseFamily();
			for(ManifestEntry entry: manifest.getVersionEntries())
			{
				String version = entry.getVersion();
				List<String> expectedRoles = entry.getRoles();
				// do request
				String path = "/release/"+ repoId + "/" + releaseFamily + "/roles";
				Response resp = target(path).queryParam("version", version).request().get();
				
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				String json = resp.readEntity(String.class);
				Gson gson = GsonFactory.getGson();
				
				Type collectionType = new TypeToken<List<String>>()
				{
				}.getType();
				List<Manifest> actualRoles = gson.fromJson(json, collectionType);
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
		RepositoryInfo repoInfo = load_valid_repos_list().stream()
				.filter(repo-> repo.repoType == RepositoryType.local)
				.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, get_repos_path().toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, get_valid_repos_path().getFileName().toString());
		
		List<Manifest> manifestList = ManifestHelpers.load_manifest_list_from_path(repoInfo.getRemoteManifestPath());
		for(Manifest manifest: manifestList)
		{
			String releaseFamily = manifest.getReleaseFamily();
			for(ManifestEntry entry: manifest.getVersionEntries())
			{
				String version = entry.getVersion();
				List<String> expectedRoles = entry.getRoles();
				// do request
				for(String roleName: expectedRoles)
				{
					String expectedFingerPrint = FileFingerPrint.create(entry.getPath(roleName).toFile());
					String path = "/release/"+ repoId + "/" + releaseFamily + "/fingerprint/" + roleName;
					Response resp = target(path).queryParam("version", version).request().get();
					assertEquals(Status.OK.getStatusCode(), resp.getStatus());
					String actualFingerPrint = resp.readEntity(String.class);
					
					assertEquals(expectedFingerPrint, actualFingerPrint);
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
		RepositoryInfo repoInfo = load_valid_repos_list().stream()
				.filter(repo-> repo.repoType == RepositoryType.local)
				.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, get_repos_path().toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, get_valid_repos_path().getFileName().toString());
		
		List<Manifest> manifestList = ManifestHelpers.load_manifest_list_from_path(repoInfo.getRemoteManifestPath());
		for(Manifest manifest: manifestList)
		{
			String releaseFamily = manifest.getReleaseFamily();
			for(ManifestEntry entry: manifest.getVersionEntries())
			{
				String version = entry.getVersion();
				List<String> expectedRoles = entry.getRoles();
				// do request
				File downloadPath = folder.newFolder(testName+"-"+releaseFamily + "-" + version);
				for(String roleName: expectedRoles)
				{
					String path = "/release/"+ repoId + "/" + releaseFamily + "/download/" + roleName;
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
		RepositoryInfo repoInfo = load_valid_repos_list().stream()
				.filter(repo-> repo.repoType == RepositoryType.local)
				.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, get_repos_path().toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, get_valid_repos_path().getFileName().toString());
		
		List<Manifest> manifestList = ManifestHelpers.load_manifest_list_from_path(repoInfo.getRemoteManifestPath());
		for(Manifest manifest: manifestList)
		{
			for(ManifestEntry manifestEntry: manifest.getVersionEntries())
			{
				String version = manifestEntry.getVersion();
				String releaseFamily = manifestEntry.getReleaseFamily();
				assertNotEquals("unknown", releaseFamily);
				String path = "/release/"+ repoId + "/" + releaseFamily + "/update-cache";
				Response resp = target(path).queryParam("version", version).request().get();
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				
				// now test that each file was downloaded
				
				List<Entry<String, Path>> expectedRoles = manifestEntry.getAllRolePaths();
				for(Entry<String, Path> roleEntry: expectedRoles)
				{
					Path expectedFile = manifestEntry.getPath(roleEntry.getKey()); 
					Path actualFile = Globals.buildDownloadTargetPath(repoId, manifestEntry, roleEntry);
					FileAssert.assertBinaryEquals(expectedFile.toFile(), actualFile.toFile());
				}
			}
		}
	}
	
}
