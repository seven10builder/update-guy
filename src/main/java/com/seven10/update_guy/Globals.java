package com.seven10.update_guy;


public class Globals
{
	/**
	 * The path of the local root folder for update-guy. This is the root of the 
	 * tree where manifests and local caches are stored. 
	 */
	public static final String SETTING_LOCAL_PATH = "update-guy.localPath";
	/**
	 * The default folder to use as the root of local storage
	 */
	public static final String DEFAULT_LOCAL_PATH = "local";
	/**
	 * The name of the repo file to look for. 
	 */
	public static final String SETTING_REPO_FILENAME = "update-guy.repoFileName";
	/**
	 * The default repo information file name
	 */
	public static final String DEFAULT_REPO_FILENAME = "repos.json";
}
