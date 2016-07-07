package com.seven10.update_guy;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map.Entry;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.ManifestEntry;

public class Globals
{
	public static final String ACTIVE_VERSIONS_FOLDER_NAME = "activeVersions";
	public static final String MANIFEST_FOLDER_NAME = "manifests";
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
	
	public static Path getRootPath()
	{
		 return FileSystems.getDefault().getPath(System.getProperty(SETTING_LOCAL_PATH, DEFAULT_LOCAL_PATH));
	}
	public static Path getRepoFile()
	{
		String repoFileName = System.getProperty(SETTING_REPO_FILENAME, DEFAULT_REPO_FILENAME);
		return getRootPath().resolve(repoFileName);
	}
	public static Path getManifestStorePath(String repoId)
	{
		return getRootPath().resolve(repoId).resolve(MANIFEST_FOLDER_NAME);
	}
	public static Path getActiveVersionStorePath()
	{
		return getRootPath().resolve(ACTIVE_VERSIONS_FOLDER_NAME);
	}
	public static Path getFileStorePathForRole(String repoId, String releaseFamily, String version, String roleId)
	{
		return getRootPath().resolve(repoId).resolve(releaseFamily).resolve(version).resolve(roleId);
	}
	/**
	 * @param versionEntry
	 * @param entry
	 * @param srcPath
	 * @return
	 * @throws RepositoryException
	 */
	public static Path buildDownloadTargetPath(String repoId, ManifestEntry versionEntry, Entry<String, Path> roleEntry)
			throws RepositoryException
	{
		Path destPath = getFileStorePathForRole(repoId,
														versionEntry.getReleaseFamily(),
														versionEntry.getVersion(),
														roleEntry.getKey());
		Path filePath = destPath.resolve(roleEntry.getValue().getFileName().toString());
		return filePath;
	}

}
