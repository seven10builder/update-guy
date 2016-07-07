package com.seven10.update_guy.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.seven10.update_guy.Globals;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.ClientParameterException;

public class ClientSettings
{
	private static final String DEFAULT_SERVER_ADDRESS = "localhost";
	private static final String DEFAULT_CACHE_PATH = Paths.get("cache").toString();
	
	
	public static ClientSettings loadConfig(Path filePath) throws ClientParameterException
	{
		if(filePath == null)
		{
			throw new IllegalArgumentException("filePath must not be null");
		}
		if(Files.exists(filePath) == false)
		{
			throw new ClientParameterException("The client config located at '%s' was not found", filePath.toString());
		}
		try
		{
			String json = FileUtils.readFileToString(filePath.toFile(), GsonFactory.encodingType);
			Gson gson = GsonFactory.getGson();
			ClientSettings clientSettings = gson.fromJson(json, ClientSettings.class);
			return clientSettings;
		}
		catch (JsonParseException|IOException e)
		{
			throw new ClientParameterException("Could not read file '%s'. Exception: %s", filePath, e.getMessage());
		}
	}
	@Override
	public String toString()
	{
		Gson gson = GsonFactory.getGson();
		String rval = gson.toJson(this);
		return rval;
	}
	@Expose
	public String serverAddress;
	@Expose
	public int serverPort;
	@Expose
	public String repoId;
	@Expose
	public String roleName;
	@Expose
	public String releaseFamily;
	@Expose 
	public String activeVersionId;
	@Expose
	private String cachePath;
	
	
	public ClientSettings()
	{
		serverAddress = DEFAULT_SERVER_ADDRESS;
		serverPort = Globals.DEFAULT_PORT_SETTING;
		cachePath = DEFAULT_CACHE_PATH;
		repoId = "";
		roleName = "";
		releaseFamily = "unknown";
		activeVersionId = "";
	}
	/**
	 * @return the serverAddress
	 */
	public String getServerAddress()
	{
		return serverAddress;
	}
	/**
	 * @param serverAddress the serverAddress to set
	 */
	public void setServerAddress(String serverAddress)
	{
		this.serverAddress = serverAddress;
	}
	/**
	 * @return the serverPort
	 */
	public int getServerPort()
	{
		return serverPort;
	}
	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}
	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(String serverPort)
	{
		this.serverPort = Integer.valueOf(serverPort);
	}
	/**
	 * @return the repoId
	 */
	public String getRepoId()
	{
		return repoId;
	}
	/**
	 * @param repoId the repoId to set
	 */
	public void setRepoId(String repoId)
	{
		this.repoId = repoId;
	}
	/**
	 * @return the roleName
	 */
	public String getRoleName()
	{
		return roleName;
	}
	/**
	 * 
	 */
	public void setActiveVersion(String newVersion)
	{
		this.activeVersionId = newVersion;
	}
	public String getActiveVersion()
	{
		return this.activeVersionId;
	}
	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}
	/**
	 * @return the releaseFamily
	 */
	public String getReleaseFamily()
	{
		return releaseFamily;
	}
	/**
	 * @param releaseFamily the releaseFamily to set
	 */
	public void setReleaseFamily(String releaseFamily)
	{
		this.releaseFamily = releaseFamily;
	}
	
	/**
	 * @return the releaseFamily
	 */
	public Path getCachePath()
	{
		return Paths.get(cachePath);
	}
	/**
	 * @param releaseFamily the releaseFamily to set
	 */
	public void setCachePath(String cachePath)
	{
		this.cachePath = cachePath;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cachePath == null) ? 0 : cachePath.hashCode());
		result = prime * result + ((releaseFamily == null) ? 0 : releaseFamily.hashCode());
		result = prime * result + ((repoId == null) ? 0 : repoId.hashCode());
		result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
		result = prime * result + ((serverAddress == null) ? 0 : serverAddress.hashCode());
		result = prime * result + ((activeVersionId == null)? 0 : activeVersionId.hashCode());
		result = prime * result + serverPort;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof ClientSettings))
		{
			return false;
		}
		ClientSettings other = (ClientSettings) obj;
		if (cachePath == null)
		{
			if (other.cachePath != null)
			{
				return false;
			}
		}
		else if (!cachePath.equals(other.cachePath))
		{
			return false;
		}
		if (releaseFamily == null)
		{
			if (other.releaseFamily != null)
			{
				return false;
			}
		}
		else if (!releaseFamily.equals(other.releaseFamily))
		{
			return false;
		}
		if (repoId == null)
		{
			if (other.repoId != null)
			{
				return false;
			}
		}
		else if (!repoId.equals(other.repoId))
		{
			return false;
		}
		if (roleName == null)
		{
			if (other.roleName != null)
			{
				return false;
			}
		}
		else if (!roleName.equals(other.roleName))
		{
			return false;
		}
		if (serverAddress == null)
		{
			if (other.serverAddress != null)
			{
				return false;
			}
		}
		else if (!serverAddress.equals(other.serverAddress))
		{
			return false;
		}
		if (serverPort != other.serverPort)
		{
			return false;
		}
		if(activeVersionId == null)
		{
			if(other.activeVersionId != null)
			{
				return false;
			}
		}
		else if(!activeVersionId.equals(other.activeVersionId))
		{
			return false;
		}
		return true;
	}
}
