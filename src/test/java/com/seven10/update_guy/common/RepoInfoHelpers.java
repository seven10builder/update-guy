package com.seven10.update_guy.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;

public class RepoInfoHelpers
{

	
	public static Path get_valid_repos_path()
	{
		Path srcFile = RepoInfoHelpers.get_repos_path().resolve("valid.json");
		return srcFile;
	}

	public static Path get_repos_path()
	{
		Path srcFile = Paths.get("src","test","resources","repoDefs");
		return srcFile;
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



	public static String hashFile(Path storeFile) throws NoSuchAlgorithmException, IOException
	{
		final InputStream fis = new AutoCloseInputStream(new FileInputStream(storeFile.toFile()));
		return new String(Hex.encodeHex(DigestUtils.md5(fis)));
	}

	public static List<RepositoryInfo> load_valid_repos_list() throws IOException
	{
		Path repoPath = get_valid_repos_path();
		return load_repos_from_file(repoPath);
	}

	public static RepositoryInfo load_valid_repo_info(RepositoryType repoType) throws IOException
	{
		List<RepositoryInfo> repoList = load_valid_repos_list();
		return repoList.stream().filter(ri->ri.repoType == repoType).findFirst().get();
	}

	/**
	 * @param testName
	 * @param temporaryFolder TODO
	 * @param repositoryType TODO
	 * @return
	 * @throws IOException
	 */
	public static RepositoryInfo setup_test_repo(String testName, TemporaryFolder temporaryFolder, RepositoryType repositoryType) throws IOException
	{
		RepositoryInfo repoInfo = load_valid_repo_info(repositoryType);
		// calc the repoId
		
		// create local root path
		Path localDir = temporaryFolder.newFolder(testName).toPath();
	
		// copy repo config to localDir
		String fileName = "repos.json";
		Path repoInfoFile = localDir.resolve(fileName);
		FileUtils.copyFile(get_valid_repos_path().toFile(), repoInfoFile.toFile());
		
		// set attribute so servlet picks it up
		System.setProperty(ServerGlobals.SETTING_LOCAL_PATH, localDir.toString());
		System.setProperty(ServerGlobals.SETTING_REPO_FILENAME, fileName);
	
		return repoInfo;
	}

}
