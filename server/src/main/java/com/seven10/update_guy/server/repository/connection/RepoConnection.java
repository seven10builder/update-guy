package com.seven10.update_guy.server.repository.connection;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.server.exceptions.RepositoryException;

public interface RepoConnection
{
	/**
	 * Connects to the repository and enables communication
	 * @throws RepositoryException if connection cannot be made
	 */
	public void connect() throws RepositoryException;
	/**
	 * Disconnects from the repository and cleans up any resources
	 * @throws RepositoryException if there is an error cleaning up resources
	 */
	public void disconnect() throws RepositoryException;
	/**
	 * Retrieves the Release Family File specified by the release family given
	 * @param releaseFamily The release family of the release family to retrieve
	 * @return the release family
	 * @throws RepositoryException If the release family cannot be found
	 */
	public ReleaseFamily getReleaseFamily(String releaseFamily) throws RepositoryException;
	/**
	 * Downloads all files from the repo to the local cache for the given version entry
	 * @param versionEntry The release family entry for the target version
	 * @param onFileComplete method to call when each file has been downloaded
	 * @throws RepositoryException Thrown if the file cannot be downloaded
	 */
	public void downloadRelease(ReleaseFamilyEntry versionEntry, Consumer<Path> onFileComplete) throws RepositoryException;
	/**
	 * Retrieves a list of all files in the path on the given repo
	 * @return The list of files
	 * @throws RepositoryException thrown if targetDir does not exist
	 */
	public List<String> getFileNames() throws RepositoryException;
	@Override
	public int hashCode();
	@Override
	public boolean equals(Object obj);
}
