package com.seven10.update_guy.server.repository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.server.exceptions.RepositoryException;

public class RepositoryInfoMgr
{
	private static final Logger logger = LogManager.getFormatterLogger(RepositoryInfoMgr.class);
	
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
			logger.error(".createDefaultFile(): could not create default file '%s'. Reason: %s", repoStorePath.toString(), e.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not initialize repo config file");
		}
	}
	
	private static boolean matchRepoId(RepositoryInfo info, String repoId)
	{
		try
		{
			boolean rval = info.getShaHash().contentEquals(repoId);
			logger.info(".matchRepoId(): %s repo id for '%s' - %s", rval ? "found": "not found", repoId, info.toString());
			return rval;
		}
		catch(RepositoryException ex)
		{
			logger.error(".matchRepoId(): Could not match repo id '%s', returning false - %s", info.toString(), ex.getMessage());
			return false;
		}
	}
	
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
		logger.info(".init(): initializing RepositoryInfoMgr");
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
			logger.error(".addRepository(): repoMap already contains hash '%s' - delete first.", shaHash);
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
			logger.error(".deleteRepository(): repository entry id='%s' does not exist", repositoryId);
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
			logger.error(".writeRepos(): could not write file '%s' - %s", repoStorePath, e.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not write repo file" );
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
			logger.error(".loadRepos(): could not read repo config file '%s' - %s", repoStorePath, e.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not read repo config file");
		}
	}
	
	public static RepositoryInfo loadRepo(Path repoStorePath, String repoId) throws RepositoryException
	{
		logger.info(".loadRepo(): loading repo '%s' from file '%s'", repoId, repoStorePath);
		List<RepositoryInfo> repoList = loadRepos(repoStorePath);
		try
		{
			return repoList.stream().filter(ri->matchRepoId(ri, repoId)).findFirst().get();
		}
		catch(NoSuchElementException ex)
		{
			logger.error(".loadRepo(): could not load repo '%s' - %s", repoId, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not load repo '%s'", repoId);
		}
	}
	
	public Map<String, RepositoryInfo> getRepoMap()
	{
		return repoMap;
	}


}
