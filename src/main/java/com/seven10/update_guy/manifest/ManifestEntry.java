package com.seven10.update_guy.manifest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.seven10.update_guy.GsonFactory;

public class ManifestEntry
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestEntry.class);
	
	@Expose
	protected String version;
	@Expose
	protected Date publishDate;
	@Expose
	protected final Map<String, Path> fileMap;
	@Expose
	protected String releaseFamily;
	
	public ManifestEntry()
	{
		releaseFamily = "unknown";
		version = "unknown";
		publishDate = new Date();
		fileMap = new HashMap<String, Path>();
	}
	
	public ManifestEntry(ManifestEntry versionEntry)
	{
		releaseFamily = versionEntry.releaseFamily;
		version = versionEntry.version;
		publishDate = versionEntry.publishDate;
		fileMap = new HashMap<String, Path>(versionEntry.fileMap);
	}
	
	@Override
	public String toString()
	{
		return GsonFactory.getGson().toJson(this);
	}
	/*
	 * (non-Javadoc)
	 * 
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
		result = prime * result + ((releaseFamily == null) ? 0 : releaseFamily.hashCode());
		logger.debug(".hashCode(): result = %s", result);
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			logger.debug(".equals(): match = true. other is the same object");
			return true;
		}
		if (obj == null)
		{
			logger.debug(".equals(): match = false. other is null");
			return false;
		}
		if (!(obj instanceof ManifestEntry))
		{
			logger.debug(".equals(): match = false. other is not a ManifestVersionEntry");
			return false;
		}
		ManifestEntry other = (ManifestEntry) obj;
		if (fileMap.size() != other.fileMap.size())
		{
			logger.debug(".equals(): match = false. other size does not match (this = %d, other = %d)", fileMap.size(),
					other.fileMap.size());
			return false;
		}
		else if (fileMap.entrySet().containsAll(other.fileMap.entrySet()) == false)
		{
			logger.debug(".equals(): match = false. other has entries that are not found in ours");
			return false;
		}
		if (!publishDate.equals(other.publishDate))
		{
			logger.debug(".equals(): match = false. publishDate do not match (this=%d, other=%d)", this.publishDate.getTime(), other.publishDate.getTime());
			return false;
		}
		if (!releaseFamily.equals(other.releaseFamily))
		{
			logger.debug(".equals(): match = false. publishDate do not match (this=%d, other=%d)", this.publishDate.getTime(), other.publishDate.getTime());
			return false;
		}
		if (!version.equals(other.version))
		{
			logger.debug(".equals(): match = false. versions do not match (this=%s, other = %s)", this.version, other.version);
			return false;
		}
		
		return true;
	}
	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}
	
	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version)
	{
		if (version == null || version.isEmpty())
		{
			throw new IllegalArgumentException("version must not be null");
		}
		logger.debug(".setVersion(): version=%s", version);
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
	 * @param publishDate
	 *            the publishDate to set
	 */
	public void setPublishDate(Date publishDate)
	{
		if (publishDate == null)
		{
			throw new IllegalArgumentException("publishDate must not be null");
		}
		logger.debug(".setPublishDate(): publishDate=%s", publishDate);
		this.publishDate = publishDate;
	}
	public String getReleaseFamily()
	{
		return releaseFamily;
	}
	public Path getPath(String fileRole)
	{
		if (fileRole == null || fileRole.isEmpty())
		{
			throw new IllegalArgumentException("fileRole must not be null");
		}
		return fileMap.get(fileRole);
	}
	
	public void addPath(String fileRole, Path filePath)
	{
		if (fileRole == null || fileRole.isEmpty())
		{
			throw new IllegalArgumentException("fileRole must not be null");
		}
		if (filePath == null)
		{
			throw new IllegalArgumentException("filePath must not be null");
		}
		logger.debug(".addPath(): role=%s, filePath=%s, new map size=%d", fileRole, filePath.toString(), fileMap.size() + 1);
		fileMap.put(fileRole, filePath);
	}
	
	public List<String> getRoles()
	{
		List<String> roleList = new ArrayList<String>();
		roleList.addAll(fileMap.keySet());
		return roleList;
	}
	
	public List<Entry<String, Path>> getRolePaths(List<String> roles)
	{
		List<Entry<String, Path>> selectedValues = fileMap.entrySet().stream() // convert all entries into stream of pairs
				.filter(pair -> filterPaths(roles, pair)) // select the pair if it is one we are interest in
				.collect(Collectors.toList()); // convert selected values to set of paths
		return selectedValues;
	}
	
	/**
	 * @param roles
	 * @param pair
	 * @return
	 */
	public boolean filterPaths(List<String> roles, Entry<String, Path> pair)
	{
		boolean isMatch = roles.contains(pair.getKey());
		logger.debug(".filterPaths(): roles=%s, pair(k,v)=(%s, %s), isMatch=%s", Arrays.toString(roles.toArray()),
				pair.getKey(), pair.getValue(), String.valueOf(isMatch));
		return isMatch;
	}
	
	public List<Entry<String, Path>> getAllRolePaths()
	{
		List<String> roles = getRoles();
		return getRolePaths(roles);
	}


	
	
}
