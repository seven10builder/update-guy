package com.seven10.update_guy.client;

import java.nio.file.Paths;

public class ClientSettings
{
	private static final String DEFAULT_SERVER_ADDRESS = "localhost";
	private static final Integer DEFAULT_PORT_SETTING = 7519;
	private static final String DEFAULT_CACHE_PATH = Paths.get("cache").toString();
	public String serverAddress;
	public int serverPort;
	public String repoId;
	public String roleName;
	public String releaseFamily;
	private String cachePath;
	
	public ClientSettings()
	{
		serverAddress = DEFAULT_SERVER_ADDRESS;
		serverPort = DEFAULT_PORT_SETTING;
		cachePath = DEFAULT_CACHE_PATH;
		repoId = "";
		roleName = "";
		releaseFamily = "unknown";
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
	public String getCachePath()
	{
		return cachePath;
	}
	/**
	 * @param releaseFamily the releaseFamily to set
	 */
	public void setCachePath(String cachePath)
	{
		this.cachePath = cachePath;
	}
	
}
