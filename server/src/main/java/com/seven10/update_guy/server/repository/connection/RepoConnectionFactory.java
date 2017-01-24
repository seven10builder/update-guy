package com.seven10.update_guy.server.repository.connection;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.net.ftp.FTPClient;

import com.seven10.update_guy.server.repository.RepositoryInfo;
import com.seven10.update_guy.server.exceptions.RepositoryException;

public class RepoConnectionFactory
{
	public static RepoConnection connect(RepositoryInfo activeRepo) throws RepositoryException
	{
		if(activeRepo == null)
		{
			throw new IllegalArgumentException("activeRepo must not be null");
		}
		RepoConnection rval;
		switch(activeRepo.repoType)
		{
		case local:
			rval = new LocalRepoConnection(activeRepo);
			break;
		case ftp:
			rval = new FtpRepoConnection(activeRepo, new FTPClient());
			break;
		default:
			throw new RepositoryException(Status.BAD_REQUEST, "Unsupported repository type '%s'", activeRepo.repoType);
		}
		return rval;
	}

}
