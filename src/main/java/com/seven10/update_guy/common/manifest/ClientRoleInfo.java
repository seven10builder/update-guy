package com.seven10.update_guy.common.manifest;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ClientRoleInfo
{
	@Expose
	public String fingerPrint;
	@Expose
	public List<String> commandLine;

	public ClientRoleInfo()
	{
		fingerPrint = "";
		commandLine = new ArrayList<String>();
	}
	public ClientRoleInfo(String fingerPrint, List<String> commandLine)
	{
		this.fingerPrint = fingerPrint;
		this.commandLine = commandLine;
	}
}