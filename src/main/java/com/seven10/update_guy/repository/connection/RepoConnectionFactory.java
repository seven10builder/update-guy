package com.seven10.update_guy.repository.connection;

import org.apache.commons.net.ftp.FTPClient;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.repository.RepositoryInfo;

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
			throw new RepositoryException("Unsupported repository type '%s'", activeRepo.repoType);
		}
		return rval;
	}

}
