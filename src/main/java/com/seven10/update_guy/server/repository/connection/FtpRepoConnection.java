/**
 * 
 */
package com.seven10.update_guy.server.repository.connection;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.manifest.Manifest;
import com.seven10.update_guy.common.manifest.ManifestEntry;
import com.seven10.update_guy.common.manifest.UpdateGuyRole;
import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.ServerGlobals;
import com.seven10.update_guy.server.exceptions.RepositoryException;

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
		Path srcPath = activeRepo.getRemoteManifestPath().resolve(manifestFileName);
		String repoId = activeRepo.getShaHash();
		Path destPath = ServerGlobals.getManifestStorePath(repoId).resolve(manifestFileName);
		// ensure the path exists

		downloadFile(srcPath, destPath);
		try
		{
			return Manifest.loadFromFile(destPath);
		}
		catch (UpdateGuyException ex)
		{
			logger.error(".getManifest(): could not load manifest from path '%s'. Reason: %s", destPath.toString(), ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "could not load manifest '%s'", destPath.getFileName().toString());
		}
	}

	/**
	 * @see com.seven10.update_guy.repository.connection.RepoConnection#downloadRelease(com.seven10.update_guy.repository.ManifestEntry)
	 */
	@Override
	public void downloadRelease(ManifestEntry versionEntry, Consumer<Path> onFileComplete) throws RepositoryException
	{
		if (versionEntry == null)
		{
			throw new IllegalArgumentException("versionEntry cannot be null");
		}
		if(onFileComplete == null)
		{
			throw new IllegalArgumentException("onFileComplete cannot be null");
		}
		List<Entry<String, UpdateGuyRole>> roleInfos = versionEntry.getRoleInfos(versionEntry.getRoles());
		if(roleInfos.size() == 0)
		{
			logger.error(".downloadRelease(): could not find any role infos for version '%s'", versionEntry.getVersion());
			throw new RepositoryException(Status.NOT_FOUND, "No manifests found");
		}
		for (Entry<String, UpdateGuyRole> entry : roleInfos) // get all the  paths
		{
			UpdateGuyRole srcPath = entry.getValue();
			Path destPath;
			try
			{
				destPath = ServerGlobals.buildDownloadTargetPath(activeRepo.getShaHash(), versionEntry, entry);
			}
			catch (UpdateGuyException ex)
			{
				logger.error(".downloadRelease(): Could not build download target path. Reason: %s", ex.getMessage());
				throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not get target path for download.");
			}
			downloadFile(srcPath.getFilePath(), destPath);
			onFileComplete.accept(destPath);
		}
	}
	
	@Override
	public List<String> getFileNames() throws RepositoryException
	{
		Path targetDir = activeRepo.getRemoteManifestPath();
		try
		{
			logger.info(".getFileNames(): attempting to walk path '%s'", targetDir.toString());
			if(ftpClient.changeWorkingDirectory(targetDir.toString()))
			{				
				FTPFile[] arr = ftpClient.listFiles(targetDir.toString());
				List<String> list = Arrays.asList(arr)				// convert array to list
									.stream()						// convert list to stream
									.map(ftpFile->ftpFile.getName())// convert each FTPFile to filename
									.collect(Collectors.toList());	// collect stream to list
				logger.debug(".getFileNames(): results from walk of path '%s' - %s", targetDir, String.join(", ", list) );
				return list;
			}
			else
			{
				logger.error(".getFileNames(): target directory '%s' does not exist", targetDir.toString());
				throw new RepositoryException(Status.NOT_FOUND, "Could not find target directory");
			}
		}
		catch (IOException ex)
		{
			logger.error(".getFileNames(): could not get list of filenames for '%s' - ", targetDir.toString(), ex.getMessage());
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, " Could not get list of filenames");
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activeRepo == null) ? 0 : activeRepo.hashCode());
		result = prime * result + ((ftpClient == null) ? 0 : ftpClient.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof FtpRepoConnection))
		{
			return false;
		}
		FtpRepoConnection other = (FtpRepoConnection) obj;
		if (activeRepo == null)
		{
			if (other.activeRepo != null)
			{
				return false;
			}
		}
		else if (!activeRepo.equals(other.activeRepo))
		{
			return false;
		}
		if (ftpClient == null)
		{
			if (other.ftpClient != null)
			{
				return false;
			}
		}

		return true;
	}

}
