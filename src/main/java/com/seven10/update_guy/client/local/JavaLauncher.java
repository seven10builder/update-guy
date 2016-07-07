package com.seven10.update_guy.client.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.seven10.update_guy.client.exceptions.FatalClientException;

public class JavaLauncher
{
	private static final String JAVA_JAR_OPTION = "-jar";

	public JavaLauncher()
	{
		
	}
	
	public void launchExecutable(ProcessBuilder processBuilder) throws FatalClientException
	{
		if(processBuilder == null)
		{
			throw new IllegalArgumentException("processBuilder must not be null");
		}
		try
		{
			Process p = processBuilder.start();
			p.waitFor();
		}
		catch (IOException ex)
		{
			throw new FatalClientException("IO Exception launching application. Reason: %s", ex.getMessage());
		}
		catch (InterruptedException ex)
		{
			throw new FatalClientException("application thread interrupted. Reason: %s", ex.getMessage());
		}
	}

	public ProcessBuilder createProcessBuilder(List<String> paramList, Path cachePath)
	{
		return new ProcessBuilder(paramList)
				.directory(new File(cachePath.toString()))
				.inheritIO();
	}

	public List<String> buildParamList(List<String> remainingParams, Path jarFilePath)
	{
		List<String> paramList = new ArrayList<String>();
		paramList.add(LocalCacheUtils.getJavaBinPath());
		paramList.add(JAVA_JAR_OPTION);
		paramList.add(jarFilePath.toString());
		paramList.addAll(remainingParams);
		return paramList;
	}
}
