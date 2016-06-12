package com.seven10.update_guy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.seven10.update_guy.manifest.ManifestVersionEntry;

public class ManifestEntryHelpers
{
	public static List<Entry<String, Path>> create_entry_folder_list(int index, Path rootFolder)
	{
		Map<String, Path> rvalue = new HashMap<String, Path>();
		for(int i = 1; i < index; i++)
		{
			String key = "role_" + i;
			Path target = rootFolder.resolve(key + ".txt");
			rvalue.put(key,  target);
		}
		return new ArrayList<Entry<String,Path>>(rvalue.entrySet());
	}
	public static ManifestVersionEntry create_valid_manifest_entry(String testName, int index, Path rootFolder)
	{
		ManifestVersionEntry mve = new ManifestVersionEntry();
		mve.setVersion("v"+ testName + index);
		List<Entry<String, Path>> entries = create_entry_folder_list(index, rootFolder);
		entries.forEach(entry->mve.addPath(entry.getKey(), entry.getValue()));
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

}
