package com.seven10.update_guy.repository.connection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

class LocalRepoConnection implements RepoConnection
{
	
	private final Path repoPath;
	private final Path cachePath;
	
	private void copyFileToPath(Path srcPath, Path destPath) throws RepositoryException
	{
		try
		{
			FileUtils.copyFile(	srcPath.toFile(), destPath.toFile());
		}
		catch (IOException e)
		{
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR,"Could not copy file '%s' to '%s'. Reason: %s", srcPath, destPath, e.getMessage());
		}
	}
	public LocalRepoConnection(RepositoryInfo activeRepo)
	{
		repoPath = activeRepo.getmanifestPath();
		cachePath = activeRepo.getCachePath();
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
		Manifest manifest = Manifest.loadFromFile(filePath);
		return manifest;
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
		for(Entry<String, Path> entry: versionEntry.getAllRolePaths())
		{
			Path srcPath = entry.getValue();
			Path destPath = cachePath.resolve(srcPath.getFileName());
			copyFileToPath(srcPath, destPath);
			onFileComplete.accept(destPath);
		}
	}
	@Override
	public Path getCachePath()
	{
		return cachePath;
	}
}
