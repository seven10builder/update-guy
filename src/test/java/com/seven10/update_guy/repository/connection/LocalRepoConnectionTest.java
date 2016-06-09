/**
 * 
 */
package com.seven10.update_guy.repository.connection;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import static com.seven10.update_guy.manifest.ManifestHelpers.*;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.test_helpers.Factories;
import com.seven10.update_guy.test_helpers.TestHelpers;
import com.seven10.update_guy.test_helpers.Validators;

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
		RepositoryInfo activeRepo = Factories.createValidRepoInfo(RepositoryType.local);
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
		copy_manifest_to_path(validManifestFileName, manifestPath);
		Manifest expected = load_manifest_from_path(manifestPath);
		
		// set up our local repo
		String repoInfoFileName = "localRepo.json";
		List<RepositoryInfo> repos = create_repo_infos_from_filename(repoInfoFileName);
		RepositoryInfo repo = get_repo_info_by_type(repos, RepositoryType.local);
		
		// make sure we're using the path the manifest is stored at
		repo.manifestPath = manifestPath.getParent();
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
		repo.cachePath = cachePath;
		
		// setup a manifest entry to get
		Path manifestPath = build_manifest_path_by_testname(releaseFamily, folder);
		ManifestVersionEntry entry = get_manifest_entry_from_file(manifestPath);
		
		copy_downloads_to_path(entry, cachePath);

		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		repoConnection.downloadRelease(entry);
		
		Validators.validateDownloadRelease(entry, repo.cachePath);
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
		RepositoryInfo repo = Factories.createValidRepoInfo(RepositoryType.local);
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		
		ManifestVersionEntry versionEntry = null;
		repoConnection.downloadRelease(versionEntry);
	}
	
	private RepositoryInfo get_repo_info_by_type(List<RepositoryInfo> repos, RepositoryType type)
	{
		RepositoryInfo repoInfo = repos.stream().filter(repo->repo.repoType == type).findAny().orElse(new RepositoryInfo());
		return repoInfo;
	}

	private List<RepositoryInfo> create_repo_infos_from_filename(String repoInfoFileName) throws IOException
	{
		Path repoPath = TestHelpers.get_repos_path().resolve(repoInfoFileName);
		String json = FileUtils.readFileToString(repoPath.toFile(), GsonFactory.encodingType);
		Gson gson = GsonFactory.getGson();
		Type collectionType = new TypeToken<List<RepositoryInfo>>()
		{
		}.getType();
		List<RepositoryInfo> repos = gson.fromJson(json, collectionType);
		return repos;
	}
	private void copy_downloads_to_path(ManifestVersionEntry versionEntry, Path cachePath) throws IOException
	{
		for(Entry<String, Path> entry: versionEntry.getAllPaths())
		{
			Path srcFile = entry.getValue();
			Path destFile = cachePath.resolve(entry.getValue().getFileName());
			FileUtils.copyFile(srcFile.toFile(), destFile.toFile());
		}
	}

	private static Path build_cache_path_by_testname(String releaseFamily, TemporaryFolder folder) throws IOException
	{
		return folder.newFolder(String.format("%s_cache", releaseFamily)).toPath();
	}

	private static ManifestVersionEntry get_manifest_entry_from_file(Path manifestPath) throws IOException
	{		
		copy_manifest_to_path(validManifestFileName, manifestPath);
		// grab the first version entry we see
		ManifestVersionEntry manifestEntry = load_manifest_from_path(manifestPath).getVersionEntries().get(0);
		return manifestEntry;
	}
}
