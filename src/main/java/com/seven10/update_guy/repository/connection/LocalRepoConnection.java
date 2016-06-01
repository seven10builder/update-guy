package com.seven10.update_guy.repository.connection;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

class LocalRepoConnection implements RepoConnection
{
	
	private final String repoPath;
	private String cachePath;
	
	private void copyFileToPath(Path srcPath, Path destPath) throws RepositoryException
	{
		try
		{
			FileUtils.copyFile(	srcPath.toFile(), destPath.toFile());
		}
		catch (IOException e)
		{
			throw new RepositoryException("Could not copy file '%s' to '%s'. Reason: %s", srcPath, destPath, e.getMessage());
		}
	}
	public LocalRepoConnection(RepositoryInfo activeRepo)
	{
		repoPath = activeRepo.manifestPath;
		cachePath = activeRepo.cachePath;
	}
	@Override
	public void connect() throws RepositoryException
	{
		return;// already connected!
	}
	@Override
	public void disconnect() throws RepositoryException
	{
		return;// no need to disconnect!
	}
	@Override
	public Manifest downloadManifest(String releaseFamily) throws RepositoryException
	{
		if(releaseFamily==null || releaseFamily.isEmpty())
		{
			throw new IllegalArgumentException("releaseFamily must not be null or emptyu");
		}
		String fileName = String.format("%s.manifest", releaseFamily);
		Path filePath = Paths.get(repoPath, fileName);
		return Manifest.loadFromFile(filePath);
	}
	@Override
	public void downloadRelease(ManifestVersionEntry versionEntry) throws RepositoryException
	{
		if(versionEntry==null)
		{
			throw new IllegalArgumentException("versionEntry must not be null");
		}
		for(Entry<String, Path> entry: versionEntry.getAllPaths())
		{
			Path srcPath = entry.getValue();
			Path destPath = Paths.get(cachePath).resolve(srcPath.getFileName());
			copyFileToPath(srcPath, destPath);
		}
	}
}
