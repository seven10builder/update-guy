package com.seven10.update_guy.repository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;

public class RepositoryInfoMgr
{
	private final Path repositoryStorePath;
	private Map<Integer, RepositoryInfo> repoMap;

	/**
	 * Creates a new repository manager
	 * @param repositoryStoreFile the path to the file containing the repository information
	 * @throws RepositoryException
	 */
	public RepositoryInfoMgr(Path repositoryStoreFile) throws RepositoryException
	{
		this.repositoryStorePath = repositoryStoreFile;
		repoMap = new HashMap<Integer, RepositoryInfo>();
		init();
	}
	
	private void init() throws RepositoryException
	{
		List<RepositoryInfo> repos = loadRepos(repositoryStorePath);
		repos.forEach(repo->repoMap.put(repo.hashCode(), repo));
	}

	public void addRepository(RepositoryInfo repoInfo) throws RepositoryException
	{
		if(repoInfo == null)
		{
			throw new IllegalArgumentException("repoInfo cannot be null");
		}
		int hash = repoInfo.hashCode();
		if (repoMap.containsKey(hash))
		{
			throw new RepositoryException("repoMap already contains hash '%d'. Delete first.", hash);
		}
		// store repositoryInfo list
		repoMap.put(hash, repoInfo);
		writeRepos(repositoryStorePath, new ArrayList<RepositoryInfo>(repoMap.values()));
	}

	public void deleteRepository(int repositoryId) throws RepositoryException
	{
		// find repositoryInfo object for repositoryId in list
		if (repoMap.containsKey(repositoryId) == false)
		{
			throw new RepositoryException("Repository entry id='%d' does not exist", repositoryId);
		}
		// remove repositoryInfo object from list
		repoMap.remove(repositoryId);
		writeRepos(repositoryStorePath, new ArrayList<RepositoryInfo>(repoMap.values()));
	}

	public static void writeRepos(Path repoStorePath, List<RepositoryInfo> repos) throws RepositoryException
	{
		if (repoStorePath == null)
		{
			throw new IllegalArgumentException("repoStorePath must not be null");
		}
		if (repos == null)
		{
			throw new IllegalArgumentException("repos must not be null");
		}
		Gson gson = GsonFactory.getGson();
		String json = gson.toJson(repos);
		try
		{
			FileUtils.writeStringToFile(repoStorePath.toFile(), json, GsonFactory.encodingType);
		}
		catch (IOException e)
		{
			throw new RepositoryException("Could not write file '%s'. Exception: %s", repoStorePath, e.getMessage());
		}
	}

	public static List<RepositoryInfo> loadRepos(Path repoStorePath) throws RepositoryException
	{
		if(repoStorePath == null)
		{
			throw new IllegalArgumentException("repoStorePath cannot be null");
		}
		try
		{
			List<RepositoryInfo> repos;
			String json = FileUtils.readFileToString(repoStorePath.toFile(), GsonFactory.encodingType);
			if(json.isEmpty())
			{
				repos = new ArrayList<RepositoryInfo>(); 
			}
			else
			{
				Gson gson = GsonFactory.getGson();
				Type collectionType = new TypeToken<List<RepositoryInfo>>(){}.getType();
				repos = gson.fromJson(json, collectionType);
			}
			return repos;
		}
		catch (IOException e)
		{
			throw new RepositoryException("Could not read file '%s'. Exception: %s", repoStorePath, e.getMessage());
		}
	}

	public Map<Integer, RepositoryInfo> getRepoMap()
	{
		return repoMap;
	}


}
