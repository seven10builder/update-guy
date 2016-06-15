package com.seven10.update_guy.release;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.connection.RepoConnection;

public class CacheManager
{	
	private ManifestEntry activeVersion;
	private Manifest manifest;
	private RepoConnection repoConnection;

	private Thread createDownloadThread(final AsyncResponse asyncResponse, ManifestEntry newEntry)
	{
		return new Thread(new Runnable()
		{
            @Override
            public void run() 
            {
            	try
            	{
            		repoConnection.downloadRelease(newEntry);
            		activeVersion = newEntry;
            		String json = GsonFactory.getGson().toJson(activeVersion);
            		Response resp = Response.ok().entity(json).build();
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
	
	public CacheManager(RepoConnection repoConnection, String releaseFamily, String activeVersion) throws RepositoryException
	{
		this.repoConnection = repoConnection;
		this.manifest = repoConnection.getManifest(releaseFamily);
		this.activeVersion = manifest.getVersionEntry(activeVersion);
	}

	/**
	 * Sets the active version to the entry identified by the parameter
	 * @param version The name of the version entry to use
	 * @throws RepositoryException If the entry does not exist
	 */
	public void setActiveVersion(String version, final AsyncResponse asyncResponse) throws RepositoryException
	{
		// get new manifestVersionEntry from version
		ManifestEntry newEntry =  manifest.getVersionEntry(version);
		Thread downloadThread = createDownloadThread(asyncResponse, newEntry);
		downloadThread.start();
	}

	/**
	 * Retrieves the name of the active version
	 * @return the name of the active version
	 */
	public String getActiveVersion()
	{
		return activeVersion.getVersion();
	}

	/**
	 * Lists all of the roles that are hosted by the active version
	 * @return the list of role names
	 */
	public List<String> getAvailableRoles()
	{
		return activeVersion.getRoles();
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
			final InputStream fis = new AutoCloseInputStream(new FileInputStream(file));
			return new String(Hex.encodeHex(DigestUtils.md5(fis)));
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
			return activeVersion.getPath(roleName).toFile();
		}
		else
		{
			throw new RepositoryException(Status.NOT_FOUND, "Could not find file for role name '%s'", roleName);
		}
	}

}
