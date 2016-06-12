/**
 * 
 */
package com.seven10.update_guy.repository.connection;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.RepoInfoHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;

import static org.hamcrest.CoreMatchers.instanceOf;

/**
 * @author kmm
 *
 */
public class RepoConnectionFactoryTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.RepoConnectionFactory#connect(com.seven10.update_guy.repository.RepositoryInfo)}.
	 * @throws Exception 
	 */
	@Test
	public void testConnect_valid() throws Exception
	{
		
		RepositoryInfo activeRepo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		
		// test local
		activeRepo.repoType = RepositoryType.local;
		RepoConnection actual = RepoConnectionFactory.connect(activeRepo);
		assertNotNull(actual);
		assertThat(actual, instanceOf(LocalRepoConnection.class));
		
		// test ftp
		activeRepo.repoType = RepositoryType.ftp;
		actual = RepoConnectionFactory.connect(activeRepo);
		assertNotNull(actual);
		assertThat(actual, instanceOf(FtpRepoConnection.class));
		
	}	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.RepoConnectionFactory#connect(com.seven10.update_guy.repository.RepositoryInfo)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testConnect_null() throws RepositoryException
	{
		RepositoryInfo activeRepo = null;
		RepoConnectionFactory.connect(activeRepo);
	}		
}
