/**
 * 
 */
package com.seven10.update_guy.repository;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static com.seven10.update_guy.RepoInfoHelpers.*;

import com.seven10.update_guy.RepoInfoHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.repository.RepositoryInfoMgr;
import com.seven10.update_guy.test_helpers.Factories;
import com.seven10.update_guy.test_helpers.TestHelpers;

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
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#RepositoryInfoMgr()}
	 * .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws RepositoryException 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRepositoryInfoMgr_null() throws FileNotFoundException, IOException, RepositoryException
	{
		Path storeFile = null;
		new RepositoryInfoMgr(storeFile);
	}

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
		File storeFile = folder.newFile("fileNotFound.json");
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storeFile.toPath());
		// there should be no repos if the file doesn't exist
		assertTrue(mgr.getRepoMap().keySet().isEmpty());
		// the file should be created if it doesn't exist
		assertTrue(Files.exists(storeFile.toPath()));
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

		
		Path storeFile = folder.newFile("add_valid_no_entries.json").toPath();
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storeFile);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storeFile);

		RepositoryInfo actual = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		mgr.addRepository(actual);
		String modifiedHash = TestHelpers.hashFile(storeFile);

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
		String releaseFamily = "addRepo-venc";
		
		Path storePath = build_repo_info_file_by_testname(releaseFamily, folder);
		copy_valid_repos_to_test(storePath);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storePath);
		int originalSize =  mgr.getRepoMap().keySet().size();

		// get the id for the repo
		RepositoryInfo actualRepoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		actualRepoInfo.description = "this is completely different";
		actualRepoInfo.port = 31337;
		actualRepoInfo.user = "drApocalypse";
		
		
		RepositoryInfo actual = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		actual.description = "this is completely different";
		actual.port = 31337;
		actual.user = "drApocalypse";
		mgr.addRepository(actual);
		String modifiedHash = TestHelpers.hashFile(storePath);

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
		String releaseFamily = "addRepo-vec";
		
		Path storePath = build_repo_info_file_by_testname(releaseFamily, folder);
		copy_valid_repos_to_test(storePath);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storePath);
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
		String modifiedHash = TestHelpers.hashFile(storePath);

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
		String releaseFamily = "addRepo-ne";
		Path storePath = build_repo_info_file_by_testname(releaseFamily, folder);
		copy_valid_repos_to_test(storePath);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storePath);
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
		String modifiedHash = TestHelpers.hashFile(storePath);

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
		String releaseFamily = "delRepo-ve";
		
		Path storePath = build_repo_info_file_by_testname(releaseFamily, folder);
		copy_valid_repos_to_test(storePath);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storePath);
		int originalSize =  mgr.getRepoMap().keySet().size();

		// get the id for the repo
		int actual = mgr.getRepoMap().values().stream().findAny().get().hashCode();
		mgr.deleteRepository(actual);
		String modifiedHash = TestHelpers.hashFile(storePath);

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
		String releaseFamily = "delRepo-vne";
		
		
		Path storePath = build_repo_info_file_by_testname(releaseFamily, folder);
		copy_valid_repos_to_test(storePath);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storePath);
		int originalSize =  mgr.getRepoMap().keySet().size();

		// get the id for the repo
		RepositoryInfo actualRepoInfo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		actualRepoInfo.description = "this is completely different";
		actualRepoInfo.port = 31337;
		actualRepoInfo.user = "drApocalypse";
		
		int actual = actualRepoInfo.hashCode();

		try
		{
			mgr.deleteRepository(actual);
			fail("Expected RepositoryException was not thrown");
		}
		catch (RepositoryException e)
		{
			assertTrue(e.getMessage().contains(expectedNotExistMsg));
		}
		String modifiedHash = TestHelpers.hashFile(storePath);

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
		String releaseFamily = "writeReadRepos";
		Path storePath = build_repo_info_file_by_testname(releaseFamily, folder);
		
		List<RepositoryInfo> expectedRepos = load_repos_from_file(get_valid_repos_path());
		
		RepositoryInfoMgr.writeRepos(storePath, expectedRepos);

		// file should be created
		assertTrue(Files.exists(storePath));
		// store hash for comparison for later
		String hashAfterWrite = TestHelpers.hashFile(storePath);

		// read the file back, testing loadRepos
		List<RepositoryInfo> actualRepos = RepositoryInfoMgr.loadRepos(storePath);
		String hashAfterLoad = TestHelpers.hashFile(storePath);

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
		List<RepositoryInfo> expectedRepos = Factories.load_valid_repos_list();
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
		String hashAfterWrite = TestHelpers.hashFile(storePath);

		// read the file back, testing loadRepos
		List<RepositoryInfo> actualRepos = RepositoryInfoMgr.loadRepos(storePath);
		String hashAfterLoad = TestHelpers.hashFile(storePath);

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
		File storeFile = folder.newFile("testGetRepoMap.json");
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storeFile.toPath());
		Map<Integer, RepositoryInfo> repoMap = mgr.getRepoMap();
		assertNotNull(repoMap);
	}

}
