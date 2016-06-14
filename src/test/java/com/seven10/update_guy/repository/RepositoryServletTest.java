/**
 * 
 */
package com.seven10.update_guy.repository;

import static org.junit.Assert.*;
import static com.seven10.update_guy.RepoInfoHelpers.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.GsonFactory;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

/**
 * @author kmm
 *		
 */
public class RepositoryServletTest extends JerseyTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Override
	protected Application configure()
	{
		return new ResourceConfig(RepositoryServlet.class);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#RepositoryServlet()}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testRepositoryServlet_file_exists_valid() throws RepositoryException, IOException
	{
		String testName = "repoServlet-fev";
		Path fileName = build_repo_info_file_by_testname(testName, folder);
		copy_valid_repos_to_test(fileName);
		System.setProperty("seven10.repo_path", fileName.toString());
		RepositoryServlet servlet = new RepositoryServlet();
		assertNotNull(servlet);
		
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#RepositoryServlet()}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testRepositoryServlet_valid_but_empty() throws RepositoryException, IOException
	{
		String testName = "repoServlet-vbe";
		Path fileName = folder.newFile(testName + ".json").toPath();
		System.setProperty("seven10.repo_path", fileName.toString());
		RepositoryServlet servlet = new RepositoryServlet();
		assertNotNull(servlet);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#RepositoryServlet()}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testRepositoryServlet_file_not_exist() throws RepositoryException, IOException
	{
		String testName = "repoServlet-vne";
		Path fileName = folder.newFolder(testName).toPath().resolve(testName + ".json");		
		assertFalse(fileName.toFile().exists());
		
		System.setProperty("seven10.repo_path", fileName.toString());
		RepositoryServlet servlet = new RepositoryServlet();
		assertNotNull(servlet);
		
		// the file should now exist
		assertTrue(fileName.toFile().exists());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#getManifest(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetManifest_valid()
	{
		Response resp = target("manifest").queryParam("releaseFamily", "relfam").request().get();
		assertEquals(200, resp.getStatus());
		String json = (String) resp.getEntity();
		Gson gson = GsonFactory.getGson();
		Manifest actual = gson.fromJson(json, Manifest.class);
		assertNotNull(actual);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#getManifest(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetManifest_empty()
	{
		Response resp = target("manifest").queryParam("releaseFamily", "").request().get();
		assertEquals(500, resp.getStatus());
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#getManifest(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetManifest_no_params()
	{
		Response resp = target("manifest").request().get();
		assertEquals(500, resp.getStatus());
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#getManifest(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetManifest_not_found()
	{
		Response resp = target("manifest").queryParam("releaseFamily", "this-dont-exist").request().get();
		assertEquals(404, resp.getStatus());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#showRepos()}.
	 * @throws IOException 
	 */
	@Test
	public void testShowRepos_valid() throws IOException
	{
		String testName = "showRepos";
		Path fileName = build_repo_info_file_by_testname(testName, folder);
		copy_valid_repos_to_test(fileName);
		List<RepositoryInfo> expected = load_repos_from_file(fileName);
		System.setProperty("seven10.repo_path", fileName.toString());
		
		Response resp = target("showRepos").request().get();
		assertEquals(200, resp.getStatus());
		
		String json = (String) resp.getEntity();
		Gson gson = GsonFactory.getGson();
		Type collectionType = new TypeToken<List<RepositoryInfo>>()
		{
		}.getType();
		List<RepositoryInfo> actual = gson.fromJson(json, collectionType);
		
		assertNotNull(actual);
		assertTrue(actual.containsAll(expected));
		assertTrue(expected.containsAll(actual));
	}
	
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#createRepository(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testCreateRepository() throws IOException
	{
		String testName = "createRepos";
		Path fileName = build_repo_info_file_by_testname(testName, folder);
		copy_valid_repos_to_test(fileName);
		List<RepositoryInfo> expected = load_repos_from_file(fileName);
		System.setProperty("seven10.repo_path", fileName.toString());
		
		Response resp = target("createRepository").request().get();
		assertEquals(200, resp.getStatus());
		
		String json = (String) resp.getEntity();
		Gson gson = GsonFactory.getGson();
		Type collectionType = new TypeToken<List<RepositoryInfo>>()
		{
		}.getType();
		List<RepositoryInfo> actual = gson.fromJson(json, collectionType);
		
		assertNotNull(actual);
		assertTrue(actual.containsAll(expected));
		assertTrue(expected.containsAll(actual));
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#deleteRepository(int)}
	 * .
	 */
	@Test
	public void testDeleteRepository()
	{
		fail("Not yet implemented");
	}
	
}
