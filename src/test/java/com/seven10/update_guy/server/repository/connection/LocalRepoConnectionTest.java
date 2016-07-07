/**
 * 
 */
package com.seven10.update_guy.server.repository.connection;

import static com.seven10.update_guy.common.ManifestHelpers.*;
import static com.seven10.update_guy.common.RepoConnectionHelpers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.common.RepoInfoHelpers;
import com.seven10.update_guy.common.DownloadValidator;
import com.seven10.update_guy.common.TestConstants;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
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
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadManifest(java.lang.String)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testgetManifest_valid() throws Exception
	{
		String releaseFamily = "getManifest-v";
		
		// setup a manifest to get
		Path manifestPath = build_manifest_path_by_testname(releaseFamily, folder);
		copy_manifest_to_path(TestConstants.valid_manifest_name, manifestPath);
		Manifest expected = load_manifest_from_path(manifestPath);
		
		// set up our local repo
		String repoInfoFileName = "localRepo.json";
		List<RepositoryInfo> repos = create_repo_infos_from_filename(repoInfoFileName);
		RepositoryInfo repo = get_repo_info_by_type(repos, RepositoryType.local);
		
		// make sure we're using the path the manifest is stored at
		repo.manifestPath = manifestPath.getParent().toString();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		Manifest actual = repoConnection.getManifest(releaseFamily);
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadManifest(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testgetManifest_null() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = null;
		repoConnection.getManifest(expected);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadManifest(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testgetManifest_empty() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = "";
		repoConnection.getManifest(expected);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadManifest(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test(expected = RepositoryException.class)
	public void testgetManifest_notFound() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = "weKnowThisAintThere";
		repoConnection.getManifest(expected);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.repository.Manifest.VersionEntry)}
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
		
		// setup a manifest entry to get
		Path manifestPath = build_manifest_path_by_testname(releaseFamily, folder);
		ManifestEntry entry = get_manifest_entry_from_file(manifestPath);
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
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.repository.Manifest.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testDownloadRelease_null() throws Exception
	{
		RepositoryInfo repo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		ManifestEntry versionEntry = null;
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
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.repository.Manifest.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDownloadRelease_null_onFileComplete() throws Exception
	{
		RepositoryInfo repo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		ManifestEntry versionEntry = new ManifestEntry();
		Consumer<Path> spiedOnFileComplete = null;
		repoConnection.downloadRelease(versionEntry, spiedOnFileComplete );
	}
}
