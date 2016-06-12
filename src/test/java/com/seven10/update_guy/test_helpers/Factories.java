package com.seven10.update_guy.test_helpers;

import static org.junit.Assert.fail;
import static com.seven10.update_guy.RepoInfoHelpers.*;
import static com.seven10.update_guy.ManifestHelpers.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

public class Factories
{
	private static final Logger logger = LogManager.getFormatterLogger(Factories.class);
	
	public static List<RepositoryInfo> load_valid_repos_list() throws IOException
	{
		Path repoPath = get_valid_repos_path();
		return load_repos_from_file(repoPath);
	}

	
	
	public static void createValidRepoFile(Path storePath) throws FileNotFoundException, IOException
	{
		Path repoFilePath = get_repos_path();
		FileUtils.copyFile(repoFilePath.toFile(), storePath.toFile());
	}
	
	public static Manifest createValidManifest(String releaseFamily, Path rootFolder) throws IOException
	{
		String json = FileUtils.readFileToString(rootFolder.toFile(), GsonFactory.encodingType);
		logger.debug(".loadValidManifest(): read file '%s' to %s", rootFolder, json);
		Gson gson = GsonFactory.getGson();
		Manifest manifest = gson.fromJson(json, Manifest.class);
		manifest.setReleaseFamily(releaseFamily);
		logger.debug(".loadValidManifest(): returning manifest = %s", manifest);
		return manifest;
	}
	
	public static ManifestVersionEntry createValidManifestEntry(String testName, int index) throws IOException
	{
		Manifest manifest = Factories.createValidManifest(testName, get_manifests_path());
		List<ManifestVersionEntry> manifestEntries = manifest.getVersionEntries();
		return manifestEntries.get(index);
	}
	
	public static Map<String, Path> createValidRolePaths(String releaseFamily) throws IOException
	{
		ManifestVersionEntry manifestEntry = createValidManifestEntry(releaseFamily, 1);
		
		Map<String, Path> map = manifestEntry.getAllPaths().stream()
				.collect(Collectors.toMap(Entry<String, Path>::getKey, Entry<String, Path>::getValue));
		return map;
	}
	
	public static Path createManifestFileForReleaseFamily(String releaseFamily, Path manPath) throws IOException
	{
		// copy valid manifest to path
		Path targetFile = manPath.resolve(String.format("%s.manifest", releaseFamily));
		FileUtils.copyFile(get_manifests_path().toFile(), targetFile.toFile());
		// return path
		return manPath;
	}
	
	public static Path createManifestFileForReleaseFamily(String releaseFamily, TemporaryFolder folder)
			throws IOException
	{
		// determine correct target path
		Path manPath = folder.newFolder("manifests").toPath();
		// copy valid manifest to path
		return createManifestFileForReleaseFamily(releaseFamily, manPath);
	}
	
	public static Path createInvalidManifestFile(Path rootFolder) throws IOException
	{
		Path destPath = rootFolder.resolve("manifests").resolve("invalid.manifest");
		// NOT a manifest object
		RepositoryInfo expected = new RepositoryInfo();
		// JSON from file to Object
		Gson gson = GsonFactory.getGson();
		String json = gson.toJson(expected);
		FileUtils.writeStringToFile(destPath.toFile(), json, "UTF-8");
		return destPath;
	}
	
	public static Path createSparseFile(String fileName, long size)
	{
		Path rval;
		RandomAccessFile file = null;
		try
		{
			file = new RandomAccessFile(fileName, "rw");
			file.setLength(size);
			rval = Paths.get(fileName);
		}
		catch (Exception e)
		{
			rval = Paths.get("");
			fail(String.format("Could not generate test file '%s'. Reason: %s", fileName, e.getMessage()));
		}
		finally
		{
			if (file != null)
			{
				try
				{
					file.close();
				}
				catch (Exception e)
				{
					fail(String.format("Could not close test file '%s' Reason: %s", fileName, e.getMessage()));
				}
			}
		}
		return rval;
	}
	
	public static Path createTestDownloadFolder(String format, String releaseFamily, TemporaryFolder folder)
			throws IOException
	{
		String fileName = String.format(format, releaseFamily);
		return folder.newFolder(fileName).toPath();
	}
	
}
