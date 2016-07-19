package com.seven10.update_guy.common.manifest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.seven10.update_guy.common.GsonFactory;

public class UpdateGuyRole
{
	@Expose
	protected String filePath;
	
	@Expose
	protected final List<String> commandLine;
	
	@Expose
	protected String fingerPrint;
	
	public UpdateGuyRole()
	{
		filePath = "";
		fingerPrint = "";
		commandLine = new ArrayList<String>();
	}

	public UpdateGuyRole(Path filePath, List<String> commandLine, String fingerPrint)
	{
		this.filePath = filePath.toString();
		this.commandLine = commandLine;
		this.fingerPrint = fingerPrint;
	}

	/**
	 * @return the filePath
	 */
	public Path getFilePath()
	{
		return Paths.get(filePath);
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(Path filePath)
	{
		this.filePath = filePath.toString();
	}

	/**
	 * @return the commandLine
	 */
	public List<String> getCommandLine()
	{
		return commandLine;
	}

	/**
	 * @param commandLine the commandLine to sets
	 */
	public void setCommandLine(List<String> commandLine)
	{
		commandLine.clear();
		commandLine.addAll(commandLine);
	}
	
	public String getFingerPrint()
	{
		return fingerPrint;
	}
	public void setFingerPrint(String newValue)
	{
		fingerPrint = newValue;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commandLine == null) ? 0 : commandLine.hashCode());
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((fingerPrint == null) ? 0 :fingerPrint.hashCode());
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
		if (!(obj instanceof UpdateGuyRole))
		{
			return false;
		}
		UpdateGuyRole other = (UpdateGuyRole) obj;
		if (commandLine == null)
		{
			if (other.commandLine != null)
			{
				return false;
			}
		}
		else if (!commandLine.equals(other.commandLine))
		{
			return false;
		}
		if (filePath == null)
		{
			if (other.filePath != null)
			{
				return false;
			}
		}
		else if (!filePath.equals(other.filePath))
		{
			return false;
		}
		if (fingerPrint == null)
		{
			if(other.fingerPrint != null)
			{
				return false;
			}
		}
		else if( (!fingerPrint.equals(other.fingerPrint)))
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
}
