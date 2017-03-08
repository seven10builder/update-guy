package com.seven10.update_guy.server;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map.Entry;

import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.common.release_family.UpdateGuyRole;

public class ServerGlobals
{
	/**
	 * The default repo information file name
	 */
	public static final String DEFAULT_REPO_FILENAME = "repos.json";
	/**
	 * The default folder to use as the root of local storage
	 */
	public static final String DEFAULT_LOCAL_PATH = "local";

	/*
	 * String constants
	 */
	/**
	 * The folder name that stores the activeVersions
	 */
	public static final String ACTIVE_VERSIONS_FOLDER_NAME = "activeVersions";
	/**
	 * The folder name where the release familys are cached
	 */
	public static final String RELEASE_FAMILY_FOLDER_NAME = "releaseFamilies";
	
	/*
	 * Settings
	 */
	/**
	 * The name of the repo file to look for. 
	 */
	public static final String SETTING_REPO_FILENAME = "update-guy.repoFileName";
	/**
	 * The path of the local root folder for update-guy. This is the root of the 
	 * tree where release families and local caches are stored. 
	 */
	public static final String SETTING_LOCAL_PATH = "update-guy.localPath";
	/**
	 * @param versionEntry
	 * @param entry
	 * @param srcPath
	 * @return
	 * @throws RepositoryException
	 */
	public static Path buildDownloadTargetPath(String repoId, ReleaseFamilyEntry versionEntry, Entry<String, UpdateGuyRole> roleEntry)
			throws UpdateGuyException
	{
		Path destPath = getFileStorePathForRole(repoId,
														versionEntry.getReleaseFamily(),
														versionEntry.getVersion(),
														roleEntry.getKey());
		Path filePath = destPath.resolve(roleEntry.getValue().getFilePath().getFileName().toString());
		return filePath;
	}

	public static Path getRootPath()
	{
		 return FileSystems.getDefault().getPath(System.getProperty(SETTING_LOCAL_PATH, DEFAULT_LOCAL_PATH));
	}
	public static Path getCacheRoot()
	{
		return getRootPath().resolve("local");
	}
	public static Path getSubRepoRoot(String repoId)
	{
		return getCacheRoot().resolve(repoId);
	}
	public static Path getFileStorePathForRole(String repoId, String releaseFamily, String version, String roleId)
	{
		return getSubRepoRoot(repoId).resolve(releaseFamily).resolve(version).resolve(roleId);
	}
	public static Path getReleaseFamilyStorePath(String repoId)
	{
		return getSubRepoRoot(repoId).resolve(RELEASE_FAMILY_FOLDER_NAME);
	}
	
	public static Path getActiveVersionStorePath()
	{
		return getCacheRoot().resolve(ACTIVE_VERSIONS_FOLDER_NAME);
	}
	
	public static Path getRepoFile()
	{
		String repoFileName = System.getProperty(SETTING_REPO_FILENAME, DEFAULT_REPO_FILENAME);
		return getRootPath().resolve(repoFileName);
	}
	
}
