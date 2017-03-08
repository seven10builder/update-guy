package com.seven10.update_guy.server.release_family;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.exceptions.UpdateGuyNotFoundException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.server.exceptions.RepositoryException;

public class ReleaseFamilyMgr
{
	private static final Logger logger = LogManager.getFormatterLogger(ReleaseFamilyMgr.class);
	
	private static void touchFile(Path path) throws IOException
	{
		try
		{
			Files.createFile(path);
			logger.info(".touchFile(): touched file '%s'", path);
		}
		catch(FileAlreadyExistsException ex)
		{
			logger.trace(".touchFile(): path '%s' already exists, ignoring", path);
			return;
		}
	}
	
	private final Path releaseFamilyPath;
	private ReleaseFamilyRefresher releaseFamilyRefresher;
	
	private ReleaseFamilyEntry getNewVersionEntry(String newVersion, ActiveVersionEncoder encoder) throws RepositoryException
	{
		// find newVersion in releaseFamily
		ReleaseFamilyEntry releaseFamilyEntry;
		try
		{
			releaseFamilyEntry = getReleaseFamily(encoder.getReleaseFamily()).getVersionEntry(newVersion);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getNewVersionEntry(): Could not get version entry for version key '%s'. Reason: %s", newVersion, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not get version entry for  '%s'", newVersion);
		}
		return releaseFamilyEntry;
	}
	
	private void writeActiveVersionStoreFile(String activeVersId, ActiveVersionEncoder encoder,
			ReleaseFamilyEntry releaseFamilyEntry) throws RepositoryException
	{
		Path fileName = encoder.encodeFileName(activeVersId);
		// write activeVersion to fileName
		try
		{
			encoder.writeVersionEntry(fileName, releaseFamilyEntry);
		}
		catch (IOException ex)
		{
			logger.error(".writeActiveVersionStoreFile(): could not write active version entry file '%s'. Reason: %s",
					fileName, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not write active version entry file '%s'",
					fileName.getFileName().toString());
		}
	}
	
	private ReleaseFamily doGetReleaseFamily(String releaseFamilyName, Path releaseFamilyFile) throws RepositoryException
	{
		ReleaseFamily releaseFamily = null;
		try
		{
			releaseFamilyRefresher.refreshLocalReleaseFamilyFiles(releaseFamilyName);
			logger.trace(".doGetReleaseFamily(): Loading release family file '%s'", releaseFamilyFile);
			releaseFamily = ReleaseFamily.loadFromFile(releaseFamilyFile);
		}
		catch (UpdateGuyNotFoundException ex)
		{
			logger.error(".doGetReleaseFamily(): release family file not found. Reason: %s", ex.getMessage());
			throw new RepositoryException(Status.NOT_FOUND, "Could not find release family file for release family");
		}
		catch(UpdateGuyException ex)
		{
			logger.error(".doGetReleaseFamily(): Could not convert file '%s' to release family object. Reason: %s", releaseFamilyFile, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not get release family for release family");
		}
		return releaseFamily;
	}
	/**
	 * Constructor for ReleaseFamilyMgr
	 * @param releaseFamilyPath The folder path where the release familes can be found
	 * @param repoId The id of the repo to associate with this manager
	 */
	public ReleaseFamilyMgr(Path releaseFamilyPath, String repoId)
	{
		if( releaseFamilyPath == null)
		{
			throw new IllegalArgumentException("releaseFamilyPath must not be null");
		}
		if(Strings.isBlank(repoId))
		{
			throw new IllegalArgumentException("repoId must not be null");
		}
		if(Files.exists(releaseFamilyPath) == false)
		{
			releaseFamilyPath.toFile().mkdirs();
		}
		this.releaseFamilyPath = releaseFamilyPath;
		this.releaseFamilyRefresher = new ReleaseFamilyRefresher(repoId, releaseFamilyPath);
	}
	
	

	/**
	 * Retrieves an instance of the releaseFamily identified by the release family.
	 * This method loads the file from disk
	 * 
	 * @param releaseFamily
	 *            The identifier for the release family to load the releaseFamily for
	 * @return The releaseFamily object
	 * @throws RepositoryException
	 *             Thrown if the file could not be loaded
	 */
	public ReleaseFamily getReleaseFamily(String releaseFamily) throws RepositoryException
	{
		if (StringUtils.isBlank(releaseFamily))
		{
			throw new IllegalArgumentException("releaseFamily must not be null or empty");
		}
		Path releaseFamilyFile = releaseFamilyPath.resolve(Globals.buildRelFamFileName(releaseFamily));
		return doGetReleaseFamily(releaseFamily, releaseFamilyFile);		
	}
	
	public void addReleaseFamily(ReleaseFamily releaseFamily) throws RepositoryException
	{
		// validate releaseFamily
		releaseFamily.setCreated(new Date());
		releaseFamily.setRetrieved(new Date());
		// determine path to store releaseFamily
		String fileName = Globals.buildRelFamFileName(releaseFamily.getReleaseFamily());
		Path filePath = releaseFamilyPath.resolve(fileName);
		// store releaseFamily
		try
		{
			ReleaseFamily.writeToFile(filePath, releaseFamily);
		}
		catch (IOException ex)
		{
			logger.error(".addReleaseFamily(): Could not write path '%s'. Reason: %s", filePath, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not write release family file");
		}
		
	}
	/**
	 * Retrieves the list of available releaseFamilys in the local cache
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public List<ReleaseFamily> getReleaseFamilies() throws RepositoryException
	{
		
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher(Globals.RELEASE_FAM_GLOB);
		
		List<ReleaseFamily> files;
		if (Files.exists(releaseFamilyPath))
		{
			try
			{
				releaseFamilyRefresher.updateReleaseFamilyList(ReleaseFamilyMgr::touchFile);
				files = Files.walk(releaseFamilyPath).filter(Files::isRegularFile)
						.filter(path -> matcher.matches(path))
						.map(path ->
						{
							try
							{	
								String fileName = path.getFileName().toString();
								String releaseFamily = FilenameUtils.getBaseName(fileName);
								return doGetReleaseFamily(releaseFamily, path);
							}
							catch(RepositoryException ex)
							{
								logger.error(".getReleaseFamilies(): convertPathToReleaseFamily() failed for path '%s', removing");
								return null;
							}
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
			}
			catch (IOException ex)
			{
				logger.error(".getReleaseFamilies(): Could not walk path '%s'. Reason: %s", releaseFamilyPath, ex.getMessage());
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not walk server path",
						releaseFamilyPath, ex.getMessage());
			}
		}
		else
		{
			logger.warn(".getReleaseFamilies(): release family folder '%s' does not exist", releaseFamilyPath.toString());
			files = new ArrayList<ReleaseFamily>();
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
		ReleaseFamilyEntry releaseFamilyEntry = getNewVersionEntry(newVersion, encoder);
		// encode fileName
		writeActiveVersionStoreFile(activeVersId, encoder, releaseFamilyEntry);
	}

	public ReleaseFamilyEntry getActiveVersion(String activeVersId, ActiveVersionEncoder encoder) throws RepositoryException
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
		// load fileName as releaseFamilyEntry
		try
		{
			return encoder.loadVersionEntry(fileName);
		}
		catch (FileNotFoundException ex)
		{
			logger.error(".getActiveVersion(): could not find active version entry for active version id '%s'. Reason: %s", activeVersId,
										ex.getMessage());
			throw new RepositoryException(Status.NOT_FOUND, "Could not find release family file '%s'", fileName.getFileName().toString());

		}
		catch (IOException ex)
		{
			logger.error(".getActiveVersion(): Could not read active version entry file '%s'. Reason: %s", fileName, ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not read active version entry");
		}
	}

	
}
