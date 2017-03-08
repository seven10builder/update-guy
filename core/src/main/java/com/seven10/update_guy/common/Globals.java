package com.seven10.update_guy.common;

public class Globals
{
	/* 
	 * Defaults
	 */
	/**
	 * The default port setting to use if a port isn't specified
	 */
	public static final String DEFAULT_PORT_SETTING = "7519";
	
	/*
	 * Settings
	 */
	/**
	 * The port for the server to use to listen for the client
	 */
	public static final String SETTING_LISTEN_PORT = "update-guy.port";

	/**
	 * retrieves the configured listening port for the server
	 * @return the port to use
	 */
	public static int getServerPort()
	{
		String portString = System.getProperty(Globals.SETTING_LISTEN_PORT, Globals.DEFAULT_PORT_SETTING);
		return Integer.valueOf(portString);
	}

	public static final String RELEASE_FAM_FILE_EXT = "rf";
	public static final String RELEASE_FAM_GLOB = String.format("glob:**.%s", RELEASE_FAM_FILE_EXT);
	
	public static final String buildRelFamFileName(String fileName)
	{
		return String.format("%s.%s", fileName, RELEASE_FAM_FILE_EXT);
	}


}
