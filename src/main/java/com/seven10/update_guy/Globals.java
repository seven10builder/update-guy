package com.seven10.update_guy;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

public class Globals
{
	private static final Logger logger = LogManager.getFormatterLogger(Globals.class);
	
	private static final Path manifestPathDefault =  FileSystems.getDefault().getPath(System.getProperty("update-guy.manifests", "manifests"));
	private static final String releaseFamilyDefault = "unknown";

	private static Globals staticGlobals = new Globals();
	private Path manifestPath;
	private String releaseFamily;
	private ManifestEntry activeVersion;
	private RepositoryInfo repoInfo;
	
	public Globals()
	{
		setManifestPath(manifestPathDefault);
		setReleaseFamily(releaseFamilyDefault);
		setActiveVersion(new ManifestEntry());
		setRepoInfo(new RepositoryInfo());
	}
	
	public String getReleaseFamily()
	{
		return releaseFamily;
	}
	public void setReleaseFamily(String newFamily)
	{
		logger.debug(".setReleaseFamily(): releaseFamily = %s", newFamily);
		this.releaseFamily = newFamily;
	}
	
	public ManifestEntry getActiveVersion()
	{
		return activeVersion;
	}
	public void setActiveVersion(ManifestEntry newVersion)
	{
		logger.debug(".setActiveVersion(): activeVersion = %s", newVersion.toString());
		this.activeVersion = newVersion;
	}
	
	public RepositoryInfo getRepoInfo()
	{
		return repoInfo;
	}
	public void setRepoInfo(RepositoryInfo newInfo)
	{
		logger.debug(".setRepoInfo(): repoInfo = %s", newInfo.toString());
		this.repoInfo = newInfo;
	}
	
	public Path getManifestPath()
	{
		return manifestPath;
	}
	public void setManifestPath(Path newPath)
	{
		logger.debug(".setManifestPath(): manifestPath = %s", newPath.toString());
		this.manifestPath = newPath;
	}
	public static Globals getGlobals()
	{
		return staticGlobals;
	}

	public static Globals createGlobals()
	{
		logger.debug("initializing static globals field");
		staticGlobals = new Globals();
		return staticGlobals;
	}

}
