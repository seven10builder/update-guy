package com.seven10.update_guy.repository;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seven10.update_guy.exceptions.RepositoryException;

public class RepositoryInfoMgr
{
	private final String repositoryStorePath;
	Map<Integer, RepositoryInfo> repoMap;

	public RepositoryInfoMgr(String repositoryStorePath)
	{
		this.repositoryStorePath = repositoryStorePath;
		repoMap = new HashMap<Integer, RepositoryInfo>();
		init();
	}
	
	private void init()
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
		writeRepos(repositoryStorePath, repoMap.values());
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
		writeRepos(repositoryStorePath, repoMap.values());
	}

	public static void writeRepos(String repoStorePath, Collection<RepositoryInfo> repos) throws RepositoryException
	{
		if(repoStorePath == null || repoStorePath.isEmpty())
		{
			throw new IllegalArgumentException("repoStorePath cannot be null or empty");
		}
		if(repos == null)
		{
			throw new IllegalArgumentException("repos cannot be null");
		}
		ObjectOutputStream oos = null;
		try
		{
			oos = new ObjectOutputStream(new FileOutputStream(repoStorePath, false));
			for (RepositoryInfo repo : repos)
			{
				oos.writeObject(repo);
			}
		}
		catch (IOException e)
		{
			throw new RepositoryException("Error writing repository information to file '%s': %s", repoStorePath, e.getMessage());
		}
		finally
		{
			try
			{
				oos.close();
			}
			catch(NullPointerException ex)
			{
				// ignore this catch, it was probably never opened or something
			}
			catch(IOException e)
			{
				throw new RepositoryException("Error closing repository information file '%s': %s", repoStorePath, e.getMessage());
			}
		}
	}

	public static List<RepositoryInfo> loadRepos(String repoStorePath)
	{
		if(repoStorePath == null || repoStorePath.isEmpty())
		{
			throw new IllegalArgumentException("repoStorePath cannot be null or empty");
		}
		ObjectInputStream ois = null;
		List<RepositoryInfo> repos = new ArrayList<RepositoryInfo>();
		try
		{
			ois = new ObjectInputStream(new FileInputStream(repoStorePath));
			try
			{
				while(true)
				{
					RepositoryInfo repo = (RepositoryInfo) ois.readObject();
					repos.add(repo);
				}
			}
			catch(EOFException e)
			{
				//ignore this exception, it just means its time to quit the loop
			}
		}
		catch (IOException|ClassNotFoundException e)
		{
			// TODO: log this => ("Error reading repository information to file '%s': %s", getRepositoryStorePath(), e.getMessage());
			repos = new ArrayList<RepositoryInfo>(0);
		}
		finally
		{
			try
			{
				ois.close();
			}
			catch(NullPointerException ex)
			{
				// ignore this catch, it was probably never opened or something
			}
			catch(IOException e)
			{
				// TODO: log this => ("Error closing repository information file '%s': %s", getRepositoryStorePath(), e.getMessage());
			}
		}
		return repos;
	}

	public Map<Integer, RepositoryInfo> getRepoMap()
	{
		return repoMap;
	}

}
