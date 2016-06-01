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
		if( releaseFamily == null || releaseFamily.isEmpty())
		{
			throw new IllegalArgumentException("releaseFamily must not be null");
		}
		this.releaseFamily = releaseFamily;
		created = new Date();
		retrieved = new Date();
		versions = new HashMap<String, ManifestVersionEntry>();
	}
	
	public Manifest(Manifest newManifest)
	{
		if( newManifest == null)
		{
			throw new IllegalArgumentException("newManifest must not be null");
		}
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
		if( newReleaseFamily == null || newReleaseFamily.isEmpty())
		{
			throw new IllegalArgumentException("newReleaseFamily must not be null or empty");
		}
		this.releaseFamily = newReleaseFamily;
	}
	
	public Date getCreated()
	{
		return created;
	}
	
	public void setCreated(Date newCreated)
	{
		if( newCreated == null)
		{
			throw new IllegalArgumentException("newCreated must not be null");
		}
		this.created = newCreated;
	}
	
	public Date getRetrieved()
	{
		return retrieved;
	}
	
	public void setRetrieved(Date newRetrieved)
	{
		if( newRetrieved == null)
		{
			throw new IllegalArgumentException("newRetrieved must not be null");
		}
		this.retrieved = newRetrieved;
	}
	
	public Collection<ManifestVersionEntry> getVersionEntries()
	{
		return versions.values();
	}
	
	public void addVersionEntry(ManifestVersionEntry versionEntry)
	{
		if( versionEntry == null)
		{
			throw new IllegalArgumentException("versionEntry must not be null");
		}
		versions.put(versionEntry.version, versionEntry);
	}
	
	public static Manifest loadFromFile(Path filePath) throws RepositoryException
	{
		if(filePath == null)
		{
			throw new IllegalArgumentException("filePath must not be null");
		}
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
