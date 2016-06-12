package com.seven10.update_guy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.repository.RepositoryInfo;

public class ManifestHelpers
{
	public static Path get_valid_manifest_file_path()
	{
		Path srcFile = get_manifests_path().resolve(TestConstants.valid_manifest_name);
		return srcFile;
	}
	public static Path get_manifests_path()
	{
		Path srcFile = Paths.get("src","test","resources","manifests");
		return srcFile;
	}
	public static Manifest load_manifest_from_path(Path manifestPath) throws IOException
	{
		String json = FileUtils.readFileToString(manifestPath.toFile(), GsonFactory.encodingType);
		Gson gson = GsonFactory.getGson();
		return gson.fromJson(json, Manifest.class);
	}

	public static void copy_manifest_to_path(String manifestFileName, Path destFile) throws IOException
	{
		Path srcFile = get_manifests_path().resolve(manifestFileName);
		FileUtils.copyFile(srcFile.toFile(), destFile.toFile());
	}

	public static Path build_manifest_path_by_testname(String testName, TemporaryFolder folder) throws IOException
	{
		String fileName = String.format("%s.manifest", testName);
		return folder.newFolder(testName).toPath().resolve(fileName);
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

}
