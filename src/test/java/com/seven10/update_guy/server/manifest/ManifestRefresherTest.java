/**
 * 
 */
package com.seven10.update_guy.server.manifest;

import static org.mockito.Mockito.*;
import static com.seven10.update_guy.common.RepoInfoHelpers.get_valid_repos_path;
import static com.seven10.update_guy.common.RepoInfoHelpers.get_repos_path;
import static com.seven10.update_guy.common.ManifestHelpers.load_manifest_from_path;
import static com.seven10.update_guy.common.ManifestHelpers.get_valid_manifest_file_path;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.RepoInfoHelpers;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.connection.RepoConnection;
import com.seven10.update_guy.server.repository.connection.RepoConnectionFactory;

/**
 * @author kmm
 *
 */
public class ManifestRefresherTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private Path prepareRepoFile(String testName) throws IOException
	{
		//prepare repoFile
		RepoInfoHelpers.get_valid_repos_path();
		System.setProperty(Globals.SETTING_LOCAL_PATH, get_repos_path().toString());
		System.setProperty(Globals.SETTING_REPO_FILENAME, get_valid_repos_path().getFileName().toString());
		return get_valid_repos_path();
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#ManifestRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testManifestRefresher_valid() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId = "valid-repo-id-really";
		Path destPath = Paths.get("valid-path-really");
		ManifestRefresher refresher = new ManifestRefresher(repoId, destPath);
		assertNotNull(refresher);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#ManifestRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifestRefresher_null_repoId() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId = null;
		Path destPath = Paths.get("valid-path-really");
		new ManifestRefresher(repoId, destPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#ManifestRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifestRefresher_empty_repoId() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId = "";
		Path destPath = Paths.get("valid-path-really");
		new ManifestRefresher(repoId, destPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#ManifestRefresher(java.lang.String, java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifestRefresher_null_destPath() throws RepositoryException, FileNotFoundException, IOException
	{
		String repoId =  "valid-repo-id-really";
		Path destPath = null;
		new ManifestRefresher(repoId, destPath);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#refreshLocalManifest(java.lang.String, java.nio.file.Path, java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testRefreshLocalManifest() throws RepositoryException, FileNotFoundException, IOException
	{
		String testName = "refresh_local_man";
		prepareRepoFile(testName);
		
		String releaseFamily = "valid";
		Path manifestPath = folder.newFolder(testName + "local-cache").toPath();
		
		
		Manifest expectedManifest = load_manifest_from_path(get_valid_manifest_file_path());
		
		RepoConnection mockedConnection = mock(RepoConnection.class);
		doReturn(expectedManifest).when(mockedConnection).getManifest(releaseFamily);
		
		ManifestRefresher mr = mock(ManifestRefresher.class);
		doReturn(mockedConnection).when(mr).createRepoConnectionForId();
		doReturn(manifestPath).when(mr).getDestinationPath();
		doCallRealMethod().when(mr).refreshLocalManifest(releaseFamily);
		
		
		mr.refreshLocalManifest(releaseFamily);
		Manifest actualManifest = load_manifest_from_path(manifestPath.resolve(releaseFamily + ".manifest"));
		assertEquals(expectedManifest, actualManifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#refreshLocalManifest(java.lang.String, java.nio.file.Path, java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRefreshLocalManifest_null_releaseFamily() throws RepositoryException, FileNotFoundException, IOException
	{
		String releaseFamily = null;
		ManifestRefresher mr = mock(ManifestRefresher.class);
		doCallRealMethod().when(mr).refreshLocalManifest(anyString());
		mr.refreshLocalManifest(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#refreshLocalManifest(java.lang.String, java.nio.file.Path, java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testRefreshLocalManifest_empty_releaseFamily() throws RepositoryException, FileNotFoundException, IOException
	{
		String releaseFamily = "";
		ManifestRefresher mr = mock(ManifestRefresher.class);
		doCallRealMethod().when(mr).refreshLocalManifest(releaseFamily);
		mr.refreshLocalManifest(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.server.manifest.ManifestRefresher#createRepoConnectionForId(java.lang.String)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testCreateRepoConnectionForId() throws RepositoryException, FileNotFoundException, IOException
	{
		String testName = "refresh_local_man";
		Path repoPath = prepareRepoFile(testName);
		List<RepositoryInfo> repos = RepoInfoHelpers.load_repos_from_file(repoPath);
		for(RepositoryInfo repo: repos)
		{
			RepoConnection expected = RepoConnectionFactory.connect(repo);
		
			ManifestRefresher mr = new ManifestRefresher(repo.getShaHash(), Paths.get("this", "shouldnt", "matter") );		
			RepoConnection actual = mr.createRepoConnectionForId();
			assertEquals(expected, actual);
		}
	}
	
}
