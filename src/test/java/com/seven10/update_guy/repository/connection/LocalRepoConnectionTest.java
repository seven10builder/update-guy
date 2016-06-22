/**
 * 
 */
package com.seven10.update_guy.repository.connection;

import static com.seven10.update_guy.ManifestHelpers.*;
import static com.seven10.update_guy.RepoConnectionHelpers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.RepoInfoHelpers;
import com.seven10.update_guy.TestConstants;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;

import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.DownloadValidator;

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
		Path cachePath = build_cache_path_by_testname(releaseFamily, folder);
		repo.cachePath = cachePath.toString();
		
		// setup a manifest entry to get
		Path manifestPath = build_manifest_path_by_testname(releaseFamily, folder);
		ManifestEntry entry = get_manifest_entry_from_file(manifestPath);
		
		copy_downloads_to_path(entry, cachePath);

		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		repoConnection.downloadRelease(entry);
		
		DownloadValidator.validate_downloaded_release(entry, repo.getCachePath());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.repository.Manifest.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDownloadRelease_null() throws Exception
	{
		RepositoryInfo repo = RepoInfoHelpers.load_valid_repo_info(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		ManifestEntry versionEntry = null;
		repoConnection.downloadRelease(versionEntry);
	}
}
