package com.seven10.update_guy.repository.connection;

import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.repository.RepositoryInfo;

public class RepoConnectionFactory
{
	public static RepoConnection connect(RepositoryInfo activeRepo) throws RepositoryException
	{
		RepoConnection rval;
		switch( activeRepo.repoType)
		{
		case local:
			rval = new LocalRepoConnection(activeRepo);
			break;
		case ftp:
			rval = new FtpRepoConnection(activeRepo);
			break;
		default:
			throw new RepositoryException("Unsupported repository type '%s'", activeRepo.repoType);
		}
		return rval;
	}

}
