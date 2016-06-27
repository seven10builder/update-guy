package com.seven10.update_guy.client.cli;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.FileFingerPrint;
import com.seven10.update_guy.exceptions.ClientParameterException;

public class ParamValidator
{
	static final Logger logger = LogManager.getFormatterLogger(ParamValidator.class.getName());
	public static void validateAddress(final String address) throws ClientParameterException
	{
		if (StringUtils.isBlank(address))
		{
			throw new ClientParameterException("address must not be blank");
		}
		if (InetAddressValidator.getInstance().isValid(address))
		{
			logger.trace(".processValidIp(): address '%s' is valid and will be used", address);
		}
		else
		{
			// not an IPV4 address, could be IPV6?
			try
			{
				logger.trace(".validateAddress(): attempting to get ip address of '%s'", address);
				InetAddress discoveredIp = InetAddress.getByName(address);
				logger.debug(".validateAddress(): '%s' = %s", address, discoveredIp.getHostAddress());
			}
			catch (final UnknownHostException ex)
			{
				throw new ClientParameterException( "Could not find host '%s'. Reason: %s", address, ex.getMessage());
			}
		}
	}

	public static void validatePort(final String newPort) throws ClientParameterException
	{
		if(StringUtils.isBlank(newPort))
		{
			throw new ClientParameterException("newPort must not be blank");
		}
		int port = Integer.valueOf(newPort);
		if( port <= 1024)
		{
			throw new ClientParameterException("Port '%d' must not be reserved value!", newPort);
		}
		logger.trace(".validatePort(): port = '%d'", port);
	}

	public static void validateRepositoryId(final String repoId) throws ClientParameterException
	{
		if(StringUtils.isBlank(repoId))
		{
			throw new ClientParameterException("repoId must not be blank");
		}
		if(repoId.length() != FileFingerPrint.encodedLength)
		{
			throw new ClientParameterException("Repo ID '%s' should be '%d' characters long", repoId, FileFingerPrint.encodedLength);
		}
		logger.trace(".validateRepositoryId(): repoId = '%s'", repoId);
	}

	public static void validateReleaseFamily(final String releaseFamily) throws ClientParameterException
	{
		if(StringUtils.isBlank(releaseFamily))
		{
			throw new ClientParameterException("releaseFamily must not be blank");
		}
	}

	public static void validateRoleName(final String roleName) throws ClientParameterException
	{
		if(StringUtils.isBlank(roleName))
		{
			throw new ClientParameterException("roleName must not be blank");
		}
	}
	
	public static void validateCachePath(final String cachePathStr) throws ClientParameterException
	{
		if(StringUtils.isBlank(cachePathStr))
		{
			throw new ClientParameterException("cachePath must not be blank");
		}
		Path cachePath = Paths.get(cachePathStr);
		if(cachePath.toFile().exists() == false)
		{
			logger.info("cachePath '%s' does not exist. attempting to create", cachePathStr);
			try
			{
				Files.createDirectory(cachePath);
			}
			catch (IOException e)
			{
				throw new ClientParameterException("Could not create path '%s'. Reason: %s", cachePath, e.getMessage());
			}
		}
	}
}
