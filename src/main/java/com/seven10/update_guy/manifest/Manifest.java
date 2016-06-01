package com.seven10.update_guy.manifest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.exceptions.RepositoryException;

public class Manifest
{

	private static final String encodingType = "UTF-8";
	String releaseFamily;
	Date created;
	Date retrieved;
	Map<String, ManifestVersionEntry> versions;

	private String formatVersions()
	{
		List<String> entryString = versions.entrySet().stream()
				.map(entry -> String.format("[%s: %s]", entry.getKey(), entry.getValue())).collect(Collectors.toList());
		return StringUtils.join(entryString.toArray(), ", ");
	}

	public Manifest(String releaseFamily)
	{
		if (releaseFamily == null || releaseFamily.isEmpty())
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
		if (newManifest == null)
		{
			throw new IllegalArgumentException("newManifest must not be null");
		}
		this.releaseFamily = newManifest.releaseFamily;
		this.created = newManifest.created;
		this.retrieved = newManifest.retrieved;
		this.versions = newManifest.versions;
	}

	@Override
	public String toString()
	{
		return "Manifest [releaseFamily=" + releaseFamily + ", created=" + created + ", retrieved=" + retrieved
				+ ", versions=[ " + formatVersions() + "]]";
	}

	public String getReleaseFamily()
	{
		return releaseFamily;
	}

	public void setReleaseFamily(String newReleaseFamily)
	{
		if (newReleaseFamily == null || newReleaseFamily.isEmpty())
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
		if (newCreated == null)
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
		if (newRetrieved == null)
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
		if (versionEntry == null)
		{
			throw new IllegalArgumentException("versionEntry must not be null");
		}
		versions.put(versionEntry.version, versionEntry);
	}

	public static void writeToFile(Path filePath, Manifest manifest) throws IOException
	{
		if (filePath == null)
		{
			throw new IllegalArgumentException("filePath must not be null");
		}
		if (manifest == null)
		{
			throw new IllegalArgumentException("manifest must not be null");
		}
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String json = gson.toJson(manifest);
		FileUtils.writeStringToFile(filePath.toFile(), json, encodingType);
	}

	public static Manifest loadFromFile(Path filePath) throws RepositoryException
	{
		if (filePath == null)
		{
			throw new IllegalArgumentException("filePath must not be null");
		}
		try
		{
			String json = FileUtils.readFileToString(filePath.toFile(), encodingType);
			Gson gson = new Gson();
			Type type = new TypeToken<Manifest>()
			{
			}.getType();
			Manifest manifest = (Manifest) gson.fromJson(json, type);
			return manifest;
		}
		catch (IOException e)
		{
			throw new RepositoryException("Could not read file '%s'. Exception: %s", filePath, e.getMessage());
		}
	}

}
