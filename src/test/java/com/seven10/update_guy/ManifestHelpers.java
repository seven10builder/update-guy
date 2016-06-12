package com.seven10.update_guy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.manifest.Manifest;

public class ManifestHelpers
{
	public static Path get_valid_manifest_file_path()
	{
		Path srcFile = get_manifests_path().resolve(validManifestFileName);
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

	public static final String validManifestFileName = "valid.manifest";

}
