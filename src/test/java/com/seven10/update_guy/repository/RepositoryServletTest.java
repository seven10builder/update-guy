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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.Globals;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;
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
	public void testRepositoryServlet_valid() throws RepositoryException, IOException
	{
		String testName = "repoServlet-fev";
		Path fileName = prepareRepoFile(testName);
		
		RepositoryServlet servlet = new RepositoryServlet();
		assertNotNull(servlet);
		assertEquals(fileName.toString(), RepositoryServlet.getRepoInfoPath().toString());
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#RepositoryServlet()}
	 * .
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test
	public void testRepositoryServlet_defaults() throws RepositoryException, IOException
	{
		
		System.clearProperty(Globals.SETTING_LOCAL_PATH);
		System.clearProperty(Globals.SETTING_REPO_FILENAME);
		
		RepositoryServlet servlet = new RepositoryServlet();
		assertNotNull(servlet);
		String expectedRepoInfoPath = Paths.get(Globals.DEFAULT_LOCAL_PATH, Globals.DEFAULT_REPO_FILENAME).toString();
		assertEquals(expectedRepoInfoPath, RepositoryServlet.getRepoInfoPath().toString());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#showAllRepos()}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testShowAllRepos_valid() throws IOException
	{
		String testName = "showAllRepos-valid";
		Path fileName = prepareRepoFile(testName);
		
		
		List<RepositoryInfo> expectedInfos = load_repos_from_file(fileName);
		
		Response resp = target("/repository/show").request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String json = resp.readEntity(String.class);
		Gson gson = GsonFactory.getGson();
		
		Type collectionType = new TypeToken<Map<String, RepositoryInfo>>(){}.getType();
		Map<String, RepositoryInfo> actualInfos = gson.fromJson(json, collectionType);
		
		Collection<RepositoryInfo> actualEntrySet = actualInfos.values();
		
		assertTrue(expectedInfos.containsAll(actualEntrySet));
		assertTrue(actualEntrySet.containsAll(expectedInfos));
	}

	private Path prepareRepoFile(String testName) throws IOException
	{
		//prepare repoFile
		Path fileName = build_repo_info_file_by_testname(testName, folder);
		copy_valid_repos_to_test(fileName);
		Path rootPath = fileName.getParent();
		System.setProperty(Globals.SETTING_LOCAL_PATH, rootPath.toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, fileName.getFileName().toString());
		return fileName;
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#showRepo(Integer)}
	 * .
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testShowRepo_valid() throws IOException, RepositoryException
	{
		String testName = "showRepo-valid";
		Path fileName = prepareRepoFile(testName);
		
		RepositoryInfo expectedRepoInfo = load_repos_from_file(fileName).stream().findFirst().get();
		
		Response resp = target("/repository/show/" + expectedRepoInfo.getShaHash()).request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String json = resp.readEntity(String.class);
		Gson gson = GsonFactory.getGson();
		
		RepositoryInfo actual = gson.fromJson(json, RepositoryInfo.class);
		assertNotNull(actual);
		assertEquals(expectedRepoInfo, actual);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#getManifest(java.lang.String)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testShowRepo_empty() throws IOException
	{
		String testName = "testShowRepo_empty";
		prepareRepoFile(testName);
		
		String bogusId = "";
		Response resp = target("/repository/show/" + bogusId).request().get();
		// the response should be identical to the regular "show" command
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String json = resp.readEntity(String.class);
		Gson gson = GsonFactory.getGson();
		
		RepositoryInfo actual = gson.fromJson(json, RepositoryInfo.class);
		assertNotNull(actual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#getManifest(java.lang.String)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testShowRepos_not_found() throws IOException
	{
		String testName = "testShowRepo_nf";
		
		prepareRepoFile(testName);
		
		String notFoundId = "0";
		Response resp = target("/repository/show/" + notFoundId).request().get();
		assertEquals(404, resp.getStatus());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#createRepository(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testCreateRepository_valid() throws IOException
	{
		String testName = "createRepos";
		
		Path fileName = prepareRepoFile(testName);
		
		// create new repoInfo object
		RepositoryInfo repoInfo = new RepositoryInfo();
		repoInfo.port = 9999;
		repoInfo.user = "create-user";
		repoInfo.repoAddress = "99.99.99.0";
		
		List<RepositoryInfo> expected = load_repos_from_file(fileName);
		expected.add(repoInfo);
		

		Entity<RepositoryInfo> entity = Entity.json(repoInfo);
		Response resp = target("/repository/create").request().post(entity);
		assertEquals(200, resp.getStatus());
		
		List<RepositoryInfo> actual = load_repos_from_file(fileName);
		
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
	public void testCreateRepository_empty_object() throws IOException
	{
		String testName = "createRepos_empty";
		
		prepareRepoFile(testName);
		
		RepositoryInfo nullValue = null;
		Entity<RepositoryInfo> entity = Entity.json(nullValue);
		Response resp = target("/repository/create").request().post(entity);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#createRepository(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testCreateRepository_collision() throws IOException, RepositoryException
	{
		String testName = "createRepos_col";
		
		Path fileName = prepareRepoFile(testName);
		
		RepositoryInfo expectedRepoInfo = load_repos_from_file(fileName).stream().findFirst().get();
		Entity<RepositoryInfo> entity = Entity.json(expectedRepoInfo);
		Response resp = target("/repository/create").request().post(entity);
		assertEquals(Status.NOT_MODIFIED.getStatusCode(), resp.getStatus());
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#deleteRepository(int)}
	 * .
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testDeleteRepository_valid() throws IOException, RepositoryException
	{
		String testName = "delete-valid";
		Path fileName = prepareRepoFile(testName);
		
		List<RepositoryInfo> allInfos = load_repos_from_file(fileName);
		RepositoryInfo targetRepo = allInfos.get(0);
		List<RepositoryInfo> expectedInfos = allInfos.subList(1, allInfos.size());
		
		Response resp = target("/repository/delete/" + targetRepo.getShaHash()).request().delete();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		
		List<RepositoryInfo> actualInfos = load_repos_from_file(fileName);
		assertTrue(actualInfos.containsAll(expectedInfos));
		assertTrue(expectedInfos.containsAll(actualInfos));
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryServlet#deleteRepository(int)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testDeleteRepository_not_found() throws IOException
	{
		String testName = "delete-nf";
		Path fileName = prepareRepoFile(testName);
		
		// get list of extant repos
		List<RepositoryInfo> expectedInfos = load_repos_from_file(fileName);
		
		int bogusId = 0;
		Response resp = target("/repository/delete/" + bogusId).request().delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		
		//  there should be no change in the files
		List<RepositoryInfo> actualInfos = load_repos_from_file(fileName);
		assertTrue(actualInfos.containsAll(expectedInfos));
		assertTrue(expectedInfos.containsAll(actualInfos));
	}
	
	@Test
	public void testGetRepoInfoById_valid() throws IOException, RepositoryException
	{
		String testName = "getRepoInfoById-v";
		
		Path repoFileName = prepareRepoFile(testName);
		
		// get list of extant repos
		List<RepositoryInfo> expectedInfos = load_repos_from_file(repoFileName);
		for(RepositoryInfo expectedInfo: expectedInfos)
		{
			
			String repoId = expectedInfo.getShaHash();
			RepositoryInfo actualInfo = RepositoryServlet.getRepoInfoById(repoId);
			
			assertEquals(expectedInfo, actualInfo);
		}
	}
}
