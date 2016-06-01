package com.seven10.update_guy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;

public class TestHelpers
{
	public static final int validDateTimestamp = 185182200;
	public static final int versionEntryCount = 5;
	public static final int versionEntryRoleCount = 5;
	public static final long testFileLength = 1024*1024*10;
	
	public static List<RepositoryInfo> createMockedRepoList(int repoCount)
	{
		List<RepositoryInfo> repos = new ArrayList<RepositoryInfo>();
		for (int i = 1; i <= repoCount; i++)
		{
			RepositoryInfo repo = createMockedRepoInfo(String.valueOf(i));
			repos.add(repo);
		}
		return repos;
	}

	public static  void createMockedFile(String repoStorePath, int repoCount) throws FileNotFoundException, IOException
	{
		ObjectOutputStream oos = null;
		try
		{
			oos = new ObjectOutputStream(new FileOutputStream(repoStorePath));
			List<RepositoryInfo> repos = createMockedRepoList(repoCount);
			for (RepositoryInfo repo : repos)
			{
				oos.writeObject(repo);
			}
		}
		finally
		{
			oos.close();
		}
	}

	public static RepositoryInfo createMockedRepoInfo(String id)
	{
		RepositoryInfo repo = new RepositoryInfo();
		repo.description = String.format("repo index = %s", id);
		repo.manifestPath = String.format("/manifest_path_%s", id);
		repo.user = String.format("user_%s", id);
		repo.password = String.format("password_%s", id);
		repo.repoAddress = String.format("repoAddress.%s", id);
		repo.cachePath = String.format("/cachePath_%s", id);
		repo.repoType = RepositoryType.ftp;
		repo.port = 21;
		return repo;
	}

	public static String hashFile(File storeFile) throws NoSuchAlgorithmException, IOException
	{
		final InputStream fis = new AutoCloseInputStream(new FileInputStream(storeFile));
		return new String(Hex.encodeHex(DigestUtils.md5(fis)));
	}
	
	public static Manifest createMockedManifest(String testName, Date dateCreated, int entryCount)
	{
		Manifest manifest = mock(Manifest.class);
		when(manifest.getReleaseFamily()).thenReturn(testName);
		when(manifest.getCreated()).thenReturn(dateCreated);
		when(manifest.getRetrieved()).thenReturn(dateCreated);
		when(manifest.getVersionEntries()).thenReturn(createValidManifestEntries(testName, entryCount, Paths.get("rootFolder")));
		return manifest;
	}

	public static Collection<ManifestVersionEntry> createValidManifestEntries(String testName, int entryCount, Path rootFolder)
	{
		List<ManifestVersionEntry> versionEntries = new ArrayList<ManifestVersionEntry>();
		for(int i=1;i <= entryCount; i++)
		{
			ManifestVersionEntry versionEntry = createValidVersionEntry(testName, i, entryCount, rootFolder);
			versionEntries.add(versionEntry);
		}
		return versionEntries;
	}

	public static ManifestVersionEntry createValidVersionEntry(String testName, int index, int roleCount, Path rootFolder)
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		versionEntry.setVersion(String.format("v%s_%d", testName,index));
		versionEntry.setPublishDate(new Date());
		createValidRolePaths(testName, roleCount, rootFolder).forEach((role, path)->versionEntry.addPath(role, path));
		return versionEntry;
	}
	public static Map<String, Path> createValidRolePaths(String testName, int rollCount, Path rootFolder)
	{
		Map<String, Path> map = new HashMap<String, Path>();
		for(int i = 1; i <= rollCount; i++)
		{
			String newPath = String.format("p%s_%d", testName, i);
			map.put(newPath, rootFolder.resolve(newPath));
		}
		return map;
	}

	public static void createInvalidManifestFile(Path rootFolder) throws   IOException
	{
		Path destPath = rootFolder.resolve("manifests").resolve("invalid.manifest");
		Map<String, Path> expected = TestHelpers.createValidRolePaths("invalid_Manifest_file", versionEntryRoleCount, rootFolder);
		// JSON from file to Object
		 GsonBuilder builder = new GsonBuilder();
	     Gson gson = builder.create();
		String json = gson.toJson(expected);
		FileUtils.writeStringToFile(destPath.toFile(), json, "UTF-8");
	}

	public static Manifest createValidManifest(String releaseFamily, Path rootFolder)
	{
		Manifest manifest = new Manifest(releaseFamily);
		manifest.setCreated(new Date());
		manifest.setRetrieved(new Date());
		createValidManifestEntries(releaseFamily, versionEntryRoleCount, rootFolder).forEach(entry->manifest.addVersionEntry(entry));
		return manifest;
	}
	/**
	 * @param fileName
	 */
	public static void createSparseFile(String fileName, long size)
	{
		RandomAccessFile file = null;
		try
		{
			file = new RandomAccessFile(fileName, "rw");
			file.setLength(size);
		}
		catch (Exception e)
		{
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
	}
	
}
