package com.seven10.update_guy.manifest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven10.update_guy.exceptions.RepositoryException;

public class Manifest
{
	
	String releaseFamily;
	Date created;
	Date retrieved;
	Map<String, ManifestVersionEntry> versions;
	
	public Manifest(String releaseFamily)
	{
		this.releaseFamily = releaseFamily;
		created = new Date();
		retrieved = new Date();
		versions = new HashMap<String, ManifestVersionEntry>();
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
	
	public Collection<ManifestVersionEntry> getVersionEntries()
	{
		return versions.values();
	}
	
	public void addVersionEntry(ManifestVersionEntry versionEntry)
	{
		versions.put(versionEntry.version, versionEntry);
	}
	
	public static Manifest loadFromFile(Path filePath) throws RepositoryException
	{
		// JSON from file to Object
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			Manifest manifest = mapper.readValue(filePath.toFile(), Manifest.class);
			return manifest;
		}
		catch (IOException e)
		{
			throw new RepositoryException("Could not read file '%s'. Exception: %s", filePath, e.getMessage());
		}
	}
	
}
