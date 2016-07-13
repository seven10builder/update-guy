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
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.exceptions.RepositoryException;

class LocalRepoConnection implements RepoConnection
{
	private static final Logger logger = LogManager.getFormatterLogger(LocalRepoConnection.class);
	private final Path repoPath;
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
		repoPath = activeRepo.getRemoteManifestPath();
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
	public Manifest getManifest(String releaseFamily) throws RepositoryException
	{
		if(releaseFamily==null || releaseFamily.isEmpty())
		{
			throw new IllegalArgumentException("releaseFamily must not be null or emptyu");
		}
		Path filePath = repoPath.resolve(String.format("%s.manifest", releaseFamily));
		try
		{
			Manifest manifest = Manifest.loadFromFile(filePath);
			return manifest;
		}
		catch(UpdateGuyException ex)
		{
			logger.error(".getManifest(): could not load Manifest from path '%s'. Reason: %s", filePath, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not load manifest '%s'", filePath.getFileName().toString());
		}
	}
	@Override
	public void downloadRelease(ManifestEntry versionEntry, Consumer<Path> onFileComplete) throws RepositoryException
	{
		if(versionEntry==null)
		{
			throw new IllegalArgumentException("versionEntry must not be null");
		}
		if(onFileComplete==null)
		{
			throw new IllegalArgumentException("onFileComplete must not be null");
		}
		for(Entry<String, Path> roleEntry: versionEntry.getAllRolePaths())
		{
			Path srcPath = roleEntry.getValue();
			Path destPath;
			try
			{
				destPath = Globals.buildDownloadTargetPath(repoId, versionEntry, roleEntry);
			}
			catch(UpdateGuyException ex)
			{
				logger.error(".getManifest(): could not build target download path. Reason: %s", ex.getMessage());
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not build target download path");
			}
			
			copyFileToPath(srcPath, destPath);
			onFileComplete.accept(destPath);
		}
	}
	
	@Override
	public List<String> getFileNames(Path targetDir) throws RepositoryException
	{
		if(targetDir == null)
		{
			throw new IllegalArgumentException("targetDir must not be null");
		}
		if(Files.exists(targetDir) == false)
		{
			logger.error(".getFileNames(): target directory '%s' does not exist", targetDir.toString());
			throw new RepositoryException(Status.NOT_FOUND, "Could not find target directory");
		}
		try
		{
			logger.info(".getFileNames(): attempting to walk path '%s'", targetDir);
			List<String> files = Files.walk(targetDir).filter(Files::isRegularFile)
					.filter(Objects::nonNull)
					.map(file->file.getFileName().toString())
					.collect(Collectors.toList());
			logger.debug(".getFileNames(): results from walk of path '%s' - %s", targetDir, String.join(", ", files) );
			return files;
		}
		catch (IOException ex)
		{
			logger.error(".getFileNames(): could not walk directory '%s' - %s", targetDir.toString(), ex.getMessage() );
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
		result = prime * result + ((repoPath == null) ? 0 : repoPath.hashCode());
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
		if (repoPath == null)
		{
			if (other.repoPath != null)
			{
				return false;
			}
		}
		else if (!repoPath.equals(other.repoPath))
		{
			return false;
		}
		return true;
	}
	
}
