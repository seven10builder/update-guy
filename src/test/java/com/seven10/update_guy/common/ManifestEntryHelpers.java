package com.seven10.update_guy.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.seven10.update_guy.common.manifest.ManifestEntry;

public class ManifestEntryHelpers
{
	public static Map<String, Path> create_entry_folder_list(int roleCount, Path rootFolder) throws IOException
	{
		Map<String, Path> rvalue = new HashMap<String, Path>();
		rootFolder.toFile().mkdirs();
		for(int i = 1; i <= roleCount; i++)
		{
			String key = "role_" + i;
			Path target = rootFolder.resolve(key + ".txt");
			Files.createFile(target);
			rvalue.put(key,  target);
		}
		return rvalue;
	}
	public static ManifestEntry create_valid_manifest_entry(String testName, int index, Path rootFolder) throws IOException
	{
		ManifestEntry mve = new ManifestEntry();
		String version = "v"+ testName + index;
		mve.setVersion(version);
		Map<String, Path> entries = create_entry_folder_list(index, rootFolder);
		entries.forEach((key,value)->mve.addPath(key, value));
		return mve;
	}
	
	public static List<ManifestEntry> create_valid_manifest_entries(String testName, int entryCount,
			Path rootFolder) throws IOException
	{
		List<ManifestEntry> versionEntries = new ArrayList<ManifestEntry>();
		for (int i = 1; i <= entryCount; i++)
		{
			ManifestEntry versionEntry = create_valid_manifest_entry(testName, i, 
												rootFolder.resolve(String.format("entry_%d_%d", entryCount, i)));
			versionEntries.add(versionEntry);
		}
		return versionEntries;
	}
	public static List<String> create_subset_of_roles(Map<String, Path> roleMap)
	{
		// filter out keys with odd hashs. This should give us a subset of the keys
		return roleMap.keySet().stream().filter(key->key.hashCode()%2 == 0).collect(Collectors.toList());
	}
	public static Path get_valid_download_file_path()
	{
		return Paths.get("src","test","resources","repoPaths","1.0","file1");
	}


}
