package com.seven10.update_guy.repository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;

public class RepositoryInfoMgr
{
	private static final Logger logger = LogManager.getFormatterLogger(RepositoryInfoMgr.class);
	
	private final Path repositoryStorePath;
	private Map<String, RepositoryInfo> repoMap;

	/**
	 * Creates a new repository manager
	 * @param repositoryStoreFile the path to the file containing the repository information
	 * @throws RepositoryException
	 */
	public RepositoryInfoMgr(Path repoStorePath) throws RepositoryException
	{
		this.repositoryStorePath = repoStorePath;
		repoMap = new HashMap<String, RepositoryInfo>();
		init();
	}
	
	private void init() throws RepositoryException
	{
		if(Files.exists(repositoryStorePath)==false)
		{
			createDefaultFile(repositoryStorePath);
		}
		List<RepositoryInfo> repos = loadRepos(repositoryStorePath);
		for(RepositoryInfo repo: repos)
		{
			String repoHash = repo.getShaHash();
			logger.debug("adding repo '%s' to repoMap", repoHash);	
			repoMap.put(repoHash, repo);
		}
	}

	public void addRepository(final RepositoryInfo repoInfo) throws RepositoryException
	{
		if(repoInfo == null)
		{
			throw new IllegalArgumentException("repoInfo cannot be null");
		}
		String shaHash = repoInfo.getShaHash();
		if (repoMap.containsKey(shaHash))
		{
			throw new RepositoryException(Status.NOT_MODIFIED, "repoMap already contains hash '%s'. Delete first.", shaHash);
		}
		// store repositoryInfo list
		repoMap.put(repoInfo.getShaHash(), repoInfo);
		writeRepos(repositoryStorePath, new ArrayList<RepositoryInfo>(repoMap.values()));
	}

	public void deleteRepository(String repositoryId) throws RepositoryException
	{
		// find repositoryInfo object for repositoryId in list
		if (repoMap.containsKey(repositoryId) == false)
		{
			throw new RepositoryException(Status.NOT_FOUND, "Repository entry id='%s' does not exist", repositoryId);
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
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not write file '%s'. Exception: %s", repoStorePath, e.getMessage());
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
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not read file '%s'. Exception: %s", repoStorePath, e.getMessage());
		}
	}

	private static void createDefaultFile(Path repoStorePath) throws RepositoryException
	{
		Path parent = repoStorePath.getParent();
		try
		{
			parent.toFile().mkdirs();
			Files.createFile(repoStorePath);
		}
		catch (IOException e)
		{
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, 
					"Could not create default file '%s'. Reason: %s", repoStorePath.toString(), e.getMessage());
		}
	}

	public Map<String, RepositoryInfo> getRepoMap()
	{
		return repoMap;
	}


}
