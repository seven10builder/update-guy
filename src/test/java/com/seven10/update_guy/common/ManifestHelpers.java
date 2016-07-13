package com.seven10.update_guy.common;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.server.repository.RepositoryInfo;

import jersey.repackaged.com.google.common.util.concurrent.UncheckedExecutionException;

public class ManifestHelpers
{
	public static Path get_valid_manifest_file_path()
	{
		Path srcFile = get_manifests_path().resolve(TestConstants.valid_manifest_name);
		return srcFile;
	}
	public static Path get_manifests_path()
	{
		Path srcFile = Paths.get("src","test","resources","remote_repo","2f4be34b3680b5a2556b1830f3c24232", "manifests");
		return srcFile;
	}
	public static Manifest load_manifest_from_path(Path manifestPath) throws IOException
	{
		String json = FileUtils.readFileToString(manifestPath.toFile(), GsonFactory.encodingType);
		Gson gson = GsonFactory.getGson();
		return gson.fromJson(json, Manifest.class);
	}
	
	public static List<Manifest> load_manifest_list_from_path(Path manifestsPath) throws IOException
	{
		
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.manifest");
		
		List<Manifest> files		
				= Files.walk(manifestsPath).filter(Files::isRegularFile)
						.filter(path -> matcher.matches(path))
						.map(path->
						{
							String json;
							try
							{
								json = FileUtils.readFileToString(path.toFile(), GsonFactory.encodingType);
							}
							catch (Exception e)
							{
								throw new UncheckedExecutionException(e);
							}
							Gson gson = GsonFactory.getGson();
							Manifest manifest = gson.fromJson(json, Manifest.class);
							return manifest;
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
		return files;
	}

	public static void copy_manifest_to_path(String manifestFileName, Path destFile) throws IOException
	{
		Path srcFile = get_manifests_path().resolve(manifestFileName);
		FileUtils.copyFile(srcFile.toFile(), destFile.toFile());
	}

	public static Path build_manifest_path_by_testname(String testName, TemporaryFolder folder) throws IOException
	{
		String fileName = String.format("%s.manifest", testName);
		return folder.newFolder(testName).toPath().resolve("manifests").resolve(fileName);
	}
	public static Path create_invalid_manifest_file(Path rootFolder) throws IOException
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
	public static void write_manifest_list_to_folder(Path rootPath, List<Manifest> manifestList) throws IOException
	{
		for(Manifest manifest: manifestList)
		{
			Path filePath = rootPath.resolve(manifest.getReleaseFamily() + ".manifest");
			Manifest.writeToFile(filePath, manifest);
		}
	}
	public static List<Manifest> create_manifest_list(String testName, int count) throws UpdateGuyException
	{
		List<Manifest> manifestList = new ArrayList<Manifest>();
		for(int i = 1; i <= count; i++)
		{
			Path filePath = get_manifests_path().resolve("valid.manifest");
			Manifest manifest = Manifest.loadFromFile(filePath);
			manifest.setReleaseFamily(testName + i);
			manifestList.add(manifest);
		}
		return manifestList;
	}

	public static void write_dummy_files_to_folder(Path rootPath, int count) throws IOException
	{
		for(int i =1; i<= count; i++)
		{
			Path filePath = rootPath.resolve("dummy"+i+".file");
			Files.createFile(filePath);
		}
	}

}
