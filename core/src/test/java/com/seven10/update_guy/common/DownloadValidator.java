package com.seven10.update_guy.common;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;

import com.seven10.update_guy.common.manifest.ManifestEntry;

public class DownloadValidator
{

	/**
	 * @param srcPath
	 * @param destFolder
	 * @throws RepositoryException
	 * @throws IOException
	 */
	public static void validate_download(Path srcPath, Path destFolder) throws IOException, Exception
	{
		assertTrue(Files.exists(destFolder, LinkOption.NOFOLLOW_LINKS));
		File srcFile = srcPath.toFile();
		File destFile = destFolder.toFile();
		assertTrue(FileUtils.contentEquals(srcFile, destFile));
	}

	public static void validate_downloaded_release(ManifestEntry versionEntry, String repoId) throws Exception
	{
		/*
		 versionEntry.getAllRoleInfos().forEach(roleEntry ->
		{
			Path srcPath = null;
			Path destPath = null;
			try
			{
				srcPath = roleEntry.getValue().getFilePath();
				destPath = ServerGlobals.buildDownloadTargetPath(repoId, versionEntry, roleEntry);
				validate_download(srcPath, destPath);
			}
			catch (Exception e)
			{
				fail(String.format("Download '%s' to '%s' was not validated: %s", srcPath, destPath, e.getMessage()));
			}
		});
		*/
		throw new NotImplementedException("validate_downloaded_release not implemented");
	}
	
}
