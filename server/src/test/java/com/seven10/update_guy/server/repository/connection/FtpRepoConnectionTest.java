/**
 * 
 */
package com.seven10.update_guy.server.repository.connection;

import static com.seven10.update_guy.common.ManifestHelpers.*;
import static com.seven10.update_guy.server.helpers.RepoConnectionHelpers.*;
import static com.seven10.update_guy.server.helpers.RepoInfoHelpers.*;
import static com.seven10.update_guy.common.ManifestEntryHelpers.*;
import static com.seven10.update_guy.common.DownloadValidator.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import com.seven10.update_guy.common.ManifestHelpers;
import com.seven10.update_guy.server.helpers.RepoInfoHelpers;

import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.repository.SpyablePathConsumer;
import com.seven10.update_guy.server.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.server.repository.connection.FtpRepoConnection;
import com.seven10.update_guy.server.exceptions.RepositoryException;
/**
 * @author kmm
 *
 */
public class FtpRepoConnectionTest
{
	private FtpRepoConnection createConnectionWithMockedFtp(RepositoryInfo repoInfo)
			throws IOException, FileNotFoundException
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
		
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		return repoConnection;
	}
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#FtpRepoConnection(com.seven10.update_guy.repository.RepositoryInfo)}.
	 * @throws Exception 
	 */
	@Test
	public void testFtpRepoConnection_valid() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		assertNotNull(repoConnection);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#FtpRepoConnection(com.seven10.update_guy.repository.RepositoryInfo)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testFtpRepoConnection_nullRepoInfo()
	{
		RepositoryInfo repoInfo = null;
		FTPClient ftpClient = mock(FTPClient.class);
		new FtpRepoConnection(repoInfo, ftpClient);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#FtpRepoConnection(com.seven10.update_guy.repository.RepositoryInfo)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testFtpRepoConnection_nullFtpClient() throws Exception
	{
		RepositoryInfo repoInfo = mock(RepositoryInfo.class);
		FTPClient ftpClient = null;
		new FtpRepoConnection(repoInfo, ftpClient);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadFile(java.nio.file.Path, java.nio.file.Path)}.
	 * @throws Exception 
	 */
	@Test
	public void testDownloadFile_valid() throws Exception
	{
		Path repoPath = RepoInfoHelpers.get_valid_repos_path();
		RepositoryInfo repoInfo = load_repos_from_file(repoPath).stream()
											.filter(repo-> repo.repoType == RepositoryType.local)
											.findFirst().get();
		
		Path srcPath = get_valid_download_file_path();
		Path destPath = folder.newFolder().toPath().resolve(srcPath.getFileName().toString());
		
		FtpRepoConnection repoConnection = createConnectionWithMockedFtp(repoInfo);
		
		repoConnection.downloadFile(srcPath, destPath);	
		
		boolean areFilesEqual = FileUtils.contentEquals(srcPath.toFile(), destPath.toFile());
		assertTrue(areFilesEqual);	
	}	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadFile(java.nio.file.Path, java.nio.file.Path)}.
	 * @throws Exception 
	 */
	@Test(expected=RepositoryException.class)
	public void testDownloadFile_srcPathNotExist() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		Path srcPath = Paths.get("someNonExistant","path","file.txt");
		Path destPath = folder.newFolder().toPath().resolve(srcPath.getFileName().toString());
		
		FtpRepoConnection repoConnection = createConnectionWithMockedFtp(repoInfo);
		
		repoConnection.downloadFile(srcPath, destPath);	
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadFile(java.nio.file.Path, java.nio.file.Path)}.
	 * @throws Exception 
	 */
	@Test(expected=RepositoryException.class)
	public void testDownloadFile_srcPathNoPriv() throws Exception
	{
		String testName = "downloadFile-psnp";
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		File srcFile = folder.newFolder(testName);
		srcFile.setReadable(false);
		Path srcPath =  srcFile.toPath().resolve(testName + ".json");
		Path destPath = folder.newFolder().toPath().resolve(srcPath.getFileName());
		
		FtpRepoConnection repoConnection = createConnectionWithMockedFtp(repoInfo);
		
		repoConnection.downloadFile(srcPath, destPath);	
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadFile(java.nio.file.Path, java.nio.file.Path)}.
	 * @throws Exception 
	 */
	@Test(expected=RepositoryException.class)
	public void testDownloadFile_destPathNoPriv() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		Path srcPath = get_valid_download_file_path();
		
		File destFile = folder.newFolder();
		destFile.setWritable(false);
		Path destPath = destFile.toPath().resolve(srcPath.getFileName().toString());
		
		FtpRepoConnection repoConnection = createConnectionWithMockedFtp(repoInfo);
		
		repoConnection.downloadFile(srcPath, destPath);
	}	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadFile(java.nio.file.Path, java.nio.file.Path)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDownloadFile_nullSrcPath() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		Path srcFullPath = null;
		Path destPath = Paths.get("destPath");
		repoConnection.downloadFile(srcFullPath, destPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadFile(java.nio.file.Path, java.nio.file.Path)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDownloadFile_nullDestPath() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		Path srcFullPath = Paths.get("srcPath");
		Path destPath = null;
		repoConnection.downloadFile(srcFullPath, destPath);
	}
	
		
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#connect()}.
	 * @throws Exception 
	 */
	@Test
	public void testConnect_whileConnected() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		when(ftpClient.isConnected()).thenReturn(true);
	
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		repoConnection.connect();

		verify(ftpClient, atLeastOnce()).isConnected();
		verify(ftpClient, atLeastOnce()).disconnect();
		verify(ftpClient, atLeastOnce()).logout();
		verify(ftpClient, atLeastOnce()).connect(repoInfo.repoAddress, repoInfo.port );
		verify(ftpClient, atLeastOnce()).login(repoInfo.user, repoInfo.password);
		verify(ftpClient, atLeastOnce()).enterLocalPassiveMode();
		verify(ftpClient, atLeastOnce()).setFileType(FTP.BINARY_FILE_TYPE);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#connect()}.
	 * @throws Exception 
	 */
	@Test
	public void testConnect_notConnected() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		when(ftpClient.isConnected()).thenReturn(false);
	
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		repoConnection.connect();

		verify(ftpClient, atLeastOnce()).isConnected();
		verify(ftpClient, never()).disconnect();
		verify(ftpClient, never()).logout();
		verify(ftpClient, atLeastOnce()).connect(repoInfo.repoAddress, repoInfo.port );
		verify(ftpClient, atLeastOnce()).login(repoInfo.user, repoInfo.password);
		verify(ftpClient, atLeastOnce()).enterLocalPassiveMode();
		verify(ftpClient, atLeastOnce()).setFileType(FTP.BINARY_FILE_TYPE);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#disconnect()}.
	 * @throws Exception 
	 */
	@Test
	public void testDisconnect_whileConnected() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		when(ftpClient.isConnected()).thenReturn(true);
	
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		repoConnection.disconnect();

		verify(ftpClient, atLeastOnce()).isConnected();
		verify(ftpClient, atLeastOnce()).disconnect();
		verify(ftpClient, atLeastOnce()).logout();
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#disconnect()}.
	 * @throws Exception 
	 */
	@Test
	public void testDisconnect_notConnected() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		when(ftpClient.isConnected()).thenReturn(false);
	
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		repoConnection.disconnect();

		verify(ftpClient, atLeastOnce()).isConnected();
		verify(ftpClient, never()).disconnect();
		verify(ftpClient, never()).logout();
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#getManifest(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testGetManifest_valid() throws Exception
	{
		String testName = "getManifest-v";
		
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.ftp);
		Manifest expected = load_manifest_from_path(get_valid_manifest_file_path());
		// 
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, create_mocked_ftp_client());
		
		Manifest actual = repoConnection.getManifest("relfam");
		
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#getManifest(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetManifest_null() throws Exception
	{
		RepositoryInfo repoInfo = mock(RepositoryInfo.class);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		String releaseFamily = null;
		repoConnection.getManifest(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#getManifest(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetManifest_empty() throws Exception
	{
		RepositoryInfo repoInfo = mock(RepositoryInfo.class);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		String releaseFamily = "";
		repoConnection.getManifest(releaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#getManifest(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test(expected=RepositoryException.class)
	public void testGetManifest_notFound() throws Exception
	{
		String testName = "getManifest-nf";

		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.ftp);
		
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, create_mocked_ftp_client());
		
		repoConnection.getManifest("some-other-release");
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.common.manifest.ManifestEntry)}.
	 * @throws Exception 
	 */
	@Test
	public void testDownloadRelease_valid() throws Exception
	{
		String releaseFamily = "downloadRel_valid";

		// set up our local repo
		String repoInfoFileName = "ftpRepo.json";
		List<RepositoryInfo> repos = create_repo_infos_from_filename(repoInfoFileName);
		RepositoryInfo repo = get_repo_info_by_type(repos, RepositoryType.ftp);
		
		// point repo at our test cache
		Path cachePath = build_cache_path_by_testname(releaseFamily, folder);
		
		// setup a manifest entry to get
		Path manifestPath = build_manifest_path_by_testname(releaseFamily, folder);
		ManifestEntry entry = get_manifest_entry_from_file(manifestPath);
		int roleCount = entry.getRoles().size();
		
		copy_downloads_to_path(entry, cachePath);

		FtpRepoConnection repoConnection = new FtpRepoConnection(repo, create_mocked_ftp_client());

		Consumer<Path> spiedOnFileComplete = spy(new SpyablePathConsumer());
		repoConnection.downloadRelease(entry, spiedOnFileComplete );
		verify(spiedOnFileComplete, times(roleCount)).accept(any());
		validate_downloaded_release(entry, repo.getShaHash());
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.common.manifest.ManifestEntry)}.
	 * @throws Exception 
	 */
	@Test
	public void testDownloadRelease_nullManifestEntry() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		ManifestEntry versionEntry = null;
		Consumer<Path> spiedOnFileComplete = spy(new SpyablePathConsumer());
		try
		{
			repoConnection.downloadRelease(versionEntry, spiedOnFileComplete );
			//we expect this to fail
			fail("downloadRelease should have thrown an IllegalArgumentException");
		}
		catch(IllegalArgumentException ex)
		{
			verify(spiedOnFileComplete, never()).accept(any());
		}
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.common.manifest.ManifestEntry)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDownloadRelease_null_onFileComplete() throws Exception
	{
		RepositoryInfo repoInfo = load_valid_repo_info(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		ManifestEntry versionEntry = new ManifestEntry();
		
		Consumer<Path> spiedOnFileComplete = null;
		repoConnection.downloadRelease(versionEntry, spiedOnFileComplete );
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.common.manifest.ManifestEntry)}.
	 * @throws Exception 
	 */
	@Test
	public void testDownloadRelease_fileNotFound() throws Exception
	{
		String testName = "getManifest-nf";
		
		// set up our local repo
		RepositoryInfo repoInfo = RepoInfoHelpers.setup_test_repo(testName, folder, RepositoryType.ftp);
		

		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, create_mocked_ftp_client_file_not_found());
		
		Consumer<Path> spiedOnFileComplete = spy(new SpyablePathConsumer());
		try
		{
			ManifestEntry entry = new ManifestEntry();
			entry.setVersion("derp-dapp");
			repoConnection.downloadRelease(entry, spiedOnFileComplete );
			//we expect this to fail
			fail("downloadRelease should have thrown a RepositoryException");
		}
		catch(RepositoryException ex)
		{
			verify(spiedOnFileComplete, never()).accept(any());
		}
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.repository.Manifest.VersionEntry)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testGetFileNames_valid() throws Exception
	{
		Path targetDir = ManifestHelpers.get_manifests_path();
		List<String> expectedFiles = Files.walk(targetDir).filter(Files::isRegularFile)
				.filter(Objects::nonNull)
				.map(file->file.getFileName().toString())
				.collect(Collectors.toList());
		
		FTPFile[] filesArr = expectedFiles.stream().map(fname -> 
		{
			FTPFile ftpFile = mock(FTPFile.class);
			doReturn(fname).when(ftpFile).getName();
			return ftpFile;
		}).toArray(size->new FTPFile[size]);
		
		FTPClient ftpClient = mock(FTPClient.class);
		doReturn(true).when(ftpClient).changeWorkingDirectory(anyString());
		doReturn(filesArr).when(ftpClient).listFiles(anyString());
		
		RepositoryInfo repo = load_valid_repo_info(RepositoryType.ftp);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repo, ftpClient);
		
		List<String> actualFiles = repoConnection.getFileNames();
		assertNotEquals(0, actualFiles.size());
		assertTrue(expectedFiles.containsAll(actualFiles));
		assertTrue(actualFiles.containsAll(expectedFiles));
	}
	
	
}
