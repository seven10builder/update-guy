package com.seven10.update_guy.server.manifest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfoMgr;
import com.seven10.update_guy.server.repository.connection.RepoConnection;
import com.seven10.update_guy.server.repository.connection.RepoConnectionFactory;

public class ManifestRefresher
{
	public interface FileCreator
	{
		void run(Path path) throws IOException;
	}
	private static final Logger logger = LogManager.getFormatterLogger(ManifestRefresher.class);
	private final String repoId;
	private final Path destManifestPath;
	
	/**
	 * Refreshes a manifest from the repository to the local server cache
	 * @param repoId The id of the repo this manifest refresher is associated with
	 * @param destManifestPath the folder path of the local server cache
	 */
	public ManifestRefresher(String repoId, Path destManifestPath)
	{
		if(Strings.isBlank(repoId))
		{
			throw new IllegalArgumentException("repoId must not be null or empty");
		}
		if(destManifestPath == null)
		{
			throw new IllegalArgumentException("destManifestPath must not be null or empty");
		}
		this.repoId = repoId;
		this.destManifestPath = destManifestPath;
	}
	
	public void refreshLocalManifest(String releaseFamily) throws RepositoryException
	{
		if (Strings.isBlank(releaseFamily))
		{
			throw new IllegalArgumentException("releaseFamily must not be null or empty");
		}
		logger.info(".refreshLocalManifest(): attempting to refresh local copy of manifest");
		RepoConnection repoConnection = createRepoConnectionForId();
		Manifest manifest = repoConnection.getManifest(releaseFamily);
		Path destManifetsFile = getDestinationPath().resolve(String.format("%s.manifest", releaseFamily));
		try
		{
			Manifest.writeToFile(destManifetsFile, manifest);
			logger.info(".refreshLocalManifest(): wrote manifest '%s' to path '%s'", manifest, destManifetsFile);
		}
		catch (IOException ex)
		{
			logger.error(".refreshLocalManifest(): could not write manifest '%s' to file '%s' - %s",
					manifest.toString(), destManifestPath, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not update local manifest");
		}
	}
	public Path getDestinationPath()
	{
		return destManifestPath;
	}
	public String getRepoId()
	{
		return repoId;
	}
	public RepoConnection createRepoConnectionForId() throws RepositoryException
	{
		RepositoryInfo repoInfo = RepositoryInfoMgr.loadRepo(Globals.getRepoFile(), getRepoId());
		RepoConnection repoConnection = RepoConnectionFactory.connect(repoInfo);
		return repoConnection;
	}

	
	public void updateManifestNameList(FileCreator fileCreator) throws RepositoryException
	{
		RepoConnection repoConnection = createRepoConnectionForId();
		List<String> fileNames = repoConnection.getFileNames();	
		for(String fileName: fileNames)
		{
			Path path = getDestinationPath().resolve(fileName);
			try
			{
				fileCreator.run(path);
			}
			catch (IOException ex)
			{
				logger.error(".refreshLocalManifest(): could not create manifest stub at path file '%s' - %s",
						destManifestPath, ex.getMessage());
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not update local manifest");
			}
		}
	}
}
