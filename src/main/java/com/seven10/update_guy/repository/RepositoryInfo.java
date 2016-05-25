package com.seven10.update_guy.repository;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RepositoryInfo implements Serializable
{
    @Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((manifestPath == null) ? 0 : manifestPath.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((repoAddress == null) ? 0 : repoAddress.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepositoryInfo other = (RepositoryInfo) obj;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (manifestPath == null)
		{
			if (other.manifestPath != null)
				return false;
		}
		else if (!manifestPath.equals(other.manifestPath))
			return false;
		if (password == null)
		{
			if (other.password != null)
				return false;
		}
		else if (!password.equals(other.password))
			return false;
		if (repoAddress == null)
		{
			if (other.repoAddress != null)
				return false;
		}
		else if (!repoAddress.equals(other.repoAddress))
			return false;
		if (user == null)
		{
			if (other.user != null)
				return false;
		}
		else if (!user.equals(other.user))
			return false;
		return true;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3262800462100466329L;
	
	@XmlElement public String repoAddress;
    @XmlElement public String user;
    @XmlElement public String password;
    @XmlElement public String manifestPath;
    @XmlElement public String description;
}
