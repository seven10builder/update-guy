package com.seven10.update_guy.server.repository.connection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.exceptions.UpdateGuyNotFoundException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.common.release_family.UpdateGuyRole;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;

class LocalRepoConnection implements RepoConnection
{
	private static final Logger logger = LogManager.getFormatterLogger(LocalRepoConnection.class);
	private final Path releaseFamilyPath;
	private final String repoId;
	
	private void copyFileToPath(Path srcPath, Path destPath) throws RepositoryException
	{
		try
		{
			FileUtils.copyFile(	srcPath.toFile(), destPath.toFile());
		}
		catch (IOException e)
		{
			logger.error(".copyFileToPath(): Could not copy file '%s' to '%s'. Reason: %s", srcPath, destPath, e.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Error copying file");
		}
	}
	public LocalRepoConnection(RepositoryInfo activeRepo) throws RepositoryException
	{
		releaseFamilyPath = activeRepo.getRemoteReleaseFamilyPath();
		repoId = activeRepo.getShaHash();
	}
	@Override
	public void connect() throws RepositoryException
	{
		//  no need to connect
	}
	@Override
	public void disconnect() throws RepositoryException
	{
		return;// no need to disconnect!
	}
	@Override
	public ReleaseFamily getReleaseFamily(String releaseFamilyName) throws RepositoryException
	{
		if(releaseFamilyName==null || releaseFamilyName.isEmpty())
		{
			throw new IllegalArgumentException("releaseFamily must not be null or empty");
		}
		Path filePath = releaseFamilyPath.resolve(Globals.buildRelFamFileName(releaseFamilyName));
		try
		{
			ReleaseFamily releaseFamily = ReleaseFamily.loadFromFile(filePath);
			return releaseFamily;
		}
		
		catch(UpdateGuyException ex)
		{
			logger.error(".getReleaseFamily(): could not load release family from path '%s'. Reason: %s", filePath, ex.getMessage());
			
			Status errorCode = (ex instanceof UpdateGuyNotFoundException) ? Status.NOT_FOUND : Status.INTERNAL_SERVER_ERROR;
			throw new RepositoryException(errorCode, "could not load release family '%s'", filePath.getFileName().toString());
		}
	}
	@Override
	public void downloadRelease(ReleaseFamilyEntry versionEntry, Consumer<Path> onFileComplete) throws RepositoryException
	{
		if(versionEntry==null)
		{
			throw new IllegalArgumentException("versionEntry must not be null");
		}
		if(onFileComplete==null)
		{
			throw new IllegalArgumentException("onFileComplete must not be null");
		}
		for(Entry<String, UpdateGuyRole> roleEntry: versionEntry.getAllRoleInfos())
		{
			UpdateGuyRole srcPath = roleEntry.getValue();
			Path destPath;
			try
			{
				destPath = ServerGlobals.buildDownloadTargetPath(repoId, versionEntry, roleEntry);
			}
			catch(UpdateGuyException ex)
			{
				logger.error(".getReleaseFamily(): could not build target download path. Reason: %s", ex.getMessage());
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not build target download path");
			}
			
			copyFileToPath(srcPath.getFilePath(), destPath);
			onFileComplete.accept(destPath);
		}
	}
	
	@Override
	public List<String> getFileNames() throws RepositoryException
	{
		if(Files.exists(releaseFamilyPath) == false)
		{
			logger.error(".getFileNames(): target directory '%s' does not exist", releaseFamilyPath.toString());
			throw new RepositoryException(Status.NOT_FOUND, "Could not find target directory");
		}
		try
		{
			logger.info(".getFileNames(): attempting to walk path '%s'", releaseFamilyPath);
			List<String> files = Files.walk(releaseFamilyPath).filter(Files::isRegularFile)
					.filter(Objects::nonNull)
					.map(file->file.getFileName().toString())
					.collect(Collectors.toList());
			logger.debug(".getFileNames(): results from walk of path '%s' - %s", releaseFamilyPath, String.join(", ", files) );
			return files;
		}
		catch (IOException ex)
		{
			logger.error(".getFileNames(): could not walk directory '%s' - %s", releaseFamilyPath.toString(), ex.getMessage() );
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not walk remote repo directory");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repoId == null) ? 0 : repoId.hashCode());
		result = prime * result + ((releaseFamilyPath == null) ? 0 : releaseFamilyPath.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof LocalRepoConnection))
		{
			return false;
		}
		LocalRepoConnection other = (LocalRepoConnection) obj;
		if (repoId == null)
		{
			if (other.repoId != null)
			{
				return false;
			}
		}
		else if (!repoId.equals(other.repoId))
		{
			return false;
		}
		if (releaseFamilyPath == null)
		{
			if (other.releaseFamilyPath != null)
			{
				return false;
			}
		}
		else if (!releaseFamilyPath.equals(other.releaseFamilyPath))
		{
			return false;
		}
		return true;
	}
	
}
