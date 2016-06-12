package com.seven10.update_guy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.seven10.update_guy.manifest.ManifestVersionEntry;

public class ManifestEntryHelpers
{
	public static Map<String, Path> create_entry_folder_list(int roleCount, Path rootFolder)
	{
		Map<String, Path> rvalue = new HashMap<String, Path>();
		for(int i = 1; i < roleCount; i++)
		{
			String key = "role_" + i;
			Path target = rootFolder.resolve(key + ".txt");
			rvalue.put(key,  target);
		}
		return rvalue;
	}
	public static ManifestVersionEntry create_valid_manifest_entry(String testName, int index, Path rootFolder)
	{
		ManifestVersionEntry mve = new ManifestVersionEntry();
		mve.setVersion("v"+ testName + index);
		Map<String, Path> entries = create_entry_folder_list(index, rootFolder);
		entries.forEach((key,value)->mve.addPath(key, value));
		return mve;
	}
	
	public static List<Entry<String, Path>> create_entry_folder_list(ManifestVersionEntry mve, int index,
			Path rootFolder)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static List<ManifestVersionEntry> create_valid_manifest_entries(String testName, int entryCount,
			Path rootFolder) throws IOException
	{
		List<ManifestVersionEntry> versionEntries = new ArrayList<ManifestVersionEntry>();
		for (int i = 1; i <= entryCount; i++)
		{
			ManifestVersionEntry versionEntry = create_valid_manifest_entry(testName, i, rootFolder);
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
