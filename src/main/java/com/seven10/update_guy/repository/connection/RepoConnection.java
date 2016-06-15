package com.seven10.update_guy.repository.connection;

import java.nio.file.Path;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestEntry;

public interface RepoConnection
{
	public void connect() throws RepositoryException;
	public void disconnect() throws RepositoryException;
	public Manifest getManifest(String releaseFamily) throws RepositoryException;
	public void downloadRelease(ManifestEntry versionEntry) throws RepositoryException;
	public Path getCachePath();
}
