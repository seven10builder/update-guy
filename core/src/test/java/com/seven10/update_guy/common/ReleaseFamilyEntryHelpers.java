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

import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.common.release_family.UpdateGuyRole;

public class ReleaseFamilyEntryHelpers
{
	public static Map<String, UpdateGuyRole> create_entry_folder_list(int roleCount, Path rootFolder) throws IOException
	{
		Map<String, UpdateGuyRole> rvalue = new HashMap<String, UpdateGuyRole>();
		rootFolder.toFile().mkdirs();
		
		for(int i = 1; i <= roleCount; i++)
		{
			String key = "role_" + i;
			Path target = rootFolder.resolve(key + ".txt");
			Files.createFile(target);
			UpdateGuyRole role = new UpdateGuyRole();
			role.setFilePath(target);
			role.setFingerPrint(FileFingerPrint.create(target));
			
			List<String> cmdList = new ArrayList<String>();
			for(int j = 1; j <= roleCount; j++)
			{
				cmdList.add("cmd"+i+j);
			}
				
			role.setCommandLine(cmdList);
			rvalue.put(key,  role);
		}
		return rvalue;
	}
	public static ReleaseFamilyEntry create_valid_release_family_entry(String testName, int index, Path rootFolder) throws IOException
	{
		ReleaseFamilyEntry mve = new ReleaseFamilyEntry();
		String version = "v"+ testName + index;
		mve.setVersion(version);
		Map<String, UpdateGuyRole> entries = create_entry_folder_list(index, rootFolder);
		entries.forEach((key,value)->mve.addRoleInfo(key, value));
		return mve;
	}
	
	public static List<ReleaseFamilyEntry> create_valid_release_family_entries(String testName, int entryCount,
			Path rootFolder) throws IOException
	{
		List<ReleaseFamilyEntry> versionEntries = new ArrayList<ReleaseFamilyEntry>();
		for (int i = 1; i <= entryCount; i++)
		{
			ReleaseFamilyEntry versionEntry = create_valid_release_family_entry(testName, i, 
												rootFolder.resolve(String.format("entry_%d_%d", entryCount, i)));
			versionEntries.add(versionEntry);
		}
		return versionEntries;
	}
	public static List<String> create_subset_of_roles(Map<String, UpdateGuyRole> roleMap)
	{
		// filter out keys with odd hashs. This should give us a subset of the keys
		return roleMap.keySet().stream().filter(key->key.hashCode()%2 == 0).collect(Collectors.toList());
	}
	public static Path get_valid_download_file_path()
	{
		return Paths.get("src","test","resources","remote_repo","local","files","1.0","file1");
	}


}
