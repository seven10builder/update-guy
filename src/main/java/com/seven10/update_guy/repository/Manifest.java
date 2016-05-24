package com.seven10.update_guy.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Manifest
{
	static class VersionEntry
	{
		public String version;
		Date publishDate;
		public Map<String, String> fileMap;		
	}
	String releaseFamily;
	Date created;
	Date retrieved;
	Map<String, VersionEntry> versions;

	public Manifest(String releaseFamily)
	{
		versions = new HashMap<String, VersionEntry>();
	}
	public Manifest(Manifest newManifest)
	{
		this.releaseFamily = newManifest.releaseFamily;
		this.created = newManifest.created;
		this.retrieved = newManifest.retrieved;
		this.versions = newManifest.versions;
	}
	public String getReleaseFamily()
	{
		return releaseFamily;
	}
	public void setReleaseFamily(String newReleaseFamily)
	{
		this.releaseFamily = newReleaseFamily;
	}
	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date newCreated)
	{
		this.created = newCreated;
	}

	public Date getRetrieved()
	{
		return retrieved;
	}

	public void setRetrieved(Date newRetrieved)
	{
		this.retrieved = newRetrieved;
	}

	public Map<String, VersionEntry> getVersions()
	{
		return versions;
	}

	public void setOutput(Map<String, VersionEntry> newVersions)
	{
		this.versions = newVersions;
	}
}
