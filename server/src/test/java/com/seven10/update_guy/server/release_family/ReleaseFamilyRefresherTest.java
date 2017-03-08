/**
 * 
 */
package com.seven10.update_guy.server.release_family;

import static org.mockito.Mockito.*;
import static com.seven10.update_guy.common.ReleaseFamilyHelpers.load_release_family_file_from_path;
import static com.seven10.update_guy.common.ReleaseFamilyHelpers.get_valid_release_family_file_path;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.ReleaseFamilyHelpers;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.server.helpers.RepoInfoHelpers;
import com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher;
import com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher.FileCreator;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.server.repository.connection.RepoConnection;
import com.seven10.update_guy.server.repository.connection.RepoConnectionFactory;

/**
 * @author kmm
 *
 */
public class ReleaseFamilyRefresherTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	

	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#ReleaseFamilyRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testReleaseFamilyRefresher_valid() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId = "valid-repo-id-really";
		Path destPath = Paths.get("valid-path-really");
		ReleaseFamilyRefresher refresher = new ReleaseFamilyRefresher(repoId, destPath);
		assertNotNull(refresher);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#ReleaseFamilyRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseFamilyRefresher_null_repoId() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId = null;
		Path destPath = Paths.get("valid-path-really");
		new ReleaseFamilyRefresher(repoId, destPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#ReleaseFamilyRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseFamilyRefresher_empty_repoId() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId = "";
		Path destPath = Paths.get("valid-path-really");
		new ReleaseFamilyRefresher(repoId, destPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#ReleaseFamilyRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReleaseFamilyRefresher_null_destPath() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId =  "valid-repo-id-really";
		Path destPath = null;
		new ReleaseFamilyRefresher(repoId, destPath);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#refreshLocalReleaseFamily(java.lang.String, java.nio.file.Path, java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testRefreshLocalReleaseFamily() throws RepositoryException, FileNotFoundException, IOException
	{
		String testName = "refresh_local_man";
		RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		String releaseFamily = "relfam";
		
		ReleaseFamily expectedReleaseFamily = load_release_family_file_from_path(get_valid_release_family_file_path());
		Path releaseFamilyPath = ReleaseFamilyHelpers.get_release_family_files_path();
		
		RepoConnection mockedConnection = mock(RepoConnection.class);
		doReturn(expectedReleaseFamily).when(mockedConnection).getReleaseFamily(releaseFamily);
		
		ReleaseFamilyRefresher mr = mock(ReleaseFamilyRefresher.class);
		doReturn(mockedConnection).when(mr).createRepoConnectionForId();
		
		doReturn(releaseFamilyPath).when(mr).getDestinationPath();
		doCallRealMethod().when(mr).refreshLocalReleaseFamilyFiles(releaseFamily);
		
		
		mr.refreshLocalReleaseFamilyFiles(releaseFamily);
		ReleaseFamily actualReleaseFamily = load_release_family_file_from_path(releaseFamilyPath.resolve(Globals.buildRelFamFileName(releaseFamily)));
		assertEquals(expectedReleaseFamily, actualReleaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#refreshLocalReleaseFamily(java.lang.String, java.nio.file.Path, java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRefreshLocalReleaseFamily_null_releaseFamily() throws RepositoryException, FileNotFoundException, IOException
	{
		String releaseFamily = null;
		ReleaseFamilyRefresher mr = mock(ReleaseFamilyRefresher.class);
		doCallRealMethod().when(mr).refreshLocalReleaseFamilyFiles(anyString());
		mr.refreshLocalReleaseFamilyFiles(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#refreshLocalReleaseFamily(java.lang.String, java.nio.file.Path, java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRefreshLocalReleaseFamily_empty_releaseFamily() throws RepositoryException, FileNotFoundException, IOException
	{
		String releaseFamily = "";
		ReleaseFamilyRefresher mr = mock(ReleaseFamilyRefresher.class);
		doCallRealMethod().when(mr).refreshLocalReleaseFamilyFiles(releaseFamily);
		mr.refreshLocalReleaseFamilyFiles(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#createRepoConnectionForId(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testCreateRepoConnectionForId() throws RepositoryException, FileNotFoundException, IOException
	{
		String testName = "refresh_local_man";
		RepositoryInfo repo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		RepoConnection expected = RepoConnectionFactory.connect(repo);
		
		ReleaseFamilyRefresher mr = new ReleaseFamilyRefresher(repo.getShaHash(), Paths.get("this", "shouldnt", "matter") );		
		RepoConnection actual = mr.createRepoConnectionForId();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.server.release_family.ReleaseFamilyRefresher#updateReleaseFamilyList(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testUpdateReleaseFamilyNameList() throws RepositoryException, FileNotFoundException, IOException
	{
		String testName = "refresh_local_man";
		RepositoryInfo repo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.local);
		
		ReleaseFamilyRefresher mr = new ReleaseFamilyRefresher(repo.getShaHash(), Paths.get("this", "shouldnt", "matter") );	
		FileCreator fileCreator = mock(ReleaseFamilyRefresher.FileCreator.class);
		mr.updateReleaseFamilyList(fileCreator);
		verify(fileCreator, atLeastOnce()).run(any());
	}
}
