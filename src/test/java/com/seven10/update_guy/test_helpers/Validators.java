package com.seven10.update_guy.test_helpers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.ManifestVersionEntry;

public class Validators
{

	/**
	 * @param srcPath
	 * @param destFolder
	 * @throws RepositoryException
	 * @throws IOException
	 */
	public static void validateDownload(Path srcPath, Path destFolder) throws RepositoryException, IOException
	{
		assertTrue(Files.exists(destFolder, LinkOption.NOFOLLOW_LINKS));
		File srcFile = srcPath.toFile();
		File destFile = destFolder.toFile();
		assertTrue(FileUtils.contentEquals(srcFile, destFile));
	}

	public static void validateDownloadRelease(ManifestVersionEntry versionEntry, Path destFolder)
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

}
