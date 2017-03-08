/**
 * 
 */
package com.seven10.update_guy.server.repository.connection;

import static com.seven10.update_guy.common.ReleaseFamilyHelpers.*;
import static com.seven10.update_guy.server.helpers.RepoConnectionHelpers.*;
import static com.seven10.update_guy.server.helpers.RepoInfoHelpers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.server.helpers.RepoInfoHelpers;
import com.seven10.update_guy.common.DownloadValidator;
import com.seven10.update_guy.common.ReleaseFamilyHelpers;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.server.repository.SpyablePathConsumer;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.server.exceptions.RepositoryException;

/**
 * @author kmm
 * 		
 */
public class LocalRepoConnectionTest
{
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#LocalRepoConnection(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testLocalRepoConnection_valid() throws Exception
	{
		RepositoryInfo activeRepo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(activeRepo);
		assertNotNull(repoConnection);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadReleaseFamily(java.lang.String)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testgetReleaseFamily_valid() throws Exception
	{
		String releaseFamily = "relfam";
	
		ReleaseFamily expected = load_release_family_file_from_path(get_valid_release_family_file_path());
		
		// set up our local repo
		List<RepositoryInfo> repos = load_repos_from_file(get_valid_repos_path());
		RepositoryInfo repo = get_repo_info_by_type(repos, RepositoryType.local);
		
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
	
		ReleaseFamily actual = repoConnection.getReleaseFamily(releaseFamily);
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadReleaseFamily(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testgetReleaseFamily_null() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = null;
		repoConnection.getReleaseFamily(expected);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadReleaseFamily(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testgetReleaseFamily_empty() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = "";
		repoConnection.getReleaseFamily(expected);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadReleaseFamily(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test(expected = RepositoryException.class)
	public void testgetReleaseFamily_notFound() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = "weKnowThisAintThere";
		repoConnection.getReleaseFamily(expected);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.ReleaseFamily.ReleaseFamily.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testDownloadRelease_valid() throws Exception
	{
		String releaseFamily = "downloadRel_valid";

		// set up our local repo
		String repoInfoFileName = "localRepo.json";
		List<RepositoryInfo> repos = create_repo_infos_from_filename(repoInfoFileName);
		RepositoryInfo repo = get_repo_info_by_type(repos, RepositoryType.local);
		
		// point repo at our test cache
		//Path cachePath = build_cache_path_by_testname(releaseFamily, folder);
		
		// setup a release family entry to get
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		ReleaseFamilyEntry entry = get_releaseFamily_entry_from_file(releaseFamilyPath);
		int roleCount = entry.getRoles().size();
		
		//copy_downloads_to_path(entry, cachePath);

		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		Consumer<Path> spiedOnFileComplete = spy(new SpyablePathConsumer());
		repoConnection.downloadRelease(entry, spiedOnFileComplete );
		verify(spiedOnFileComplete, times(roleCount)).accept(any());
		
		DownloadValidator.validate_downloaded_release(entry, repo.getShaHash());
	}
		/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.ReleaseFamily.ReleaseFamily.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testDownloadRelease_null() throws Exception
	{
		RepositoryInfo repo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		ReleaseFamilyEntry versionEntry = null;
		Consumer<Path> spiedOnFileComplete = spy(new SpyablePathConsumer());
		try
		{
			repoConnection.downloadRelease(versionEntry, spiedOnFileComplete );
			//we expect this to fail
			fail("downloadRelease should have thrown an IllegalArgumentException");
		}
		catch(IllegalArgumentException ex)
		{
			verify(spiedOnFileComplete, never()).accept(any());
		}
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.ReleaseFamily.ReleaseFamily.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDownloadRelease_null_onFileComplete() throws Exception
	{
		RepositoryInfo repo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		ReleaseFamilyEntry versionEntry = new ReleaseFamilyEntry();
		Consumer<Path> spiedOnFileComplete = null;
		repoConnection.downloadRelease(versionEntry, spiedOnFileComplete );
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.ReleaseFamily.ReleaseFamily.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testGetFileNames_valid() throws Exception
	{
		Path targetDir = ReleaseFamilyHelpers.get_release_family_files_path();
		List<String> expectedFiles = Files.walk(targetDir).filter(Files::isRegularFile)
				.filter(Objects::nonNull)
				.map(file->file.getFileName().toString())
				.collect(Collectors.toList());
		
		RepositoryInfo repo = load_valid_repo_info(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		List<String> actualFiles = repoConnection.getFileNames();
		assertNotEquals(0, actualFiles.size());
		assertTrue(expectedFiles.containsAll(actualFiles));
		assertTrue(actualFiles.containsAll(expectedFiles));
	}

}
