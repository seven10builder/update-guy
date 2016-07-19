package com.seven10.update_guy.client.local;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.common.FileFingerPrint;
import com.seven10.update_guy.common.manifest.ManifestEntry;

public class LocalCacheUtils
{
	private static final Logger logger = LogManager.getFormatterLogger(LocalCacheUtils.class.getName());
	private ClientSettings settings;
	
	public static String getJavaBinPath()
	{
		String javaHome = System.getProperty("java.home");
		Path javaBinPath = Paths.get(javaHome).resolve("bin").resolve("java.exe");
		return javaBinPath.toString();
	}
	
	public LocalCacheUtils(ClientSettings settings)
	{
		if(settings == null)
		{
			throw new IllegalArgumentException("settings must not be null");
		}
		this.settings = settings;
	}
	public String getLocalChecksum(Path jarFilePath) throws FatalClientException
	{
		if(jarFilePath == null)
		{
			throw new IllegalArgumentException("jarFilePath must not be null");
		}
		String rval;
		try
		{
			logger.debug(".getLocalChecksum(): target jarFilePath = '%s'", jarFilePath);
			rval = FileFingerPrint.create(jarFilePath);
			logger.info(".getLocalChecksum(): checksum for jarFilePath '%s' = %s", jarFilePath, rval);
		}
		catch (FileNotFoundException ex)
		{
			logger.warn(".getLocalChecksum(): could not find file '%s', assuming 'new'", jarFilePath);
			rval = "";
		}
		catch (IOException e)
		{
			throw new FatalClientException("IO errr encountered generating checksum for local file '%s'. Reason: %s",
					jarFilePath.toString(), e.getMessage());
		}
		return rval;
	}
	
	public Path buildTargetPath(ManifestEntry release) throws FatalClientException
	{
		if(release == null)
		{
			throw new IllegalArgumentException("release must not be null");
		}
		try
		{
			List<String> roleList = release.getRoles()
					.stream()											//convert to stream
					.filter(role->role.equals(settings.getRoleName()))	// get only the role that matches this name
					.collect(Collectors.toList());						// convert to a list
			logger.debug(".buildTargetPath(): roleList = %s", String.join(", ", roleList));
			
			Path jarFileName = release.getRoleInfos(roleList)
					.stream()											// convert to stream
					.map(entry->entry.getValue().getFilePath().getFileName())			// change each entry to just the value
					.findFirst()										// find our file
					.get();												// convert it from stream
			logger.debug(".buildTargetPath(): jarFileName = %s", jarFileName.toString());
					
			Path jarFilePath = settings.getCachePath().resolve(jarFileName);
			logger.debug(".buildTargetPath(): jarFilePath = %s", jarFilePath.toString());
			return jarFilePath;
		}
		catch(NoSuchElementException ex)
		{
			throw new FatalClientException("Could not find role '%s'", settings.getRoleName());
		}
	}
	
}
