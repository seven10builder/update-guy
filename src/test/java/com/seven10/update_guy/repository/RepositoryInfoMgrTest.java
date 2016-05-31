/**
 * 
 */
package com.seven10.update_guy.repository;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.TestHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfoMgr;

/**
 * @author dra
 *
 */
public class RepositoryInfoMgrTest
{

	private final static int maxNumberOfRepos = 5;
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
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRepositoryInfoMgr_null() throws FileNotFoundException, IOException
	{
		String storeFile = null;
		new RepositoryInfoMgr(storeFile);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#RepositoryInfoMgr()}
	 * .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRepositoryInfoMgr_empty() throws FileNotFoundException, IOException
	{
		String storeFile = "";
		new RepositoryInfoMgr(storeFile);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#testRepositoryInfoMgr()}
	 * .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public void testRepositoryInfoMgr_fileNotFound() throws FileNotFoundException, IOException
	{
		File storeFile = folder.newFile("fileNotFound.dat");
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storeFile.getAbsolutePath());
		// there should be no repos if the file doesn't exist
		assertTrue(mgr.getRepoMap().keySet().isEmpty());
		// the file should be created if it doesn't exist
		assertTrue(Files.exists(storeFile.toPath()));
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#testRepositoryInfoMgr()}
	 * .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public void testRepositoryInfoMgr_ManyRepoList() throws FileNotFoundException, IOException
	{
		for (int i = 0; i < maxNumberOfRepos; i++)
		{
			String fileName = String.format("ManyRepoList%d.dat", i);
			String storePath = folder.newFile(fileName).getAbsolutePath();
			TestHelpers.createMockedFile(storePath, i);
			RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);
			assertEquals(mgr.getRepoMap().keySet().size(), i);
		}
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
	public void testAddRepository_valid_no_entries() throws NoSuchAlgorithmException, IOException, RepositoryException
	{

		File storeFile = folder.newFile("add_valid_no_entries.dat");
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storeFile.getAbsolutePath());

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storeFile);

		RepositoryInfo actual = TestHelpers.createMockedRepoInfo("valid_no_entries");
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
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test
	public void testAddRepository_valid_entries_no_colision()
			throws NoSuchAlgorithmException, IOException, RepositoryException
	{

		File storeFile = folder.newFile("addRepo-venc.dat");
		String storePath = storeFile.getAbsolutePath();
		TestHelpers.createMockedFile(storePath, maxNumberOfRepos - 1);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storeFile);

		RepositoryInfo actual = TestHelpers.createMockedRepoInfo("addRepo-venc");
		mgr.addRepository(actual);
		String modifiedHash = TestHelpers.hashFile(storeFile);

		// there should be only one item
		assertEquals(mgr.getRepoMap().keySet().size(), maxNumberOfRepos);
		// the file should have changed (been saved to)
		assertNotEquals(originalHash, modifiedHash);
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
	public void testAddRepository_valid_entries_colision() throws NoSuchAlgorithmException, IOException
	{

		File storeFile = folder.newFile("addRepo-vec.dat");
		String storePath = storeFile.getAbsolutePath();
		TestHelpers.createMockedFile(storePath, 1);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storeFile);

		RepositoryInfo actual = TestHelpers.createMockedRepoInfo("1"); // see createDummyFile
															// for why this is 1
		try
		{
			mgr.addRepository(actual); // this SHOULD throw an exception
			fail("Expected RepositoryException was not thrown");
		}
		catch (RepositoryException e)
		{
			assertTrue(e.getMessage().contains(expectedCollisionMsg));
		}
		String modifiedHash = TestHelpers.hashFile(storeFile);

		// there should still be only one item
		assertEquals(mgr.getRepoMap().keySet().size(), 1);
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

		File storeFile = folder.newFile("addRepo-ne.dat");
		String storePath = storeFile.getAbsolutePath();
		TestHelpers.createMockedFile(storePath, 1);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storeFile);

		RepositoryInfo actual = null;
		try
		{
			mgr.addRepository(actual); // this SHOULD throw an exception
			fail("Expected RepositoryException was not thrown");
		}
		catch (IllegalArgumentException e)
		{
		}
		String modifiedHash = TestHelpers.hashFile(storeFile);

		// there should still be only one item
		assertEquals(mgr.getRepoMap().keySet().size(), 1);
		// the file should NOT have changed (been saved to)
		assertEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#deleteRepository(int)}
	 * .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testDeleteRepository_valid_exists()
			throws FileNotFoundException, IOException, RepositoryException, NoSuchAlgorithmException
	{
		File storeFile = folder.newFile("delRepo-ve.dat");
		String storePath = storeFile.getAbsolutePath();
		TestHelpers.createMockedFile(storePath, 1);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storeFile);

		// get the id for the repo
		int actual = TestHelpers.createMockedRepoInfo("1").hashCode();
		mgr.deleteRepository(actual);
		String modifiedHash = TestHelpers.hashFile(storeFile);

		// there should be zero items
		assertEquals(mgr.getRepoMap().keySet().size(), 0);
		// the file should have changed (been saved to)
		assertNotEquals(originalHash, modifiedHash);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#deleteRepository(int)}
	 * .
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void testDeleteRepository_valid_not_exists()
			throws FileNotFoundException, IOException, NoSuchAlgorithmException
	{
		File storeFile = folder.newFile("delRepo-vne.dat");
		String storePath = storeFile.getAbsolutePath();
		TestHelpers.createMockedFile(storePath, 1);
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);

		// get sha of config to compare later
		String originalHash = TestHelpers.hashFile(storeFile);

		// get the id for the repo
		int actual = TestHelpers.createMockedRepoInfo("delRepo-vne").hashCode();
		try
		{
			mgr.deleteRepository(actual);
			fail("Expected RepositoryException was not thrown");
		}
		catch (RepositoryException e)
		{
			assertTrue(e.getMessage().contains(expectedNotExistMsg));
		}
		String modifiedHash = TestHelpers.hashFile(storeFile);

		// nothing should have been deleted
		assertEquals(mgr.getRepoMap().keySet().size(), 1);
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
		String fileName = "writeReadRepos.dat";
		File storeFile = folder.newFile(fileName);
		String storePath = storeFile.getAbsolutePath();

		List<RepositoryInfo> expectedRepos = TestHelpers.createMockedRepoList(maxNumberOfRepos);
		RepositoryInfoMgr.writeRepos(storePath, expectedRepos);

		// file should be created
		assertTrue(Files.exists(storeFile.toPath()));
		// store hash for comparison for later
		String hashAfterWrite = TestHelpers.hashFile(storeFile);

		// read the file back, testing loadRepos
		List<RepositoryInfo> actualRepos = RepositoryInfoMgr.loadRepos(storePath);
		String hashAfterLoad = TestHelpers.hashFile(storeFile);

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
		String storePath = null;
		List<RepositoryInfo> expectedRepos = TestHelpers.createMockedRepoList(maxNumberOfRepos);
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
	public void testWrite_emptyPath() throws IOException, RepositoryException, NoSuchAlgorithmException
	{
		String storePath = "";
		List<RepositoryInfo> expectedRepos = TestHelpers.createMockedRepoList(maxNumberOfRepos);
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
		String storePath = "writeNullRepos.dat";
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
		String fileName = "writeEmptyRepos.dat";
		File storeFile = folder.newFile(fileName);
		String storePath = storeFile.getAbsolutePath();

		List<RepositoryInfo> expectedRepos = TestHelpers.createMockedRepoList(0);
		RepositoryInfoMgr.writeRepos(storePath, expectedRepos);

		// file should be created
		assertTrue(Files.exists(storeFile.toPath()));
		// store hash for comparison for later
		String hashAfterWrite = TestHelpers.hashFile(storeFile);

		// read the file back, testing loadRepos
		List<RepositoryInfo> actualRepos = RepositoryInfoMgr.loadRepos(storePath);
		String hashAfterLoad = TestHelpers.hashFile(storeFile);

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
		String storePath = null;
		RepositoryInfoMgr.loadRepos(storePath);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#loadRepos(java.util.Collection)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws NoSuchAlgorithmException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoad_emptyPath() throws IOException, RepositoryException, NoSuchAlgorithmException
	{
		String storePath = "";
		RepositoryInfoMgr.loadRepos(storePath);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.RepositoryInfoMgr#getRepoMap()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetRepoMap() throws IOException
	{
		File storeFile = folder.newFile("testGetRepoMap.dat");
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storeFile.getAbsolutePath());
		Map<Integer, RepositoryInfo> repoMap = mgr.getRepoMap();
		assertNotNull(repoMap);
	}

}
