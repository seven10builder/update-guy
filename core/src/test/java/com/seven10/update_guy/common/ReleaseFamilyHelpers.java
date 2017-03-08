package com.seven10.update_guy.common;

import java.io.IOException;
import java.io.UncheckedIOException;
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
import org.apache.commons.lang3.NotImplementedException;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;


public class ReleaseFamilyHelpers
{

	
	public static Path get_valid_release_family_file_path()
	{
		Path srcFile = get_release_family_files_path().resolve(TestConstants.valid_release_family_name);
		return srcFile;
	}
	public static Path get_release_family_files_path()
	{
		Path srcFile = Paths.get("src","test","resources","remote_repo","local", "2f4be34b3680b5a2556b1830f3c24232", "releaseFamilies");
		return srcFile;
	}
	public static ReleaseFamily load_release_family_file_from_path(Path releaseFamilyPath) throws IOException
	{
		String json = FileUtils.readFileToString(releaseFamilyPath.toFile(), GsonFactory.encodingType);
		Gson gson = GsonFactory.getGson();
		return gson.fromJson(json, ReleaseFamily.class);
	}
	
	public static List<ReleaseFamily> load_releaseFamily_list_from_path(Path releaseFamiliesPath) throws IOException
	{
		
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher(Globals.RELEASE_FAM_GLOB);
		
		List<ReleaseFamily> files		
				= Files.walk(releaseFamiliesPath).filter(Files::isRegularFile)
						.filter(path -> matcher.matches(path))
						.map(path->
						{
							String json;
							try
							{
								json = FileUtils.readFileToString(path.toFile(), GsonFactory.encodingType);
							}
							catch (IOException e)
							{
								throw new UncheckedIOException(e);
							}
							Gson gson = GsonFactory.getGson();
							ReleaseFamily releaseFamily = gson.fromJson(json, ReleaseFamily.class);
							return releaseFamily;
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
		return files;
	}

	public static void copy_release_family_file_to_path(String releaseFamilyFileName, Path destFile) throws IOException
	{
		Path srcFile = get_release_family_files_path().resolve(releaseFamilyFileName);
		FileUtils.copyFile(srcFile.toFile(), destFile.toFile());
	}

	public static Path build_release_family_file_path_by_testname(String testName, TemporaryFolder folder) throws IOException
	{
		String fileName = String.format("%s.%s", testName, Globals.RELEASE_FAM_FILE_EXT);
		return folder.newFolder(testName).toPath().resolve("releaseFamilies").resolve(fileName);
	}
	public static Path create_invalid_release_family_file(Path rootFolder) throws IOException
	{
		//Path destPath = rootFolder.resolve("releaseFamilys").resolve(String.format("invalid.%s", RELEASE_FAM_FILE_EXT);
		// NOT a releaseFamily object
		//RepositoryInfo expected = new RepositoryInfo();
		// JSON from file to Object
		//Gson gson = GsonFactory.getGson();
		//String json = gson.toJson(expected);
		//FileUtils.writeStringToFile(destPath.toFile(), json, "UTF-8");
		//return destPath;
		throw new NotImplementedException("create_invalid_releaseFamily_file not implemented");
	}
	public static void write_releaseFamily_list_to_folder(Path rootPath, List<ReleaseFamily> releaseFamilyList) throws IOException
	{
		for(ReleaseFamily releaseFamily: releaseFamilyList)
		{
			Path filePath = rootPath.resolve(releaseFamily.getReleaseFamily() + "." + Globals.RELEASE_FAM_FILE_EXT);
			ReleaseFamily.writeToFile(filePath, releaseFamily);
		}
	}
	public static List<ReleaseFamily> create_releaseFamily_list(String testName, int count) throws UpdateGuyException
	{
		List<ReleaseFamily> releaseFamilyList = new ArrayList<ReleaseFamily>();
		for(int i = 1; i <= count; i++)
		{
			Path filePath = get_release_family_files_path().resolve(String.format("valid.%s", Globals.RELEASE_FAM_FILE_EXT ));
			ReleaseFamily releaseFamily = ReleaseFamily.loadFromFile(filePath);
			releaseFamily.setReleaseFamily(testName + i);
			releaseFamilyList.add(releaseFamily);
		}
		return releaseFamilyList;
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
