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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seven10.update_guy.TestHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.GsonFactory;
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
	/**
	 * @param releaseFamily
	 * @param destFolder
	 * @param srcPath
	 * @return
	 */
	private static LocalRepoConnection createLocalRepoInfo(String releaseFamily, Path destFolder, Path srcPath)
	{
		RepositoryInfo repo = new RepositoryInfo();
		repo.description = releaseFamily;
		repo.manifestPath = srcPath.toString();
		repo.cachePath = destFolder.toString();
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
	 * @param releaseFamily
	 * @param srcFile
	 * @return
	 * @throws RepositoryException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private void prepareManifestFile(String releaseFamily, Path srcFile) throws RepositoryException, IOException
	{
		Manifest manifest = TestHelpers.createValidManifest(releaseFamily, folder.newFile().toPath());
		Gson gson = GsonFactory.getGson();
		String json = gson.toJson(manifest);
		FileUtils.writeStringToFile(srcFile.toFile(), json, "UTF-8");
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
		
		RepositoryInfo activeRepo = TestHelpers.createMockedRepoInfo("lrc_valid");
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
	public void testDownloadManifest_valid() throws IOException, RepositoryException
	{
		String releaseFamily = "downloadMan_valid";
		String fileName = String.format("%s.manifest", releaseFamily);
		Path srcFile = createTestDownloadFolder("%s-src", releaseFamily).resolve(fileName);
		Path destFolder = createTestDownloadFolder("%s-dest", releaseFamily);
		prepareManifestFile(releaseFamily, srcFile);
		LocalRepoConnection repoConnection = createLocalRepoInfo(releaseFamily, destFolder, srcFile);
		
		repoConnection.downloadManifest(releaseFamily);
		
		validateDownload(srcFile, destFolder.resolve(fileName));
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
	public void testDownloadManifest_null() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = null;
		repoConnection.downloadManifest(expected);
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
	public void testDownloadManifest_empty() throws IOException, RepositoryException
	{
		RepositoryInfo repo = new RepositoryInfo();
		LocalRepoConnection repoConnection = new LocalRepoConnection(repo);
		String expected = "";
		repoConnection.downloadManifest(expected);
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
		LocalRepoConnection repoConnection = createLocalRepoInfo(releaseFamily, destFolder, srcFile);
		
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
		LocalRepoConnection repoConnection = createLocalRepoInfo(releaseFamily, destFolder, srcFile);
		
		ManifestVersionEntry versionEntry = null;
		repoConnection.downloadRelease(versionEntry);
	}
	
}
