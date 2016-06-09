/**
 * 
 */
package com.seven10.update_guy.repository.connection;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;
import com.seven10.update_guy.repository.RepositoryInfo.RepositoryType;
import com.seven10.update_guy.test_helpers.Factories;
import com.seven10.update_guy.test_helpers.TestHelpers;
import com.seven10.update_guy.test_helpers.Validators;

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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		Path srcPath = TestHelpers.getTestFilePath();
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		String releaseFamily = "downloadFile-psnp";
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		File srcFile = folder.newFolder();
		srcFile.setReadable(false);
		Path srcPath = TestHelpers.combineToJsonFileName(releaseFamily, srcFile.toPath());
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		Path srcPath = TestHelpers.getTestFilePath();
		
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		Path srcFullPath = Paths.get("srcPath");
		Path destPath = null;
		repoConnection.downloadFile(srcFullPath, destPath);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#buildDestPath(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testBuildDestPath() throws Exception
	{
		String fileName = "filename.ext";
		
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		
		Path expected = repoInfo.cachePath.resolve(fileName);
		Path actual = repoConnection.buildDestPath(fileName);
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#connect()}.
	 * @throws Exception 
	 */
	@Test
	public void testConnect_whileConnected() throws Exception
	{
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		String releaseFamily = "getManifest";
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, repoInfo.manifestPath);
		Path srcPath = TestHelpers.combineToJsonFileName(releaseFamily, rootFolder);
		Manifest expected = Manifest.loadFromFile(srcPath);
		
		Manifest actual = createConnectionWithMockedFtp(repoInfo).getManifest(releaseFamily);		
		
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#getManifest(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetManifest_null() throws Exception
	{
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
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
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		String releaseFamily = "someRandomReleaseFamily";
		repoConnection.getManifest(releaseFamily);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.manifest.ManifestVersionEntry)}.
	 * @throws Exception 
	 */
	@Test
	public void testDownloadRelease_valid() throws Exception
	{
		String releaseFamily = "downloadRel_valid";
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
				
		createConnectionWithMockedFtp(repoInfo).downloadRelease(versionEntry);
		
		Validators.validateDownloadRelease(versionEntry, repoInfo.cachePath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.manifest.ManifestVersionEntry)}.
	 * @throws Exception 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDownloadRelease_nullManifestEntry() throws Exception
	{
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		FTPClient ftpClient = mock(FTPClient.class);
		FtpRepoConnection repoConnection = new FtpRepoConnection(repoInfo, ftpClient);
		ManifestVersionEntry versionEntry = null;
		repoConnection.downloadRelease(versionEntry);
		
	}
	/**
	 * Test method for {@link com.seven10.update_guy.repository.connection.FtpRepoConnection#downloadRelease(com.seven10.update_guy.manifest.ManifestVersionEntry)}.
	 * @throws Exception 
	 */
	@Test(expected=RepositoryException.class)
	public void testDownloadRelease_fileNotFound() throws Exception
	{
		String releaseFamily = "downloadRel_fnf";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		versionEntry.addPath("nonsenseRole", Paths.get("invalid","path","nonsenseFile.role"));
		
		RepositoryInfo repoInfo = Factories.createValidRepoInfo(RepositoryType.ftp);
		createConnectionWithMockedFtp(repoInfo).downloadRelease(versionEntry);
		
		Validators.validateDownloadRelease(versionEntry, repoInfo.cachePath);
	}
}
