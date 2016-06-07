/**
 * 
 */
package com.seven10.update_guy.repository.connection;

import static org.junit.Assert.*;

import org.junit.Test;

import com.seven10.update_guy.TestHelpers;
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
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.RepoConnectionFactory#connect(com.seven10.update_guy.repository.RepositoryInfo)}.
	 * @throws RepositoryException 
	 */
	@Test
	public void testConnect_valid() throws RepositoryException
	{
		RepositoryInfo activeRepo = TestHelpers.createValidRepoInfo("testConnectLocal");
		
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
