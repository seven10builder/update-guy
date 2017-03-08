package com.seven10.update_guy.server.release_family;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonSyntaxException;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.server.ServerGlobals;

public class ActiveVersionEncoder
{
	private final String repoId;
	private final String releaseFamily;

	public ActiveVersionEncoder(String repoId, String releaseFamily)
	{
		if(StringUtils.isBlank(repoId))
		{
			throw new IllegalArgumentException("repoId must not be null or empty");
		}
		if(StringUtils.isBlank(releaseFamily))
		{
			throw new IllegalArgumentException("releaseFamily must not be null or empty");
		}
		this.repoId = repoId;
		this.releaseFamily = releaseFamily;
	}
	public String getRepoId() { return repoId;}
	public String getReleaseFamily() { return releaseFamily;}
	
	public Path encodeFileName(String activeVersId)
	{
		if(StringUtils.isBlank(activeVersId))
		{
			throw new IllegalArgumentException("activeVersId must not be null or empty");
		}
		Path path = ServerGlobals.getActiveVersionStorePath();
		String fileName = String.format("%s.json", activeVersId);
		return path.resolve(repoId).resolve(releaseFamily).resolve(fileName);
	}

	
	public ReleaseFamilyEntry loadVersionEntry(Path fileName) throws IOException, JsonSyntaxException
	{
		if(fileName == null)
		{
			throw new IllegalArgumentException("fileName must not be null");
		}
		String json = FileUtils.readFileToString(fileName.toFile(), GsonFactory.encodingType);
		ReleaseFamilyEntry releaseFamilyEntry = GsonFactory.getGson().fromJson(json, ReleaseFamilyEntry.class);
		return releaseFamilyEntry;
	}
	
	public void writeVersionEntry(Path fileName, ReleaseFamilyEntry entry) throws IOException
	{
		if(fileName == null)
		{
			throw new IllegalArgumentException("fileName must not be null");
		}
		if(entry == null)
		{
			throw new IllegalArgumentException("entry must not be null");
		}
		String json = GsonFactory.getGson().toJson(entry);
		FileUtils.writeStringToFile(fileName.toFile(), json, GsonFactory.encodingType);
	}

}
