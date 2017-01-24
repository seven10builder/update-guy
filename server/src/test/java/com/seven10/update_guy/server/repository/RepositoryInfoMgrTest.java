/**
 * 
 */
package com.seven10.update_guy.server.repository;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static com.seven10.update_guy.server.helpers.RepoInfoHelpers.*;

import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.helpers.RepoInfoHelpers;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfoMgr;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;

/**
 * @author dra
 *
 */
public class RepositoryInfoMgrTest
{

	private static final String expectedCollisionMsg = "repoMap already contains hash";
	private static final CharSequence expectedNotExistMsg = " does not exist";
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#testRepositoryInfoMgr()}
	 * .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws RepositoryException 
	 */
	@Test
	public void testRepositoryInfoMgr_fileNotFound() throws FileNotFoundException, IOException, RepositoryException
	{
		String fileName = "fileNotfound.json";
		Path repoInfoFile = folder.newFolder("filenotfound").toPath().resolve(fileName);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(repoInfoFile);
		// there should be no repos if the file doesn't exist
		assertTrue(mgr.getRepoMap().keySet().isEmpty());
		// the file should be created if it doesn't exist
		assertTrue(Files.exists(repoInfoFile));
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#addRepository(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testAddRepository_valid_no_entries() throws Exception
	{
		String fileName = "add_valid_no_entries.json";
		Path repoInfoFile = folder.newFolder("noEntries").toPath().resolve(fileName);
		
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(repoInfoFile);
		// get sha of config to compare later
		String originalHash = RepoInfoHelpers.hashFile(repoInfoFile);

		RepositoryInfo actual = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		mgr.addRepository(actual);
		String modifiedHash = RepoInfoHelpers.hashFile(repoInfoFile);

		// there should be only one item
		assertEquals(mgr.getRepoMap().keySet().size(), 1);
		// the file should have changed (been saved to)
		assertNotEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#addRepository(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testAddRepository_valid_entries_no_colision()
			throws Exception
	{
		String testName = "addRepo-venc";
		Path repoInfoFile = folder.newFolder(testName).toPath().resolve(testName + ".json");
		FileUtils.copyFile(get_valid_repos_path().toFile(), repoInfoFile.toFile());
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(repoInfoFile);

		// get sha of config to compare later
		String originalHash = RepoInfoHelpers.hashFile(repoInfoFile);
		int originalSize =  mgr.getRepoMap().keySet().size();

		// get the id for the repo
	
		RepositoryInfo actual =  new RepositoryInfo();
		actual.description = "this is completely different";
		actual.port = 31337;
		actual.user = "drApocalypse";
		actual.repoType = RepositoryType.ftp;
		mgr.addRepository(actual);
		String modifiedHash = RepoInfoHelpers.hashFile(repoInfoFile);

		assertEquals(originalSize+1, mgr.getRepoMap().keySet().size());
		// the file should have changed (been saved to)
		assertNotEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#addRepository(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testAddRepository_valid_entries_colision() throws Exception
	{
		String testName = "addRepo-vec";
		
		Path repoInfoFile = folder.newFolder(testName).toPath().resolve(testName + ".json");
		FileUtils.copyFile(get_valid_repos_path().toFile(), repoInfoFile.toFile());
		
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(repoInfoFile);

		// get sha of config to compare later
		String originalHash = RepoInfoHelpers.hashFile(repoInfoFile);
		int originalSize =  mgr.getRepoMap().keySet().size();

		// get the id for the repo
		RepositoryInfo actual = mgr.getRepoMap().values().stream().findAny().get();
	
		try
		{
			mgr.addRepository(actual); // this SHOULD throw an exception
			fail("Expected RepositoryException was not thrown");
		}
		catch (RepositoryException e)
		{
			assertTrue(e.getMessage().contains(expectedCollisionMsg));
		}
		String modifiedHash = RepoInfoHelpers.hashFile(repoInfoFile);

		// there should still be only one item
		assertEquals(originalSize, mgr.getRepoMap().keySet().size());
		// the file should NOT have changed (been saved to)
		assertEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#addRepository(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test
	public void testAddRepository_nullEntries() throws NoSuchAlgorithmException, IOException, RepositoryException
	{
		String testName = "addRepo-ne";
		Path repoInfoFile = folder.newFolder(testName).toPath().resolve(testName + ".json");
		FileUtils.copyFile(get_valid_repos_path().toFile(), repoInfoFile.toFile());
		
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(repoInfoFile);

		// get sha of config to compare later
		String originalHash = RepoInfoHelpers.hashFile(repoInfoFile);
		int originalSize =  mgr.getRepoMap().keySet().size();

		RepositoryInfo actual = null;
		try
		{
			mgr.addRepository(actual); // this SHOULD throw an exception
			fail("Expected RepositoryException was not thrown");
		}
		catch (IllegalArgumentException e)
		{
		}
		String modifiedHash = RepoInfoHelpers.hashFile(repoInfoFile);

		// there should still be only one item
		assertEquals(mgr.getRepoMap().keySet().size(), originalSize);
		// the file should NOT have changed (been saved to)
		assertEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#deleteRepository(int)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testDeleteRepository_valid_exists()	throws Exception
	{
		String testName = "delRepo-ve";
		
		Path repoInfoFile = folder.newFolder(testName).toPath().resolve(testName + ".json");
		FileUtils.copyFile(get_valid_repos_path().toFile(), repoInfoFile.toFile());
		
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(repoInfoFile);

		// get sha of config to compare later
		String originalHash = RepoInfoHelpers.hashFile(repoInfoFile);
		int originalSize =  mgr.getRepoMap().keySet().size();

		// get the id for the repo
		String actual = mgr.getRepoMap().values().stream().findAny().get().getShaHash();
		mgr.deleteRepository(actual);
		String modifiedHash = RepoInfoHelpers.hashFile(repoInfoFile);

		// there should be zero items
		assertEquals(originalSize-1, mgr.getRepoMap().keySet().size());
		// the file should have changed (been saved to)
		assertNotEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#deleteRepository(int)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testDeleteRepository_valid_not_exists()
			throws Exception
	{
		String testName = "delRepo-vne";
		
		Path storePath = folder.newFolder(testName).toPath().resolve(testName + ".json");
		FileUtils.copyFile(get_valid_repos_path().toFile(), storePath.toFile());
		
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = RepoInfoHelpers.hashFile(storePath);
		int originalSize =  mgr.getRepoMap().keySet().size();

		// get the id for the repo
		RepositoryInfo actualRepoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		actualRepoInfo.description = "this is completely different";
		actualRepoInfo.port = 31337;
		actualRepoInfo.user = "drApocalypse";
		
		String actual = actualRepoInfo.getShaHash();

		try
		{
			mgr.deleteRepository(actual);
			fail("Expected RepositoryException was not thrown");
		}
		catch (RepositoryException e)
		{
			assertTrue(e.getMessage().contains(expectedNotExistMsg));
		}
		String modifiedHash = RepoInfoHelpers.hashFile(storePath);

		// nothing should have been deleted
		assertEquals(originalSize, mgr.getRepoMap().keySet().size());
		// the file should NOT have changed (been saved to)
		assertEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#writeRepos(java.util.Collection)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testWriteReadRepos() throws IOException, RepositoryException, NoSuchAlgorithmException
	{
		String testName = "writeReadRepos";
		Path storePath = folder.newFolder(testName).toPath().resolve(testName + ".json");
		
		List<RepositoryInfo> expectedRepos = load_repos_from_file(get_valid_repos_path());
		
		RepositoryInfoMgr.writeRepos(storePath, expectedRepos);

		// file should be created
		assertTrue(Files.exists(storePath));
		// store hash for comparison for later
		String hashAfterWrite = RepoInfoHelpers.hashFile(storePath);

		// read the file back, testing loadRepos
		List<RepositoryInfo> actualRepos = RepositoryInfoMgr.loadRepos(storePath);
		String hashAfterLoad = RepoInfoHelpers.hashFile(storePath);

		// loading should not change the file
		assertEquals(hashAfterWrite, hashAfterLoad);
		// make sure the list comes back exactly equal
		assertTrue(actualRepos.containsAll(expectedRepos));
		assertTrue(expectedRepos.containsAll(actualRepos));
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#writeRepos(java.util.Collection)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testWrite_nullPath() throws IOException, RepositoryException, NoSuchAlgorithmException
	{
		Path storePath = null;
		List<RepositoryInfo> expectedRepos = RepoInfoHelpers.load_valid_repos_list();
		RepositoryInfoMgr.writeRepos(storePath, expectedRepos);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#writeRepos(java.util.Collection)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testWrite_nullRepos() throws IOException, RepositoryException, NoSuchAlgorithmException
	{
		Path storePath = Paths.get("writeNullRepos");
		List<RepositoryInfo> expectedRepos = null;
		RepositoryInfoMgr.writeRepos(storePath, expectedRepos);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#writeRepos(java.util.Collection)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testWrite_emptyRepos() throws IOException, RepositoryException, NoSuchAlgorithmException
	{
		String fileName = "writeEmptyRepos.json";
		Path storePath = folder.newFile(fileName).toPath();

		List<RepositoryInfo> expectedRepos = new ArrayList<RepositoryInfo>();
		RepositoryInfoMgr.writeRepos(storePath, expectedRepos);

		// file should be created
		assertTrue(Files.exists(storePath));
		// store hash for comparison for later
		String hashAfterWrite = RepoInfoHelpers.hashFile(storePath);

		// read the file back, testing loadRepos
		List<RepositoryInfo> actualRepos = RepositoryInfoMgr.loadRepos(storePath);
		String hashAfterLoad = RepoInfoHelpers.hashFile(storePath);

		// loading should not change the file
		assertEquals(hashAfterWrite, hashAfterLoad);
		// make sure the list comes back exactly equal
		assertEquals(0, actualRepos.size());
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#writeRepos(java.util.Collection)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoad_nullPath() throws IOException, RepositoryException, NoSuchAlgorithmException
	{
		Path storePath = null;
		RepositoryInfoMgr.loadRepos(storePath);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#getRepoMap()}.
	 * 
	 * @throws IOException
	 * @throws RepositoryException 
	 */
	@Test
	public void testGetRepoMap() throws IOException, RepositoryException
	{
		String testName = "getRepoMap";
		Path repoInfoFile = folder.newFolder(testName).toPath().resolve(testName + ".json");
		FileUtils.copyFile(get_valid_repos_path().toFile(), repoInfoFile.toFile());
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(repoInfoFile);
		Map<String, RepositoryInfo> repoMap = mgr.getRepoMap();
		assertNotNull(repoMap);
	}

}
