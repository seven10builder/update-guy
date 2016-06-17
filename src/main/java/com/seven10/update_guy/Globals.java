package com.seven10.update_guy;

import java.nio.file.Path;

import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

public class Globals
{
	private static final Path manifestPathDefault = null;
	private static final String releaseFamilyDefault = null;
	private Path manifestPath;
	private String releaseFamily;
	private ManifestEntry activeVersion;
	private RepositoryInfo repoInfo;
	
	public Globals()
	{
		manifestPath = manifestPathDefault;
		releaseFamily = releaseFamilyDefault;
		activeVersion = new ManifestEntry();
		repoInfo = new RepositoryInfo();
	}
	public String getReleaseFamily()
	{
		return releaseFamily;
	}
	public void setReleaseFamily(String releaseFamily)
	{
		this.releaseFamily = releaseFamily;
	}
	
	public ManifestEntry getActiveVersion()
	{
		return activeVersion;
	}
	public void setActiveVersion(ManifestEntry activeVersion)
	{
		this.activeVersion = activeVersion;
	}
	
	public RepositoryInfo getRepoInfo()
	{
		return repoInfo;
	}
	public void setRepoInfo(RepositoryInfo repoInfo)
	{
		this.repoInfo = repoInfo;
	}
	
	public Path getManifestPath()
	{
		return manifestPath;
	}
	public void setManifestPath(Path manifestPath)
	{
		this.manifestPath = manifestPath;
	}
}
