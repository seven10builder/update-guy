package com.seven10.update_guy.repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
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
		RepositoryInfo[] repos = loadRepos(repositoryStorePath);
		for (RepositoryInfo repo : repos)
		{
			repoMap.put(repo.hashCode(), repo);
		}
	}

	public void addRepository(RepositoryInfo repoInfo) throws RepositoryException
	{
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
		ObjectOutputStream oos = null;
		try
		{
			oos = new ObjectOutputStream(new FileOutputStream(repoStorePath));
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

	public static RepositoryInfo[] loadRepos(String repoStorePath)
	{
		ObjectInputStream ois = null;
		RepositoryInfo[] repos;
		try
		{
			ois = new ObjectInputStream(new FileInputStream(repoStorePath));
			repos = (RepositoryInfo[]) ois.readObject();
		}
		catch (IOException|ClassNotFoundException e)
		{
			// TODO: log this => ("Error reading repository information to file '%s': %s", getRepositoryStorePath(), e.getMessage());
			repos = new RepositoryInfo[0];
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
