package com.seven10.update_guy.release;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javax.ws.rs.core.Response.Status;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.connection.RepoConnection;
import com.seven10.update_guy.repository.connection.RepoConnectionFactory;

public class ReleaseMgr
{	
	private final Manifest releaseFamily;
	private final RepositoryInfo repoInfo;

	
	/**
	 * Constructor for the CacheManager, a class used to handle the serving of locally cached files to consumers
	 * @param releaseId 
	 */
	public ReleaseMgr(Manifest releaseFamily, RepositoryInfo repoInfo)
	{
		if( releaseFamily == null)
		{
			throw new IllegalArgumentException("releaseFamily must not be null");
		}
		if( repoInfo == null)
		{
			throw new IllegalArgumentException("repoInfo must not be null");
		}
		this.releaseFamily = releaseFamily ;
		this.repoInfo = repoInfo;
	}


	/**
	 * Retrieves a File object that references the file that provides the role desired
	 * @param roleName The name of the role the desired file provides
	 * @return a File object pointing to the file
	 * @throws RepositoryException Thrown if the role cannot be found
	 */
	public File getFileForRole(String version, String roleName) throws RepositoryException
	{
		if(roleName == null || roleName.isEmpty())
		{
			throw new IllegalArgumentException("roleName must not be null or empty");
		}
		ManifestEntry activeVersion = releaseFamily.getVersionEntry(version);
		if( activeVersion.getRoles().contains(roleName))
		{
			return activeVersion.getPath(roleName).toFile();
		}
		else
		{
			throw new RepositoryException(Status.NOT_FOUND, "Could not find file for role name '%s'", roleName);
		}
	}

	public void cacheFiles(String version, Consumer<java.nio.file.Path> onFileComplete, Runnable onDownloadComplete) throws RepositoryException
	{
		if(version == null || version.isEmpty())
		{
			throw new IllegalArgumentException("version must not be null or empty");
		}
		if(onFileComplete == null)
		{
			throw new IllegalArgumentException("onFileComplete must not be null");
		}
		if(onDownloadComplete == null)
		{
			throw new IllegalArgumentException("onDownloadComplete must not be null");
		}
		ManifestEntry activeVersion = releaseFamily.getVersionEntry(version);
		// create repo connection
		RepoConnection repoConnection = RepoConnectionFactory.connect(repoInfo);		
		repoConnection.downloadRelease(activeVersion, onFileComplete);
		onDownloadComplete.run();
	}

	public List<String> getAllRoles(String version) throws RepositoryException
	{
		if(version == null || version.isEmpty())
		{
			throw new IllegalArgumentException("version must not be null or empty");
		}
		ManifestEntry activeVersion = releaseFamily.getVersionEntry(version);
		return activeVersion.getRoles();
	}

}
