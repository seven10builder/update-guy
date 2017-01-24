package com.seven10.update_guy.client;

import java.nio.file.Path;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.cli.Options;

import com.seven10.update_guy.client.exceptions.ClientParameterException;
import com.seven10.update_guy.client.exceptions.FatalClientException;
import com.seven10.update_guy.client.local.JavaLauncher;
import com.seven10.update_guy.client.local.LocalCacheUtils;
import com.seven10.update_guy.client.request.Requester;
import com.seven10.update_guy.client.request.RequesterUtils;

public class FunctionalInterfaces
{
	public interface RequesterFactory
	{
		Requester getRequester(String url, String methodName);
	}

	public interface WebReqFactory
	{
		public Invocation.Builder buildRequest();
	}

	public interface ResponseToFileMgr
	{
		public void copy(Path targetPath, Response response, Status statusCode) throws FatalClientException;
	}

	public interface OnShowHelp
	{
		void showHelp(Options options);
	}

	public interface OnConfigFileCmd
	{
		ClientSettings doCommand(String path) throws ClientParameterException;
	}

	public interface RequesterUtilsFactory
	{
		public RequesterUtils build(ClientSettings settings);
	}

	public interface LocalCacheUtilsFactory
	{
		public LocalCacheUtils build(ClientSettings settings);
	}

	public interface JavaLauncherFactory
	{
		public JavaLauncher build(ClientSettings settings);
	}

	public interface UpdateGuyClientFactory
	{
		public UpdateGuyClient build(String[] params);
	}

}
