package com.seven10.update_guy.release;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.seven10.update_guy.FileFingerPrint;
import com.seven10.update_guy.Globals;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.repository.connection.RepoConnection;
import com.seven10.update_guy.repository.connection.RepoConnectionFactory;

public class CacheManager
{	

	private Globals globals;

	private Thread createDownloadThread(final AsyncResponse asyncResponse, RepoConnection repoConnection)
	{
		return new Thread(new Runnable()
		{
            @Override
            public void run() 
            {
            	try
            	{
            		repoConnection.downloadRelease(globals.getActiveVersion());
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
	 * @param manifestPath The path to the manifest FILE for a given release family
	 * @param cachePath The path where the various versions store the files to be served to consumers
	 * @param releaseFamily
	 * @param activeVersion
	 * @throws RepositoryException
	 */
	public CacheManager(Globals globals) throws RepositoryException
	{
		this.globals = globals;
	}



	/**
	 * Lists all of the roles that are hosted by the active version
	 * @return the list of role names
	 */
	public List<String> getAvailableRoles()
	{
		return globals.getActiveVersion().getRoles();
	}

	/**
	 * Retrieves a fingerprint of the file that fills the role specified
	 * @param roleName The name of the role the desired file provides
	 * @return A string representation of the fingerprint of the file
	 * @throws RepositoryException If the role does not exist or the file cannot be found
	 */
	public String getFingerPrintForRole(String roleName) throws RepositoryException
	{
		File file = getFileForRole(roleName);
		try
		{
			return FileFingerPrint.create(file);
		}
		catch(IOException ex)
		{
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Error retrieving fingerprint for file '%s'", file.getAbsolutePath());
		}
	}

	/**
	 * Retrieves a File object that references the file that provides the role desired
	 * @param roleName The name of the role the desired file provides
	 * @return a File object pointing to the file
	 * @throws RepositoryException Thrown if the role cannot be found
	 */
	public File getFileForRole(String roleName) throws RepositoryException
	{
		if( getAvailableRoles().contains(roleName))
		{
			return globals.getActiveVersion().getPath(roleName).toFile();
		}
		else
		{
			throw new RepositoryException(Status.NOT_FOUND, "Could not find file for role name '%s'", roleName);
		}
	}

	public void cacheFiles(final AsyncResponse asyncResponse) throws RepositoryException
	{
		// create repo connection
		RepoConnection repoConnection = RepoConnectionFactory.connect(globals.getRepoInfo());		
		// create download thread
		Thread downloadThread = createDownloadThread(asyncResponse, repoConnection);
		// start thread
		downloadThread.start();
	}

}
