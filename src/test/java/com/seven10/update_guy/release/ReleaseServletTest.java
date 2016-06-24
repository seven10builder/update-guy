/**
 * 
 */
package com.seven10.update_guy.release;

import static com.seven10.update_guy.RepoInfoHelpers.build_repo_info_file_by_testname;
import static com.seven10.update_guy.RepoInfoHelpers.copy_valid_repos_to_test;
import static com.seven10.update_guy.RepoInfoHelpers.load_repos_from_file;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

/**
 * @author kmm
 *
 */
public class ReleaseServletTest extends JerseyTest
{
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
	 */
	@Test
	public void testReleaseServlet_valid() throws RepositoryException
	{
		String versionId = "version";
		String repoId = "repo";
		ReleaseServlet releaseServlet = new ReleaseServlet(repoId, versionId);
		assertNotNull(releaseServlet);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#ReleaseServlet(com.seven10.update_guy.release.CacheManager)}.
	 * @throws RepositoryException 
	 */
	@Test
	public void testReleaseServlet_repoId_notFound() throws RepositoryException
	{
		String versionId = "version";
		String repoId = "repo";
		ReleaseServlet releaseServlet = new ReleaseServlet(repoId, versionId);
		assertNotNull(releaseServlet);
		fail("Not yet implemented");
	}
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#ReleaseServlet(com.seven10.update_guy.release.CacheManager)}.
	 * @throws RepositoryException 
	 */
	@Test
	public void testReleaseServlet_versionId_notFound() throws RepositoryException
	{
		String versionId = "version";
		String repoId = "repo";
		ReleaseServlet releaseServlet = new ReleaseServlet(repoId, versionId);
		assertNotNull(releaseServlet);
		fail("Not yet implemented");
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
		Path localManifestPath = parentPath.resolve(repoId).resolve("manifests");
		List<Manifest> manifestList = ManifestHelpers.create_manifest_list(testName, 5);
		ManifestHelpers.write_manifest_list_to_folder(localManifestPath, manifestList);
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
	 */
	@Test
	public void testGetFingerprint()
	{
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#getFile(java.lang.String)}.
	 */
	@Test
	public void testGetFile()
	{
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.release.ReleaseServlet#doUpdateCache(javax.ws.rs.container.AsyncResponse)}.
	 */
	@Test
	public void testDoUpdateCache()
	{
		fail("Not yet implemented");
	}
	
}
