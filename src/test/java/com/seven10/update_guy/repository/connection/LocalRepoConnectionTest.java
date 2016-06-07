/**
 * 
 */
package com.seven10.update_guy.repository.connection;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.TestHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;

/**
 * @author kmm
 * 		
 */
public class LocalRepoConnectionTest
{
	private static final Logger logger = LogManager.getFormatterLogger(LocalRepoConnectionTest.class);
	/**
	 * @param srcPath
	 * @param destFolder
	 * @param releaseFamily
	 * @return
	 */
	private static LocalRepoConnection createLocalRepoInfo(Path srcPath, Path destFolder, String releaseFamily)
	{
		RepositoryInfo repo = new RepositoryInfo();
		repo.description = releaseFamily;
		repo.manifestPath = srcPath;
		repo.cachePath = destFolder;
		repo.repoType = RepositoryType.local;
		return new LocalRepoConnection(repo);
	}
	
	private Path createTestDownloadFolder(String format, String releaseFamily) throws IOException
	{
		Path destFolder = folder.newFolder(String.format(format, releaseFamily)).toPath();
		return destFolder;
	}
	
	private void prepareDownloadFiles(String releaseFamily, Path srcFile, ManifestVersionEntry versionEntry)
	{
		versionEntry.getAllPaths().forEach(entry ->
		{
			String fileName = entry.getValue().toString();
			TestHelpers.createSparseFile(fileName, TestHelpers.testFileLength);
		});
	}

	/**
	 * @param srcPath
	 * @param destFolder
	 * @throws RepositoryException
	 * @throws IOException
	 */
	private void validateDownload(Path srcPath, Path destFolder) throws RepositoryException, IOException
	{
		assertTrue(Files.exists(destFolder, LinkOption.NOFOLLOW_LINKS));
		File srcFile = srcPath.toFile();
		File destFile = destFolder.toFile();
		assertTrue(FileUtils.contentEquals(srcFile, destFile));
	}
	
	private void validateDownloadRelease(ManifestVersionEntry versionEntry, Path destFolder)
	{
		versionEntry.getAllPaths().forEach(entry ->
		{
			Path srcPath = entry.getValue();
			Path fileName = srcPath.getFileName();
			Path destPath = destFolder.resolve(fileName);
			try
			{
				validateDownload(srcPath, destPath);
			}
			catch (Exception e)
			{
				fail(String.format("Download '%s' to '%s' was not validated: %s", srcPath, destPath, e.getMessage()));
			}
		});
	}
	
		
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#LocalRepoConnection(com.seven10.update_guy.repository.RepositoryInfo)}
	 * .
	 */
	@Test
	public void testLocalRepoConnection_valid()
	{
		
		RepositoryInfo activeRepo = TestHelpers.createValidRepoInfo("lrc_valid");
		LocalRepoConnection repoConnection = new LocalRepoConnection(activeRepo);
		assertNotNull(repoConnection);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadManifest(java.lang.String)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test
	public void testgetManifest_valid() throws IOException, RepositoryException
	{
		String releaseFamily = "relfam";
		
		Path srcManifestPath = TestHelpers.createManifestForReleaseFamily(releaseFamily, folder);
		Path cacheFolder = createTestDownloadFolder("%s-dest", releaseFamily);
		
		LocalRepoConnection repoConnection = createLocalRepoInfo(srcManifestPath, cacheFolder, releaseFamily);
		Path manifestFile = srcManifestPath.resolve(String.format("%s.manifest", releaseFamily));
		logger.debug(".testgetManifest_valid(): manifestFile = %s", manifestFile);
		
		Manifest expected = TestHelpers.loadValidManifest(releaseFamily, manifestFile);
		
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
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test
	public void testDownloadRelease_valid() throws IOException, RepositoryException
	{
		String releaseFamily = "downloadRel_valid";
		Path srcFile = createTestDownloadFolder("%s-src", releaseFamily);
		Path destFolder = createTestDownloadFolder("%s-dest", releaseFamily);
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1,
				TestHelpers.versionEntryRoleCount, srcFile);
				
		prepareDownloadFiles(releaseFamily, srcFile, versionEntry);
		LocalRepoConnection repoConnection = createLocalRepoInfo(srcFile, destFolder, releaseFamily);
		
		repoConnection.downloadRelease(versionEntry);
		
		validateDownloadRelease(versionEntry, destFolder);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.LocalRepoConnection#downloadRelease(com.seven10.update_guy.repository.Manifest.VersionEntry)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDownloadRelease_null() throws IOException, RepositoryException
	{
		String releaseFamily = "downloadRel_valid";
		Path srcFile = createTestDownloadFolder("%s-src", releaseFamily);
		Path destFolder = createTestDownloadFolder("%s-dest", releaseFamily);
		LocalRepoConnection repoConnection = createLocalRepoInfo(srcFile, destFolder, releaseFamily);
		
		ManifestVersionEntry versionEntry = null;
		repoConnection.downloadRelease(versionEntry);
	}
	
}
