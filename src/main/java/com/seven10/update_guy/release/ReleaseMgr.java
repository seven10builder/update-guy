package com.seven10.update_guy.release;

import java.io.File;
import java.util.List;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.connection.RepoConnection;
import com.seven10.update_guy.repository.connection.RepoConnectionFactory;

public class ReleaseMgr
{	
	private final ManifestEntry activeVersion;
	private final RepositoryInfo repoInfo;

	private Thread createDownloadThread(final AsyncResponse asyncResponse, RepoConnection repoConnection, final ManifestEntry activeVersion)
	{
		return new Thread(new Runnable()
		{
            @Override
            public void run() 
            {
            	try
            	{
            		repoConnection.downloadRelease(activeVersion);
            		Response resp = Response.ok().build();
            		asyncResponse.resume(resp);
            	}
            	catch(RepositoryException ex)
            	{
            		asyncResponse.resume(ex);
            		return;
            	}
            }
        });
	}
	
	/**
	 * Constructor for the CacheManager, a class used to handle the serving of locally cached files to consumers
	 * @param releaseId 
	 */
	public ReleaseMgr(ManifestEntry activeVersion, RepositoryInfo repoInfo)
	{
		if( activeVersion == null)
		{
			throw new IllegalArgumentException("activeVersion must not be null");
		}
		if( repoInfo == null)
		{
			throw new IllegalArgumentException("repoInfo must not be null");
		}
		this.activeVersion = activeVersion ;
		this.repoInfo = repoInfo;
	}


	/**
	 * Retrieves a File object that references the file that provides the role desired
	 * @param roleName The name of the role the desired file provides
	 * @return a File object pointing to the file
	 * @throws RepositoryException Thrown if the role cannot be found
	 */
	public File getFileForRole(String roleName) throws RepositoryException
	{
		if(roleName == null || roleName.isEmpty())
		{
			throw new IllegalArgumentException("roleName must not be null or empty");
		}
		if( activeVersion.getRoles().contains(roleName))
		{
			return activeVersion.getPath(roleName).toFile();
		}
		else
		{
			throw new RepositoryException(Status.NOT_FOUND, "Could not find file for role name '%s'", roleName);
		}
	}

	public void cacheFiles(final AsyncResponse asyncResponse) throws RepositoryException
	{
		// create repo connection
		RepoConnection repoConnection = RepoConnectionFactory.connect(repoInfo);		
		// create download thread
		Thread downloadThread = createDownloadThread(asyncResponse, repoConnection, activeVersion);
		// start thread
		downloadThread.start();
	}

	public List<String> getAllRoles()
	{
		return activeVersion.getRoles();
	}

}
