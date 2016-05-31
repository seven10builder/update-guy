package com.seven10.update_guy.manifest;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class ManifestVersionEntry
{
	protected String version;
	protected Date publishDate;
	protected Map<String, Path> fileMap;
	
	public ManifestVersionEntry()
	{
		fileMap = new HashMap<String, Path>();
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
		this.publishDate = publishDate;
	}
	
	public Path getPath(String fileRole)
	{
		return fileMap.get(fileRole);
	}
	public void addPath(String fileRole, Path filePath)
	{
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
}
