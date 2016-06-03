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
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.exceptions.RepositoryException;

public class Manifest
{

	private static final String encodingType = "UTF-8";
	@Expose
	String releaseFamily;
	@Expose
	Date created;
	@Expose
	Date retrieved;
	@Expose
	Map<String, ManifestVersionEntry> versions;

	private String formatVersions()
	{
		List<String> entryString = versions.entrySet().stream()
				.map(entry -> String.format("[%s: %s]", entry.getKey(), entry.getValue())).collect(Collectors.toList());
		return StringUtils.join(entryString.toArray(), ", ");
	}

	public Manifest()
	{
		releaseFamily = "unknown";
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
		this.versions = new HashMap<String, ManifestVersionEntry>(newManifest.versions);
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
		Gson gson = GsonFactory.getGson();
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
			Gson gson = GsonFactory.getGson();
			Manifest manifest = (Manifest) gson.fromJson(json, Manifest.class);
			return manifest;
		}
		catch (IOException e)
		{
			throw new RepositoryException("Could not read file '%s'. Exception: %s", filePath, e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((releaseFamily == null) ? 0 : releaseFamily.hashCode());
		result = prime * result + ((retrieved == null) ? 0 : retrieved.hashCode());
		result = prime * result + ((versions == null) ? 0 : versions.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Manifest))
		{
			return false;
		}
		Manifest other = (Manifest) obj;
		if (created == null)
		{
			if (other.created != null)
			{
				return false;
			}
		}
		else if (!created.equals(other.created))
		{
			return false;
		}
		if (releaseFamily == null)
		{
			if (other.releaseFamily != null)
			{
				return false;
			}
		}
		else if (!releaseFamily.equals(other.releaseFamily))
		{
			return false;
		}
		if (retrieved == null)
		{
			if (other.retrieved != null)
			{
				return false;
			}
		}
		else if (!retrieved.equals(other.retrieved))
		{
			return false;
		}
		if (versions == null)
		{
			if (other.versions != null)
			{
				return false;
			}
		}
		else if (versions.size() != other.versions.size())
		{
			return false;
		}
		else if( versions.entrySet().containsAll(other.versions.entrySet()))
		{
			return false;
		}
		return true;
	}

}
