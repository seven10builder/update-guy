package com.seven10.update_guy.server.repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;
import com.seven10.update_guy.common.FileFingerPrint;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.server.exceptions.RepositoryException;

@XmlRootElement
public class RepositoryInfo
{
	
	private byte[] toByteArray() throws RepositoryException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		
		try
		{
			outputStream.write( description.getBytes() );
			outputStream.write( releaseFamilyPath.getBytes() );
			outputStream.write( password.getBytes() );
			outputStream.write( repoAddress.getBytes() );
			outputStream.write( repoType.toString().getBytes() );
			outputStream.write( user.getBytes() );
			outputStream.write( Integer.toString(port).getBytes() );
		}
		catch (IOException e)
		{
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not convert RepoInfo object to byte array. reason: %s", e.getMessage());
		}
		return outputStream.toByteArray();
	}
	
	public enum RepositoryType
	{
		@Expose
		@XmlElement
		local,
		@Expose
		@XmlElement
		ftp
	}
    public RepositoryInfo()
    {
    	repoAddress = "";
    	port = 0;
    	user = "";
    	password = "";
    	releaseFamilyPath = ".";
    	repoType = RepositoryType.local;
    	//cachePath = ".";
    	description = "unknown";
    }
    /**
     * The DNS-resolvable name or IP address for the repo. This should be 'localhost' for
     *  local repos
     */
	@Expose
	public String repoAddress;
	/**
	 * The port to use for this repo
	 */
	@Expose
	public int port;
	/**
	 * The user account for this repo. This value is ignored for local repos
	 */
	@Expose
	public String user;
	/**
	 * The password for this repo. This value is ignored for local repos
	 * Note: This value should be filtered out or masked when serialized to a string
	 */
	@Expose
    public String password;
	/**
	 * The path on the repo where any release family files are stored
	 */
	@Expose
    public String releaseFamilyPath;
	/**
	 * a human readable description
	 */
	@Expose
    public String description;
	/**
	 * Repository type
	 */
	@Expose
    public RepositoryType repoType;
	/**
	 * order in which to look for information when autodetecting the correct repo
	 */
	@Expose
	public int priority;
    
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((releaseFamilyPath == null) ? 0 : releaseFamilyPath.hashCode());
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
		if (releaseFamilyPath == null)
		{
			if (other.releaseFamilyPath != null)
			{
				return false;
			}
		}
		else if (!releaseFamilyPath.equals(other.releaseFamilyPath))
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return GsonFactory.getGson().toJson(this);
	}
	public String getShaHash() throws RepositoryException
	{
		try
		{
			return FileFingerPrint.create(this.toByteArray());
		}
		catch (IOException e)
		{
			throw new RepositoryException(Status.INTERNAL_SERVER_ERROR, "Could not create message digest for RepositoryInfo. reason: %s", e.getMessage());
		}
	}

	public Path getRemoteReleaseFamilyPath()
	{
		return Paths.get(this.releaseFamilyPath);
	}
	
}
