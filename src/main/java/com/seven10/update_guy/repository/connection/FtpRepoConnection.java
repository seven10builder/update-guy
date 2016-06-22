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
import java.util.Map.Entry;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.manifest.Manifest;
import com.seven10.update_guy.manifest.ManifestEntry;
import com.seven10.update_guy.repository.RepositoryInfo;

/**
 * @author kmm
 * 
 */
public class FtpRepoConnection implements RepoConnection
{
	private static final Logger logger = LogManager.getFormatterLogger(FtpRepoConnection.class);

	private FTPClient ftpClient;
	private RepositoryInfo activeRepo;


	/**
	 * @param outputStream
	 * @param inputStream
	 * @throws RepositoryException
	 */
	private void closeStreams(final OutputStream outputStream, final InputStream inputStream) throws RepositoryException
	{
		try
		{
			closeOutputFileStream(outputStream);
		}
		finally
		{
			closeInputFileStream(inputStream);
		}
	}
	/**
	 * @param fileStream
	 * @throws RepositoryException
	 */
	private void closeInputFileStream(final InputStream fileStream) throws RepositoryException
	{
		try
		{
			fileStream.close();
		}
		catch (IOException ex)
		{
			String msg = "Could not close input stream. Reason: " + ex.getMessage();
			logger.error(".closeInputFileStream(): %s", msg);
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, msg);
		}
		catch(NullPointerException ex)
		{
			// do nothing
		}
	}
	/**
	 * @param fileStream
	 * @throws RepositoryException
	 */
	private void closeOutputFileStream(final OutputStream fileStream) throws RepositoryException
	{
		try
		{
			fileStream.close();
		}
		catch (IOException ex)
		{
			String msg = "Could not close output stream. Reason: " + ex.getMessage();
			logger.error(".closeOutputFileStream(): %s", msg);
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, msg);
		}
		catch(NullPointerException ex)
		{
			// do nothing
		}
	}
	/**
	 * @param srcPath
	 * @param outputStream
	 * @param inputStream
	 * @throws RepositoryException
	 */
	private void executeDownload(String srcPath, final OutputStream outputStream, final InputStream inputStream)
			throws RepositoryException
	{
		if (inputStream == null)
		{
			String msg = "Could not retrieve filestream. Reason: " + ftpClient.getReplyString();
			logger.error(".executeDownload(): %s", msg);
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, msg);
		}
		try
		{
			IOUtils.copy(inputStream, outputStream);
			while (ftpClient.completePendingCommand() == false)
			{
				logger.info(".executeDownload(): File '%s' download is not complete.", srcPath);
			}
		}
		catch (IOException ex)
		{
			String msg = "IOException on download: %s" + ex.getMessage();
			logger.error(".downloadFile(): %s", msg);
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, msg);
		}
		logger.info(".executeDownload(): File '%s' has been downloaded successfully.", srcPath);
	}
	/**
	 * @param srcPath
	 * @return
	 * @throws RepositoryException
	 */
	private InputStream createInputStream(String srcPath) throws RepositoryException
	{
		final InputStream inputStream;
		try
		{
			inputStream = ftpClient.retrieveFileStream(srcPath);
		}
		catch (IOException ex)
		{
			String msg = "Could not create input stream. Reason: " + ex.getMessage();
			logger.error(".createInputStream(): %s", msg);
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, msg);
		}
		return inputStream;
	}
	/**
	 * @param destPath
	 * @param outputStream
	 * @return
	 * @throws RepositoryException
	 */
	private OutputStream createOutputStream(Path destPath) throws RepositoryException
	{
		final OutputStream outputStream;
		try
		{
			outputStream = new BufferedOutputStream(new FileOutputStream(destPath.toFile()));
			return outputStream;
		}
		catch (IOException ex)
		{
			String msg = "Could not create output stream. Reason: " + ex.getMessage();
			logger.error(".createOutputStream(): %s", msg);
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, msg);
		}
	}
	
	public FtpRepoConnection(RepositoryInfo activeRepo, FTPClient ftpClient)
	{
		if (activeRepo == null)
		{
			throw new IllegalArgumentException("activeRepo cannot be null");
		}
		if (ftpClient == null)
		{
			throw new IllegalArgumentException("ftpClient cannot be null");
		}
		this.activeRepo = activeRepo;
		this.ftpClient = ftpClient;
	}

	public void downloadFile(Path srcFullPath, Path destPath) throws RepositoryException
	{
		if (srcFullPath == null)
		{
			throw new IllegalArgumentException("srcFullPath cannot be null");
		}
		if (destPath == null)
		{
			throw new IllegalArgumentException("destPath cannot be null");
		}

		// ensure the dest path exists
		destPath.getParent().toFile().mkdirs();
		String srcPath = srcFullPath.toString();

		final OutputStream outputStream = createOutputStream(destPath);
		final InputStream inputStream = createInputStream(srcPath);

		try
		{
			executeDownload(srcPath, outputStream, inputStream);
		}
		finally
		{
			closeStreams(outputStream, inputStream);
		}
	}

	/**
	 * @param fileName
	 * @return
	 */
	public Path buildDestPath(String fileName)
	{
		return activeRepo.getCachePath().resolve(fileName);
	}

	@Override
	public void connect() throws RepositoryException
	{
		try
		{
			if (ftpClient.isConnected())
			{
				ftpClient.logout();
				ftpClient.disconnect();
			}
			ftpClient.connect(activeRepo.repoAddress, activeRepo.port);
			ftpClient.login(activeRepo.user, activeRepo.password);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		}
		catch (IOException ex)
		{
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not connect to ftp client. Reason: %s", ex.getMessage());
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
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not disconnect from ftp client. Reason: %s", ex.getMessage());
		}

	}

	@Override
	public Manifest getManifest(String releaseFamily) throws RepositoryException
	{
		if (releaseFamily == null || releaseFamily.isEmpty())
		{
			throw new IllegalArgumentException("releaseFamily cannot be null or empty");
		}
		String manifestFileName = String.format("%s.manifest", releaseFamily);
		Path srcPath = activeRepo.getmanifestPath().resolve(manifestFileName);
		Path destPath = buildDestPath(manifestFileName);
		// ensure the path exists

		downloadFile(srcPath, destPath);
		return Manifest.loadFromFile(destPath);
	}

	/**
	 * @see com.seven10.update_guy.repository.connection.RepoConnection#downloadRelease(com.seven10.update_guy.repository.ManifestEntry)
	 */
	@Override
	public void downloadRelease(ManifestEntry versionEntry) throws RepositoryException
	{
		if (versionEntry == null)
		{
			throw new IllegalArgumentException("versionEntry cannot be null");
		}
		for (Entry<String, Path> entry : versionEntry.getRolePaths(versionEntry.getRoles())) // get all the  paths
		{
			Path srcPath = entry.getValue();
			Path destPath = buildDestPath(srcPath.getFileName().toString());
			downloadFile(srcPath, destPath);
		}
	}
	@Override
	public Path getCachePath()
	{
		return activeRepo.getCachePath();
	}
}
