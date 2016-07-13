package com.seven10.update_guy.server.manifest;

import java.io.FileNotFoundException;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.exceptions.UpdateGuyNotFoundException;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.server.exceptions.RepositoryException;


public class ManifestMgr
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestMgr.class);
	
	/**
	 * Used in a steam map to produce a Manifest from a given path
	 * 
	 * @param path
	 *            a path to the manifest file
	 * @return The manifest or NULL if the manifest file could not be opened
	 * @throws RepositoryException 
	 */
	private static Manifest convertPathToManifest(Path path)
	{
		Manifest manifest = null;
		try
		{
			logger.trace("Loading manifest file '%s'", path);
			manifest = Manifest.loadFromFile(path);
		}
		catch (UpdateGuyNotFoundException ex)
		{
			logger.error(".convertPathToManifest(): manifest file not found. Reason: %s", ex.getMessage());
		}
		catch(UpdateGuyException ex)
		{
			logger.error(".convertPathToManifest(): Could not convert file '%s' to manifest object. Reason: %s", path, ex.getMessage());
		}
		return manifest;
	}

	private final Path manifestPath;
	private ManifestRefresher manifestRefresher;
	
	private ManifestEntry getNewVersionEntry(String newVersion, ActiveVersionEncoder encoder) throws RepositoryException
	{
		// find newVersion in manifest
		ManifestEntry manifestEntry;
		try
		{
			manifestEntry = getManifest(encoder.getReleaseFamily()).getVersionEntry(newVersion);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getNewVersionEntry(): Could not get version entry for version key '%s'. Reason: %s", newVersion, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not get version entry for  '%s'", newVersion);
		}
		return manifestEntry;
	}
	
	private void writeActiveVersionStoreFile(String activeVersId, ActiveVersionEncoder encoder,
			ManifestEntry manifestEntry) throws RepositoryException
	{
		Path fileName = encoder.encodeFileName(activeVersId);
		// write activeVersion to fileName
		try
		{
			encoder.writeVersionEntry(fileName, manifestEntry);
		}
		catch (IOException ex)
		{
			logger.error(".writeActiveVersionStoreFile(): could not write active version entry file '%s'. Reason: %s",
					fileName, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not write active version entry file '%s'",
					fileName.getFileName().toString());
		}
	}
	
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
	
	
	public ManifestMgr(Path manifestPath, String repoId)
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
		this.manifestRefresher = new ManifestRefresher(repoId, manifestPath);
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
		try
		{
			manifestRefresher.refreshLocalManifest(releaseFamily);
			return Manifest.loadFromFile(manifestFile);
		}
		catch (UpdateGuyNotFoundException ex)
		{
			logger.error(".getManifest(): Could not find manifest for path '%s'. reason: %s", manifestFile, ex.getMessage());
			throw new RepositoryException(Status.NOT_FOUND, "Could not find manifest file for release family '%s'", releaseFamily);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getManifest(): Could not get manifest for path '%s'. reason: %s", manifestFile, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not get manifest for release family '%s'", releaseFamily);
		}
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
				logger.error(".getManifests(): Could not walk path '%s'. Reason: %s", manifestPath, ex.getMessage());
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not walk server path",
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

	public void setActiveVersion(String newVersion, String activeVersId, ActiveVersionEncoder encoder) throws RepositoryException
	{
		if(StringUtils.isBlank(newVersion))
		{
			throw new IllegalArgumentException("newVersion must not be null or empty");
		}
		if(StringUtils.isBlank(activeVersId))
		{
			throw new IllegalArgumentException("activeVersId must not be null or empty");
		}
		if(encoder == null)
		{
			throw new IllegalArgumentException("encoder must not be null");
		}
		ManifestEntry manifestEntry = getNewVersionEntry(newVersion, encoder);
		// encode fileName
		writeActiveVersionStoreFile(activeVersId, encoder, manifestEntry);
	}

	public ManifestEntry getActiveVersion(String activeVersId, ActiveVersionEncoder encoder) throws RepositoryException
	{
		if(StringUtils.isBlank(activeVersId))
		{
			throw new IllegalArgumentException("activeVersId must not be null or empty");
		}
		if(encoder == null)
		{
			throw new IllegalArgumentException("encoder must not be null");
		}
		// encode fileName
		Path fileName = encoder.encodeFileName(activeVersId);
		// load fileName as ManifestEntry
		try
		{
			return encoder.loadVersionEntry(fileName);
		}
		catch (FileNotFoundException ex)
		{
			logger.error(".getActiveVersion(): could not find active version entry for active version id '%s'. Reason: %s", activeVersId,
										ex.getMessage());
			throw new RepositoryException(Status.NOT_FOUND, "Could not find manifest file '%s'", fileName.getFileName().toString());

		}
		catch (IOException ex)
		{
			logger.error(".getActiveVersion(): Could not read active version entry file '%s'. Reason: %s", fileName, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not read active version entry");
		}
	}
}
