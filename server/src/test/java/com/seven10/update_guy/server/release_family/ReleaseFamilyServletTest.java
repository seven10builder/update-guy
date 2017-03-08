/**
 * 
 */
package com.seven10.update_guy.server.release_family;

import static com.seven10.update_guy.common.ReleaseFamilyHelpers.*;
import static com.seven10.update_guy.server.helpers.RepoInfoHelpers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.server.helpers.RepoInfoHelpers;
import com.seven10.update_guy.server.release_family.ReleaseFamilyServlet;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;

/**
 * @author kmm
 *		
 */
public class ReleaseFamilyServletTest extends JerseyTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Override
	protected Application configure()
	{
		ResourceConfig resourceConfig = new ResourceConfig(ReleaseFamilyServlet.class);
		return resourceConfig;
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.ReleaseFamilyServlet.ReleaseFamilyServlet#ReleaseFamilyServlet(com.seven10.update_guy.release-family.ReleaseFamilyMgr)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testReleaseFamilyServlet_valid() throws IOException
	{
		String repoId = "repoId";
		ReleaseFamilyServlet servlet = new ReleaseFamilyServlet(repoId);
		assertNotNull(servlet);
	}
		
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.ReleaseFamilyServlet.ReleaseFamilyServlet#getReleaseFamily(java.lang.String)}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetReleaseFamily_show_specific_valid() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "showSpec_v";
		Path repoPath = RepoInfoHelpers.get_valid_repos_path();
		// calc the repoId
		String repoId = load_valid_repo_info(RepositoryType.local).getShaHash();
		Path rootPath = folder.newFolder(testName).toPath();
		String fileName = testName + ".json";
		FileUtils.copyFile(repoPath.toFile(), rootPath.resolve(fileName).toFile());
		
		// set attribute so servlet picks it up
		System.setProperty(ServerGlobals.SETTING_LOCAL_PATH, rootPath.toString());
		System.setProperty(ServerGlobals.SETTING_REPO_FILENAME, fileName);
		List<ReleaseFamily> releaseFamilyList = load_releaseFamily_list_from_path(get_release_family_files_path());
		Path destReleaseFamilyPath = rootPath.resolve(repoId).resolve("releaseFamilies");
		destReleaseFamilyPath.toFile().mkdirs();
		for(ReleaseFamily expected: releaseFamilyList)
		{
			Response resp = target("/release-family/"+ repoId + "/show/" + expected.getReleaseFamily())
						.request()
						.get();
			assertEquals(Status.OK.getStatusCode(), resp.getStatus());
			String json = resp.readEntity(String.class);
			Gson gson = GsonFactory.getGson();
			ReleaseFamily actual = gson.fromJson(json, ReleaseFamily.class);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.ReleaseFamilyServlet.ReleaseFamilyServlet#getReleaseFamily(java.lang.String)}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetReleaseFamily_specific_family_not_found() throws IOException, RepositoryException
	{
		String testName = "getReleaseFamily-sfnf";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Response resp = target("/release-family/"+ repoId + "/show" + "/this-doesnt-exist").request().get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.ReleaseFamilyServlet.ReleaseFamilyServlet#getActiveRelease()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetReleaseFamilys_show_all_valid() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "showAll-v";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		
		List<ReleaseFamily> expectedReleaseFamilyList = load_releaseFamily_list_from_path(get_release_family_files_path());

		// do request
		Response resp = target("/release-family/"+ repoId + "/show" + "/").request().get();
		
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String json = resp.readEntity(String.class);
		Gson gson = GsonFactory.getGson();
		
		Type collectionType = new TypeToken<List<ReleaseFamily>>()
		{
		}.getType();
		List<ReleaseFamily> actual = gson.fromJson(json, collectionType);
		assertNotNull(actual);
		assertNotEquals(0, actual.size());
		assertTrue(expectedReleaseFamilyList.containsAll(actual));
		assertTrue(actual.containsAll(expectedReleaseFamilyList));
	}
	
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.ReleaseFamilyServlet.ReleaseFamilyServlet#getActiveRelease()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetactive_release_valid() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "getact-rv";
		Path repoPath = RepoInfoHelpers.get_valid_repos_path();
		// calc the repoId
		String repoId = load_valid_repo_info(RepositoryType.local).getShaHash();
		Path rootPath = folder.newFolder(testName).toPath();
		String fileName = testName + ".json";
		FileUtils.copyFile(repoPath.toFile(), rootPath.resolve(fileName).toFile());
		
		// set attribute so servlet picks it up
		System.setProperty(ServerGlobals.SETTING_LOCAL_PATH, rootPath.toString());
		System.setProperty(ServerGlobals.SETTING_REPO_FILENAME, fileName);
		List<ReleaseFamily> releaseFamilyList = load_releaseFamily_list_from_path(get_release_family_files_path());
		Path destReleaseFamilyPath = rootPath.resolve(repoId).resolve("releaseFamilies");
		destReleaseFamilyPath.toFile().mkdirs();

		String activeVersionId = "actVersTest";
		
		for (ReleaseFamily releaseFamily : releaseFamilyList)
		{
			for (ReleaseFamilyEntry entry : releaseFamily.getVersionEntries())
			{
				// set the active version
				Response resp = target("/release-family/" + repoId + "/active-release/" + releaseFamily.getReleaseFamily() + "/" + activeVersionId)
						.queryParam("newVersion", entry.getVersion())
						.request().get();
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				String json = resp.readEntity(String.class);
				ReleaseFamilyEntry actualEntry1 = GsonFactory.getGson().fromJson(json, ReleaseFamilyEntry.class);
				
				// get the active version
				resp = target("/release-family/" + repoId + "/active-release/"  + releaseFamily.getReleaseFamily() + "/" + activeVersionId)
						.queryParam("releaseFamily", releaseFamily.getReleaseFamily())
						.request().get();
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				json = resp.readEntity(String.class);
				ReleaseFamilyEntry actualEntry2 = GsonFactory.getGson().fromJson(json, ReleaseFamilyEntry.class);
				assertEquals(actualEntry1, actualEntry2);
			}
			
		}
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.ReleaseFamilyServlet.ReleaseFamilyServlet#getActiveRelease()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetactive_release_versionId_notFound() throws IOException, RepositoryException, UpdateGuyException
	{
		// /active-release/{activeVersId}?releaseFamily=derp&newVersion=dapp
		String testName = "getActiveRel-vid-nf";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();

		List<ReleaseFamily> releaseFamilyList = load_releaseFamily_list_from_path(get_release_family_files_path());
	
		String activeVersionId = "not-found";
		
		ReleaseFamily releaseFamily = releaseFamilyList.stream().findFirst().get();

		Response resp = target("/release-family/" + repoId + "/active-release/"  + releaseFamily.getReleaseFamily() + "/"+ activeVersionId)
						.request().get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.ReleaseFamilyServlet.ReleaseFamilyServlet#getActiveRelease()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetactive_release_relfam_notFound() throws IOException, RepositoryException, UpdateGuyException
	{
		// /active-release/{activeVersId}?releaseFamily=derp&newVersion=dapp
		String testName = "getActiveRel-rf-nf";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		
		
		String activeVersionId = "not-found";
		
		String releaseFamily = "some-fake-release-fam";
		Response resp = target("/release-family/" + repoId + "/active-release/"  + releaseFamily + "/" + activeVersionId)
						.request().get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
}
