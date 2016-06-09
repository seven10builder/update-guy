package com.seven10.update_guy.test_helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

public class TestHelpers
{
	
	public static final int validDateTimestamp = 1851822000;
	public static final int versionEntryCount = 5;
	public static final int versionEntryRoleCount = 5;
	public static final long testFileLength = 1024*1024*10;
	public static Path combineToManifestFileName(String releaseFamily, Path rootFolder)
	{
		return rootFolder.resolve(String.format("%s.manifest", releaseFamily));
	}
	public static Path combineToJsonFileName(String releaseFamily, Path rootFolder)
	{
		return rootFolder.resolve(String.format("%s.json", releaseFamily));
	}
	public static Path getValidFtpRepoFilePath()
	{
		Path srcFile = Paths.get("src","test","resources","repoDefs","ftpRepo.json");
		return srcFile;
	}
	
	public static Path getValidLocalRepoFilePath()
	{
		Path srcFile = Paths.get("src","test","resources","repoDefs","localRepo.json");
		return srcFile;
	}
	public static Path get_repos_path()
	{
		Path srcFile = Paths.get("src","test","resources","repoDefs");
		return srcFile;
	}
	public static Path get_manifests_path()
	{
		Path srcFile = Paths.get("src","test","resources","manifests");
		return srcFile;
	}
	public static Path getTestFilePath()
	{
		return Paths.get("src","test","resources","repoPaths","1.0","file1");
	}

	public static String hashFile(Path storeFile) throws NoSuchAlgorithmException, IOException
	{
		final InputStream fis = new AutoCloseInputStream(new FileInputStream(storeFile.toFile()));
		return new String(Hex.encodeHex(DigestUtils.md5(fis)));
	}
	
}
