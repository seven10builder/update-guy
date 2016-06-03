package com.seven10.update_guy.manifest;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class ManifestVersionEntry
{
	@Expose
	protected String version;
	@Expose
	protected Date publishDate;
	@Expose
	protected Map<String, Path> fileMap;
	
	public ManifestVersionEntry()
	{
		version = "unknown";
		publishDate = new Date();
		fileMap = new HashMap<String, Path>();
	}

	public ManifestVersionEntry(ManifestVersionEntry versionEntry)
	{
		version = versionEntry.version;
		publishDate = versionEntry.publishDate;
		fileMap = new HashMap<String, Path>(versionEntry.fileMap);
	}

	@Override
	public String toString()
	{
		return "ManifestVersionEntry [version=" + version + ", publishDate=" + publishDate + ", fileMap=" + fileMap
				+ "]";
	}
	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version)
	{
		if(version == null || version.isEmpty())
		{
			throw new IllegalArgumentException("version must not be null");
		}
		this.version = version;
	}

	/**
	 * @return the publishDate
	 */
	public Date getPublishDate()
	{
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(Date publishDate)
	{
		if(publishDate == null)
		{
			throw new IllegalArgumentException("publishDate must not be null");
		}
		this.publishDate = publishDate;
	}
	
	public Path getPath(String fileRole)
	{
		if(fileRole == null || fileRole.isEmpty())
		{
			throw new IllegalArgumentException("fileRole must not be null");
		}
		return fileMap.get(fileRole);
	}
	public void addPath(String fileRole, Path filePath)
	{
		if(fileRole == null || fileRole.isEmpty())
		{
			throw new IllegalArgumentException("fileRole must not be null");
		}
		if(filePath == null)
		{
			throw new IllegalArgumentException("filePath must not be null");
		}
		fileMap.put(fileRole, filePath);
	}
	public Set<String> getRoles()
	{
		return fileMap.keySet();
	}
	public Set<Entry<String, Path>> getPaths(Set<String> roles)
	{
		Set<Entry<String, Path>> selectedValues = fileMap.entrySet().stream()	// convert set all entries into stream of pairs
		 		.filter(pair->roles.contains(pair.getKey()))	// select the pair if it is one we are interest in
				.collect(Collectors.toSet());					// convert selected values to set of paths
		return selectedValues;
	}

	public Set<Entry<String, Path>> getAllPaths()
	{
		return getPaths(getRoles());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileMap == null) ? 0 : fileMap.hashCode());
		result = prime * result + ((publishDate == null) ? 0 : publishDate.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		if (!(obj instanceof ManifestVersionEntry))
		{
			return false;
		}
		ManifestVersionEntry other = (ManifestVersionEntry) obj;
		if (fileMap == null)
		{
			if (other.fileMap != null)
			{
				return false;
			}
		}
		else if (fileMap.size() != other.fileMap.size())
		{
			return false;
		}
		else if( fileMap.entrySet().containsAll(other.fileMap.entrySet()))
		{
			return false;
		}
		if (publishDate == null)
		{
			if (other.publishDate != null)
			{
				return false;
			}
		}
		else if (!publishDate.equals(other.publishDate))
		{
			return false;
		}
		if (version == null)
		{
			if (other.version != null)
			{
				return false;
			}
		}
		else if (!version.equals(other.version))
		{
			return false;
		}
		return true;
	}
}
