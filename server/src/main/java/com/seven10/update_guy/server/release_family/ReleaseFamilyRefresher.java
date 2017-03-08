package com.seven10.update_guy.server.release_family;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfoMgr;
import com.seven10.update_guy.server.repository.connection.RepoConnection;
import com.seven10.update_guy.server.repository.connection.RepoConnectionFactory;

public class ReleaseFamilyRefresher
{
	public interface FileCreator
	{
		void run(Path path) throws IOException;
	}
	private static final Logger logger = LogManager.getFormatterLogger(ReleaseFamilyRefresher.class);
	private final String repoId;
	private final Path destReleaseFamilyPath;
	
	/**
	 * Refreshes a release family from the repository to the local server cache
	 * @param repoId The id of the repo this release family refresher is associated with
	 * @param destReleaseFamilyPath the folder path of the local server cache
	 */
	public ReleaseFamilyRefresher(String repoId, Path destReleaseFamilyPath)
	{
		if(Strings.isBlank(repoId))
		{
			throw new IllegalArgumentException("repoId must not be null or empty");
		}
		if(destReleaseFamilyPath == null)
		{
			throw new IllegalArgumentException("destReleaseFamilyPath must not be null or empty");
		}
		this.repoId = repoId;
		this.destReleaseFamilyPath = destReleaseFamilyPath;
	}
	
	public void refreshLocalReleaseFamilyFiles(String releaseFamilyName) throws RepositoryException
	{
		if (Strings.isBlank(releaseFamilyName))
		{
			throw new IllegalArgumentException("releaseFamily must not be null or empty");
		}
		logger.info(".refreshLocalReleaseFamilyFiles(): attempting to refresh local copy of release family");
		RepoConnection repoConnection = createRepoConnectionForId();
		ReleaseFamily releaseFamily = repoConnection.getReleaseFamily(releaseFamilyName);
		Path destManifetsFile = getDestinationPath().resolve(Globals.buildRelFamFileName(releaseFamilyName));
		try
		{
			ReleaseFamily.writeToFile(destManifetsFile, releaseFamily);
			logger.info(".refreshLocalReleaseFamilyFiles(): wrote release family '%s' to path '%s'", releaseFamily, destManifetsFile);
		}
		catch (IOException ex)
		{
			logger.error(".refreshLocalReleaseFamilyFiles(): could not write release family '%s' to file '%s' - %s",
					releaseFamily.toString(), destReleaseFamilyPath, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not update local release family");
		}
	}
	public Path getDestinationPath()
	{
		return destReleaseFamilyPath;
	}
	public String getRepoId()
	{
		return repoId;
	}
	public RepoConnection createRepoConnectionForId() throws RepositoryException
	{
		RepositoryInfo repoInfo = RepositoryInfoMgr.loadRepo(ServerGlobals.getRepoFile(), getRepoId());
		RepoConnection repoConnection = RepoConnectionFactory.connect(repoInfo);
		return repoConnection;
	}

	
	public void updateReleaseFamilyList(FileCreator fileCreator) throws RepositoryException
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
				logger.error(".updateReleaseFamilyList(): could not create release family stub at path file '%s' - %s",
						destReleaseFamilyPath, ex.getMessage());
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not update local release family");
			}
		}
	}
}
