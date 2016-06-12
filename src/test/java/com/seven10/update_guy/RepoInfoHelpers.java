package com.seven10.update_guy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;

public class RepoInfoHelpers
{

	public static Path get_valid_ftp_repo_path()
	{
		Path srcFile = RepoInfoHelpers.get_repos_path().resolve("ftpRepo.json");
		return srcFile;
	}

	public static Path get_valid_repos_path()
	{
		Path srcFile = RepoInfoHelpers.get_repos_path().resolve("valid.json");
		return srcFile;
	}

	public static Path get_valid_local_repo_path()
	{
		Path srcFile = RepoInfoHelpers.get_repos_path().resolve("localRepo.json");
		return srcFile;
	}

	public static Path get_repos_path()
	{
		Path srcFile = Paths.get("src","test","resources","repoDefs");
		return srcFile;
	}

	public static void copy_valid_repos_to_test(Path destPath) throws IOException
	{
		Path srcPath = get_valid_repos_path();
		FileUtils.copyFile(srcPath.toFile(), destPath.toFile());
	}

	public static Path build_repo_info_file_by_testname(String testName, TemporaryFolder folder) throws IOException
	{
			String fileName = String.format("%s.json", testName);
			return folder.newFolder(testName).toPath().resolve(fileName);
	}

	public static List<RepositoryInfo> load_repos_from_file(Path repoPath) throws IOException, JsonSyntaxException
	{
		String json = FileUtils.readFileToString(repoPath.toFile(), GsonFactory.encodingType);
		Gson gson = GsonFactory.getGson();
		Type collectionType = new TypeToken<List<RepositoryInfo>>()
		{
		}.getType();
		List<RepositoryInfo> repos = gson.fromJson(json, collectionType);
		return repos;
	}

	public static RepositoryInfo load_valid_repo_info(RepositoryType type) throws Exception
	{
		Path repoDefPath;
		switch (type)
		{
		case ftp:
			repoDefPath = get_valid_ftp_repo_path();
			break;
		case local:
			repoDefPath = get_valid_local_repo_path();
			break;
		default:
			throw new Exception("incorrect type passed in");
		}
		List<RepositoryInfo> repos = load_repos_from_file(repoDefPath);
		return repos.get(0);
	}

}
