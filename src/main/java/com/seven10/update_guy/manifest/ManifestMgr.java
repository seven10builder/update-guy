package com.seven10.update_guy.manifest;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.exceptions.RepositoryException;

public class ManifestMgr
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestMgr.class);
	
	/**
	 * Used in a steam map to produce a Manifest from a given path
	 * 
	 * @param path
	 *            a path to the manifest file
	 * @return The manifest or NULL if the manifest file could not be opened
	 */
	private static Manifest convertPathToManifest(Path path)
	{
		Manifest manifest = null;
		try
		{
			logger.trace("Loading manifest file '%s'", path);
			manifest = Manifest.loadFromFile(path);
		}
		catch (RepositoryException e)
		{
			logger.error("Could not convert file '%s' to manifest object. Reason: %s", path, e.getMessage());
		}
		return manifest;
	}

	private final Path manifestPath;
	
	/**
	 * Generates a path for the local copy of the manifest based on the family
	 * name
	 * 
	 * @param releaseFamily
	 *            The release family name that identifies the manifest to open
	 * @return A path to the local copy of the manifest
	 */
	private Path getLocalManifestFilePath(String releaseFamily)
	{
		String manifestFileName = String.format("%s.manifest", releaseFamily);
		Path manifestFile = manifestPath.resolve(manifestFileName);
		return manifestFile;
	}
	
	
	public ManifestMgr(Path manifestPath)
	{
		if( manifestPath == null)
		{
			throw new IllegalArgumentException("manifestPath must not be null");
		}
		if(Files.exists(manifestPath) == false)
		{
			manifestPath.toFile().mkdirs();
		}
		this.manifestPath = manifestPath;
	}
	
	
	/**
	 * Retrieves an instance of the manifest identified by the release family.
	 * This method loads the file from disk
	 * 
	 * @param releaseFamily
	 *            The identifier for the release family to load the manifest for
	 * @return The manifest object
	 * @throws RepositoryException
	 *             Thrown if the file could not be loaded
	 */
	public Manifest getManifest(String releaseFamily) throws RepositoryException
	{
		if (releaseFamily == null || releaseFamily.isEmpty())
		{
			throw new IllegalArgumentException("releaseFamily must not be null or empty");
		}
		Path manifestFile = getLocalManifestFilePath(releaseFamily);
		return Manifest.loadFromFile(manifestFile);
	}
	
	/**
	 * Retrieves the list of available manifests in the local cache
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public List<Manifest> getManifests() throws RepositoryException
	{
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.manifest");
		
		List<Manifest> files;
		if (Files.exists(manifestPath))
		{
			try
			{
				files = Files.walk(manifestPath).filter(Files::isRegularFile)
						.filter(path -> matcher.matches(path))
						.map(path -> convertPathToManifest(path))
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
			}
			catch (IOException ex)
			{
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not walk path '%s'. Reason: %s",
						manifestPath, ex.getMessage());
			}
		}
		else
		{
			logger.warn(".getManifests(): manifest folder '%s' does not exist", manifestPath.toString());
			files = new ArrayList<Manifest>();
		}
		return files;
		
	}
	
}
