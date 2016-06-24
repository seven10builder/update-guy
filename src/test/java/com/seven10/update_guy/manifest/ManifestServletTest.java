/**
 * 
 */
package com.seven10.update_guy.manifest;

import static com.seven10.update_guy.ManifestHelpers.*;
import static com.seven10.update_guy.RepoInfoHelpers.*;
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
import org.junit.Before;
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
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;

/**
 * @author kmm
 *		
 */
public class ManifestServletTest extends JerseyTest
{
	
	private static final String manifestServletName = "manifest_servlet";
	
	private String setupRepoId(String testName) throws IOException, RepositoryException
	{
		Path destPath = build_repo_info_file_by_testname(testName, folder);
		copy_valid_repos_to_test(destPath);
		List<RepositoryInfo> repoInfoList = RepoInfoHelpers.load_repos_from_file(destPath);
		RepositoryInfo repoInfo = repoInfoList.stream().filter(ri->ri.repoType == RepositoryType.local).findFirst().get();
		return repoInfo.getShaHash();
	}
	
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Override
	protected Application configure()
	{
		ResourceConfig resourceConfig = new ResourceConfig(ManifestServlet.class);
		return resourceConfig;
	}
	
	@Before public void setUpManifests() throws IOException, RepositoryException
	{
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(manifestServletName, 5);
		// save list to files in folder
		Path rootPath = folder.newFolder(manifestServletName).toPath();
		write_manifest_list_to_folder(rootPath.resolve("manifests"), expected);
		
		// create globals variable
		System.setProperty(Globals.SETTING_LOCAL_PATH, rootPath.toString());
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
	 */
	@Test
	public void testGetManifest_show_specific_valid() throws IOException, RepositoryException
	{
		String testName = setupRepoId("showSpec-v");
		Path repoFile = RepoInfoHelpers.build_repo_info_file_by_testname(testName , folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = RepoInfoHelpers.load_repos_from_file(repoFile).stream()
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
		for(Manifest expected: manifestList)
		{
			Response resp = target("/manifest/"+ repoId + "/show/" + expected.releaseFamily)
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
		String repoId = setupRepoId("showSpec-nf");
		Response resp = target("/manifest/"+ repoId + "/show" + "/this-doesnt-exist").request().get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getManifests()}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testGetManifests_show_all_valid() throws IOException, RepositoryException
	{
		// copy valid repo.json file
		String testName = "showall-v";
		Path repoFile = RepoInfoHelpers.build_repo_info_file_by_testname(testName , folder);
		RepoInfoHelpers.copy_valid_repos_to_test(repoFile);
		RepositoryInfo repoInfo = RepoInfoHelpers.load_repos_from_file(repoFile).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		// calc the repoId
		String repoId = repoInfo.getShaHash();
		// copy some manifests to the manifest path
		Path parentPath = repoFile.getParent();
		Path localManifestPath = parentPath.resolve(repoId).resolve("manifests");
		List<Manifest> expectedManifestList = ManifestHelpers.create_manifest_list(testName, 5);
		ManifestHelpers.write_manifest_list_to_folder(localManifestPath, expectedManifestList);
		// set attribute so servlet picks it up
		System.setProperty(Globals.SETTING_LOCAL_PATH, parentPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, repoFile.getFileName().toString());
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

}
