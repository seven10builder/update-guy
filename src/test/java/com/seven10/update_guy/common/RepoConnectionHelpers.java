package com.seven10.update_guy.common;

import static com.seven10.update_guy.common.ManifestHelpers.*;
import static com.seven10.update_guy.common.RepoInfoHelpers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.TestConstants;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.common.manifest.UpdateGuyRole;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;

public class RepoConnectionHelpers
{
	public static List<RepositoryInfo> create_repo_infos_from_filename(String repoInfoFileName) throws IOException
	{
		Path repoPath = get_repos_path().resolve(repoInfoFileName);
		String json = FileUtils.readFileToString(repoPath.toFile(), GsonFactory.encodingType);
		Gson gson = GsonFactory.getGson();
		Type collectionType = new TypeToken<List<RepositoryInfo>>()
		{
		}.getType();
		List<RepositoryInfo> repos = gson.fromJson(json, collectionType);
		return repos;
	}

	public static void copy_downloads_to_path(ManifestEntry versionEntry, Path cachePath) throws IOException
	{
		for(Entry<String, UpdateGuyRole> entry: versionEntry.getAllRoleInfos())
		{
			Path srcFile = entry.getValue().getFilePath();
			Path destFile = cachePath.resolve(entry.getValue().getFilePath().getFileName());
			FileUtils.copyFile(srcFile.toFile(), destFile.toFile());
		}
	}

	public static RepositoryInfo get_repo_info_by_type(List<RepositoryInfo> repos, RepositoryType type)
	{
		RepositoryInfo repoInfo = repos.stream().filter(repo->repo.repoType == type).findAny().orElse(new RepositoryInfo());
		return repoInfo;
	}

	public static Path build_cache_path_by_testname(String releaseFamily, TemporaryFolder folder) throws IOException
	{
		return folder.newFolder(String.format("%s_cache", releaseFamily)).toPath();
	}

	public static ManifestEntry get_manifest_entry_from_file(Path manifestPath) throws IOException
	{		
		copy_manifest_to_path(TestConstants.valid_manifest_name, manifestPath);
		// grab the first version entry we see
		ManifestEntry manifestEntry = load_manifest_from_path(manifestPath).getVersionEntries().get(0);
		return manifestEntry;
	}
	
	public static FTPClient create_mocked_ftp_client() throws IOException
	{
		FTPClient ftpClient = mock(FTPClient.class);
		Answer<InputStream> fileStreamAnswer = new Answer<InputStream>()
		{
		    @Override
		    public InputStream answer(InvocationOnMock invocation) throws Throwable
		    {
		      String filePath = (String) invocation.getArguments()[0];
		      return new FileInputStream(filePath);
		    }
		};
		when(ftpClient.retrieveFileStream(anyString())).then(fileStreamAnswer);
		when(ftpClient.completePendingCommand()).thenReturn(true);
		return ftpClient;
	}
	public static FTPClient create_mocked_ftp_client_file_not_found() throws IOException
	{
		FTPClient ftpClient = mock(FTPClient.class);
		when(ftpClient.retrieveFileStream(anyString())).thenReturn(null);
		when(ftpClient.completePendingCommand()).thenReturn(true);
		return ftpClient;
	}

}
