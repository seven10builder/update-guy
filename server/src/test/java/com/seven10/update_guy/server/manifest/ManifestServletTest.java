/**
 * 
 */
package com.seven10.update_guy.server.manifest;

import static com.seven10.update_guy.common.ManifestHelpers.*;
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
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.server.manifest.ManifestServlet;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;

/**
 * @author kmm
 *		
 */
public class ManifestServletTest extends JerseyTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Override
	protected Application configure()
	{
		ResourceConfig resourceConfig = new ResourceConfig(ManifestServlet.class);
		return resourceConfig;
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#ManifestServlet(com.seven10.update_guy.manifest.ManifestMgr)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testManifestServlet_valid() throws IOException
	{
		String repoId = "repoId";
		ManifestServlet servlet = new ManifestServlet(repoId);
		assertNotNull(servlet);
	}
		
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getManifest(java.lang.String)}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetManifest_show_specific_valid() throws IOException, RepositoryException, UpdateGuyException
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
		List<Manifest> manifestList = load_manifest_list_from_path(get_manifests_path());
		Path destManifestPath = rootPath.resolve(repoId).resolve("manifests");
		destManifestPath.toFile().mkdirs();
		for(Manifest expected: manifestList)
		{
			Response resp = target("/manifest/"+ repoId + "/show/" + expected.getReleaseFamily())
						.request()
						.get();
			assertEquals(Status.OK.getStatusCode(), resp.getStatus());
			String json = resp.readEntity(String.class);
			Gson gson = GsonFactory.getGson();
			Manifest actual = gson.fromJson(json, Manifest.class);
			assertEquals(expected, actual);
		}
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getManifest(java.lang.String)}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetManifest_specific_family_not_found() throws IOException, RepositoryException
	{
		String testName = "getManifest-sfnf";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		Response resp = target("/manifest/"+ repoId + "/show" + "/this-doesnt-exist").request().get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getActiveRelease()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 */
	@Test
	public void testGetManifests_show_all_valid() throws IOException, RepositoryException, UpdateGuyException
	{
		String testName = "showAll-v";
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		String repoId = repoInfo.getShaHash();
		
		List<Manifest> expectedManifestList = load_manifest_list_from_path(get_manifests_path());

		// do request
		Response resp = target("/manifest/"+ repoId + "/show" + "/").request().get();
		
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String json = resp.readEntity(String.class);
		Gson gson = GsonFactory.getGson();
		
		Type collectionType = new TypeToken<List<Manifest>>()
		{
		}.getType();
		List<Manifest> actual = gson.fromJson(json, collectionType);
		assertNotNull(actual);
		assertNotEquals(0, actual.size());
		assertTrue(expectedManifestList.containsAll(actual));
		assertTrue(actual.containsAll(expectedManifestList));
	}
	
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getActiveRelease()}.
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
		List<Manifest> manifestList = load_manifest_list_from_path(get_manifests_path());
		Path destManifestPath = rootPath.resolve(repoId).resolve("manifests");
		destManifestPath.toFile().mkdirs();

		String activeVersionId = "actVersTest";
		
		for (Manifest manifest : manifestList)
		{
			for (ManifestEntry entry : manifest.getVersionEntries())
			{
				// set the active version
				Response resp = target("/manifest/" + repoId + "/active-release/" + manifest.getReleaseFamily() + "/" + activeVersionId)
						.queryParam("newVersion", entry.getVersion())
						.request().get();
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				String json = resp.readEntity(String.class);
				ManifestEntry actualEntry1 = GsonFactory.getGson().fromJson(json, ManifestEntry.class);
				
				// get the active version
				resp = target("/manifest/" + repoId + "/active-release/"  + manifest.getReleaseFamily() + "/" + activeVersionId)
						.queryParam("releaseFamily", manifest.getReleaseFamily())
						.request().get();
				assertEquals(Status.OK.getStatusCode(), resp.getStatus());
				json = resp.readEntity(String.class);
				ManifestEntry actualEntry2 = GsonFactory.getGson().fromJson(json, ManifestEntry.class);
				assertEquals(actualEntry1, actualEntry2);
			}
			
		}
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getActiveRelease()}.
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

		List<Manifest> manifestList = load_manifest_list_from_path(get_manifests_path());
	
		String activeVersionId = "not-found";
		
		Manifest manifest = manifestList.stream().findFirst().get();

		Response resp = target("/manifest/" + repoId + "/active-release/"  + manifest.getReleaseFamily() + "/"+ activeVersionId)
						.request().get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getActiveRelease()}.
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
		Response resp = target("/manifest/" + repoId + "/active-release/"  + releaseFamily + "/" + activeVersionId)
						.request().get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
}
