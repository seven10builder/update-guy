package com.seven10.update_guy.repository;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.annotations.Expose;

public class RepositoryInfo
{
	public enum RepositoryType
	{
		local,
		ftp
	}
    public RepositoryInfo()
    {
    	repoAddress = "";
    	port = 0;
    	user = "";
    	password = "";
    	manifestPath = Paths.get(".");
    }
	@Expose
	public String repoAddress;
	@Expose
	public int port;
	@Expose
	public String user;
	@Expose
    public String password;
	@Expose
    public Path manifestPath;
	@Expose
    public String description;
	@Expose
    public RepositoryType repoType;
	@Expose
    public Path cachePath;
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cachePath == null) ? 0 : cachePath.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((manifestPath == null) ? 0 : manifestPath.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((repoAddress == null) ? 0 : repoAddress.hashCode());
		result = prime * result + ((repoType == null) ? 0 : repoType.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + (port);
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
		if (!(obj instanceof RepositoryInfo))
		{
			return false;
		}
		RepositoryInfo other = (RepositoryInfo) obj;
		if (cachePath == null)
		{
			if (other.cachePath != null)
			{
				return false;
			}
		}
		else if (!cachePath.equals(other.cachePath))
		{
			return false;
		}
		if (description == null)
		{
			if (other.description != null)
			{
				return false;
			}
		}
		else if (!description.equals(other.description))
		{
			return false;
		}
		if (manifestPath == null)
		{
			if (other.manifestPath != null)
			{
				return false;
			}
		}
		else if (!manifestPath.equals(other.manifestPath))
		{
			return false;
		}
		if (password == null)
		{
			if (other.password != null)
			{
				return false;
			}
		}
		else if (!password.equals(other.password))
		{
			return false;
		}
		if (repoAddress == null)
		{
			if (other.repoAddress != null)
			{
				return false;
			}
		}
		else if (!repoAddress.equals(other.repoAddress))
		{
			return false;
		}
		if( port != other.port)
		{
			return false;
		}
		if (repoType != other.repoType)
		{
			return false;
		}
		if (user == null)
		{
			if (other.user != null)
			{
				return false;
			}
		}
		else if (!user.equals(other.user))
		{
			return false;
		}
		return true;
	}
	
}
