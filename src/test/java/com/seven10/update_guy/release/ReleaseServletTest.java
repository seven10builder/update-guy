/**
 * 
 */
package com.seven10.update_guy.release;

import static com.seven10.update_guy.RepoInfoHelpers.build_repo_info_file_by_testname;
import static com.seven10.update_guy.RepoInfoHelpers.copy_valid_repos_to_test;
import static com.seven10.update_guy.RepoInfoHelpers.load_repos_from_file;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.FileFingerPrint;
import com.seven10.update_guy.Globals;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.ManifestHelpers;
import com.seven10.update_guy.RepoInfoHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.manifest.ManifestServlet;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;

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
	 * @param repoId
	 * @param parentPath
	 * @return
	 * @throws RepositoryException
	 * @throws IOException
	 */
	private List<Manifest> populateManifestList(String testName, String repoId, Path parentPath)
			throws RepositoryException, IOException
	{
		Path localManifestPath = parentPath.resolve(repoId).resolve("manifests");
		List<Manifest> manifestList = ManifestHelpers.create_manifest_list(testName, 5);
		ManifestHelpers.write_manifest_list_to_folder(localManifestPath, manifestList);
		return manifestList;
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
	 */
	@Test
	public void testReleaseServlet_valid() throws RepositoryException, IOException
	{
		String testName = "ctor-v";
		Path repoFile = build_repo_info_file_by_testname(testName , folder);
		copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();
		List<Manifest> manifestList = populateManifestList(testName, repoId, parentPath);
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
		
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
	 */
	@Test(expected=RepositoryException.class)
	public void testReleaseServlet_repoId_notFound() throws RepositoryException, IOException
	{
		String testName = "ctor-rnf";
		Path repoFile = build_repo_info_file_by_testname(testName , folder);
		copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc an invalid repoId
		String repoId = repoInfo.getShaHash() + "breakrepoid";
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();
		List<Manifest> manifestList = populateManifestList(testName, repoId, parentPath);
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
		
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
		String testName = "ctor-rfnf";
		Path repoFile = build_repo_info_file_by_testname(testName , folder);
		copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc an invalid repoId
		String repoId = repoInfo.getShaHash();
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();

		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
		
		String releaseFamily = "someunknownRelease";
		new ReleaseServlet(repoId, releaseFamily);
	}
	
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#getRoles()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetRoles() throws IOException, RepositoryException
	{
		String testName = "getroles_v";
		Path repoFile = build_repo_info_file_by_testname(testName , folder);
		copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();
		List<Manifest> manifestList = populateManifestList(testName, repoId, parentPath);
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
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
	 */
	@Test
	public void testGetFingerprint_valid() throws IOException, RepositoryException
	{
		String testName = "getFingerprint_v";
		Path repoFile = build_repo_info_file_by_testname(testName , folder);
		copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();
		List<Manifest> manifestList = populateManifestList(testName, repoId, parentPath);
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
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
	 */
	@Test
	public void testGetFile_valid() throws IOException, RepositoryException
	{
		String testName = "getFiles_v";
		Path repoFile = build_repo_info_file_by_testname(testName , folder);
		copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();
		List<Manifest> manifestList = populateManifestList(testName, repoId, parentPath);
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
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
	 */
	@Test
	public void testDoUpdateCache() throws IOException, RepositoryException
	{
		String testName = "update-cache";
		Path repoFile = build_repo_info_file_by_testname(testName , folder);
		copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();
		List<Manifest> manifestList = populateManifestList(testName, repoId, parentPath);
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
		for(Manifest manifest: manifestList)
		{
			String releaseFamily = manifest.getReleaseFamily();
			for(ManifestEntry manifestEntry: manifest.getVersionEntries())
			{
				String version = manifestEntry.getVersion();
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
