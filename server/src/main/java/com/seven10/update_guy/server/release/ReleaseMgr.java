package com.seven10.update_guy.server.release;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.net.io.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.common.release_family.UpdateGuyRole;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.connection.RepoConnection;
import com.seven10.update_guy.server.repository.connection.RepoConnectionFactory;

public class ReleaseMgr
{
	private static final Logger logger = LogManager.getFormatterLogger(ReleaseMgr.class);
	private final ReleaseFamily releaseFamily;
	private final RepositoryInfo repoInfo;

	/**
	 * Constructor for the CacheManager, a class used to handle the serving of
	 * locally cached files to consumers
	 * 
	 * @param releaseId
	 */
	public ReleaseMgr(ReleaseFamily releaseFamily, RepositoryInfo repoInfo)
	{
		if (releaseFamily == null)
		{
			throw new IllegalArgumentException("releaseFamily must not be null");
		}
		if (repoInfo == null)
		{
			throw new IllegalArgumentException("repoInfo must not be null");
		}
		this.releaseFamily = releaseFamily;
		this.repoInfo = repoInfo;
	}

	/**
	 * Retrieves the role info object that references the file that provides the
	 * role desired
	 * 
	 * @param roleName
	 *            The name of the role the desired file provides
	 * @return a File object pointing to the file
	 * @throws RepositoryException
	 *             Thrown if the role cannot be found
	 */
	public UpdateGuyRole getRoleInfoForRole(String version, String roleName) throws RepositoryException
	{
		if (roleName == null || roleName.isEmpty())
		{
			throw new IllegalArgumentException("roleName must not be null or empty");
		}
		ReleaseFamilyEntry activeVersion;
		try
		{
			activeVersion = releaseFamily.getVersionEntry(version);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getFileForRole(): error getting version entry for '%s'. Reason: %s", version,
					ex.getMessage());
			throw new RepositoryException(Status.NOT_FOUND, "Could not find file for role name '%s'", roleName);
		}
		if (activeVersion.getRoles().contains(roleName))
		{
			return activeVersion.getRoleInfo(roleName);
		}
		else
		{
			throw new RepositoryException(Status.NOT_FOUND, "Could not find file for role name '%s'", roleName);
		}
	}

	public void cacheFiles(String version, Consumer<java.nio.file.Path> onFileComplete, Runnable onDownloadComplete)
			throws RepositoryException
	{
		if (version == null || version.isEmpty())
		{
			throw new IllegalArgumentException("version must not be null or empty");
		}
		if (onFileComplete == null)
		{
			throw new IllegalArgumentException("onFileComplete must not be null");
		}
		if (onDownloadComplete == null)
		{
			throw new IllegalArgumentException("onDownloadComplete must not be null");
		}

		ReleaseFamilyEntry activeVersion;
		try
		{
			activeVersion = releaseFamily.getVersionEntry(version);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".cacheFiles(): Could not get version entry '%s'. Reason: %s", version, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not get version entry '%s'", version);
		}
		// create repo connection
		RepoConnection repoConnection = RepoConnectionFactory.connect(repoInfo);
		repoConnection.downloadRelease(activeVersion, onFileComplete);
		onDownloadComplete.run();
	}

	public List<String> getAllRoles(String version) throws RepositoryException
	{
		if (version == null || version.isEmpty())
		{
			throw new IllegalArgumentException("version must not be null or empty");
		}
		try
		{
			ReleaseFamilyEntry activeVersion = releaseFamily.getVersionEntry(version);
			return activeVersion.getRoles();
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getAllRoles(): could not get version entry '%s'. Reason: %s", version, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not get version Entry '%s'", version);
		}
	}

	public void addRelease(String version) throws RepositoryException
	{
		boolean releaseExists = releaseFamily.getVersionEntries().stream()
				.anyMatch(ve -> ve.getVersion().compareTo(version) == 0);
		if (releaseExists)
		{
			// already exists, throw an error
			logger.error(".addRelease(): version entry '%s' cannot be added. Already exists", version);
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not add version Entry '%s'", version);
		}
		ReleaseFamilyEntry releaseFamilyEntry = new ReleaseFamilyEntry();
		releaseFamilyEntry.setVersion(version);
		releaseFamilyEntry.setReleaseFamily(releaseFamily.getReleaseFamily());
		releaseFamilyEntry.setPublishDate(new Date());
		releaseFamily.addVersionEntry(releaseFamilyEntry);

	}

	public void addRoleInfo(String version, String roleName, UpdateGuyRole updateGuyRole) throws RepositoryException
	{
		ReleaseFamilyEntry versionEntry = releaseFamily.getVersionEntries().stream()
				.filter(ve -> ve.getVersion().compareTo(version) == 0).distinct().findFirst().get();
		versionEntry.addRoleInfo(roleName, updateGuyRole);

	}

	public void uploadFile(InputStream uploadedInputStream, Path uploadedFileLocation) throws RepositoryException
	{
		try(OutputStream out = new FileOutputStream(uploadedFileLocation.toFile()))
		{
			Util.copyStream(uploadedInputStream, out);
			out.flush();
		}
		catch (IOException e)
		{
			logger.error(".addRelease(): could not write file to path '%s': Reason: %s", uploadedFileLocation, e.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not store uploaded file");
		}

	}

}
