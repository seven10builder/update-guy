/**
 * 
 */
package com.seven10.update_guy.repository.connection;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestVersionEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

/**
 * @author kmm
 * 		
 */
public class FtpRepoConnection implements RepoConnection
{
	private FTPClient ftpClient;
	private RepositoryInfo activeRepo;
	
	public static void downloadFile(FTPClient ftpClient, Path srcFullPath, Path destPath)
	{
		try
		{
			String srcPath = srcFullPath.toString();
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destPath.toFile()));
			InputStream inputStream = ftpClient.retrieveFileStream(srcPath);
			IOUtils.copy(inputStream, outputStream);
			boolean success = ftpClient.completePendingCommand();
			if (success)
			{
				System.out.println(String.format("File '%s' has been downloaded successfully.", srcPath));
			}
			outputStream.close();
			inputStream.close();
		}
		catch (IOException ex)
		{
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public FtpRepoConnection(RepositoryInfo activeRepo)
	{
		this.activeRepo = activeRepo;
		this.ftpClient = new FTPClient();
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	public Path buildDestPath(String fileName)
	{
		return activeRepo.cachePath.resolve(fileName);
	}
	
	@Override
	public void connect() throws RepositoryException
	{
		try
		{
			ftpClient.connect(activeRepo.repoAddress, activeRepo.port);
			ftpClient.login(activeRepo.user, activeRepo.password);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		}
		catch (IOException ex)
		{
			throw new RepositoryException("Could not connect to ftp client. Reason: %s", ex.getMessage());
		}
	}
	
	@Override
	public void disconnect() throws RepositoryException
	{
		try
		{
			if (ftpClient.isConnected())
			{
				ftpClient.logout();
				ftpClient.disconnect();
			}
		}
		catch (IOException ex)
		{
			throw new RepositoryException("Could not disconnect from ftp client. Reason: %s", ex.getMessage());
		}
		
	}
	
	@Override
	public Manifest getManifest(String releaseFamily) throws RepositoryException
	{
		String manifestFileName = String.format("%s.manifest", releaseFamily);
		Path srcPath = activeRepo.manifestPath.resolve(manifestFileName);
		Path destPath = buildDestPath(manifestFileName);
		downloadFile(ftpClient, srcPath, destPath);
		return Manifest.loadFromFile(destPath);
	}
	
	/**
	 * @see com.seven10.update_guy.repository.connection.RepoConnection#downloadRelease(com.seven10.update_guy.repository.ManifestVersionEntry)
	 */
	@Override
	public void downloadRelease(ManifestVersionEntry versionEntry) throws RepositoryException
	{
		versionEntry.getPaths(versionEntry.getRoles()) // get all the paths
				.forEach(entry ->
				{
					Path srcPath = entry.getValue();
					Path destPath = buildDestPath(srcPath.getFileName().toString());
					downloadFile(ftpClient, srcPath, destPath);
				});
	}
}
