/**
 * 
 */
package com.seven10.update_guy.repository.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfoMgr;

/**
 * @author dra
 *
 */
public class RepositoryInfoMgrTest
{

	  @Rule
	  public TemporaryFolder folder = new TemporaryFolder();
	  
	  private void createDummyFile(String repoStorePath, int repoCount) throws FileNotFoundException, IOException
		{
			ObjectOutputStream oos = null;
			try
			{
				oos = new ObjectOutputStream(new FileOutputStream(repoStorePath));
				for (int i=0; i < repoCount; i++)
				{
					RepositoryInfo repo = createDummyRepoInfo(i);
					oos.writeObject(repo);
				}
			}
			finally
			{
				oos.close();
			}
			
		}

		private RepositoryInfo createDummyRepoInfo(int repoIndex)
		{
			RepositoryInfo repo = Mockito.mock(RepositoryInfo.class);
			Mockito.when(repo.description).thenReturn(String.format("repo index = %d", repoIndex));
			Mockito.when(repo.manifestPath).thenReturn(String.format("/manifest_path_%d", repoIndex));
			Mockito.when(repo.user).thenReturn(String.format("user_%d", repoIndex));
			Mockito.when(repo.password).thenReturn(String.format("password_%d", repoIndex));
			Mockito.when(repo.repoAddress).thenReturn(String.format("repoAddress.%d", repoIndex));
			return repo;
		}
		
	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfoMgr#RepositoryInfoMgr()}.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testRepositoryInfoMgr_emptyRepoList() throws FileNotFoundException, IOException
	{
		String storePath = folder.newFile("emptyRepoList.dat").getAbsolutePath();
		createDummyFile(storePath, 0);		
		RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);
		assertTrue(mgr.getRepoMap().keySet().isEmpty());
	}

	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfoMgr#RepositoryInfoMgr()}.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testRepositoryInfoMgr_ManyRepoList() throws FileNotFoundException, IOException
	{
		for(int i = 0; i < 5; i++)
		{
			String fileName = String.format("ManyRepoList%d.dat", i);		
			String storePath = folder.newFile(fileName).getAbsolutePath();
			createDummyFile(storePath, i);		
			RepositoryInfoMgr mgr = new RepositoryInfoMgr(storePath);
			assertEquals(mgr.getRepoMap().keySet().size(), i);
		}
	}

	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfoMgr#addRepository(com.seven10.update_guy.repository.RepositoryInfo)}.
	 */
	@Test
	public void testAddRepository()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfoMgr#deleteRepository(int)}.
	 */
	@Test
	public void testDeleteRepository()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfoMgr#writeRepos(java.util.Collection)}.
	 */
	@Test
	public void testWriteRepos()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfoMgr#loadRepos()}.
	 */
	@Test
	public void testLoadRepos()
	{
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.seven10.update_guy.repository.RepositoryInfoMgr#getRepoMap()}.
	 */
	@Test
	public void testGetRepoMap()
	{
		fail("Not yet implemented");
	}

}
