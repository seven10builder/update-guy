package com.seven10.update_guy.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.seven10.update_guy.client.cli.CliMgr;
import com.seven10.update_guy.manifest.ManifestEntry;

public class UpdateGuyClient
{
	private static final String JAVA_JAR_OPTION = "-jar";

	private static String getJavaBinPath()
	{
		String javaHome = System.getProperty("java.home");
		Path javaBinPath = Paths.get(javaHome).resolve("bin").resolve("java.exe");
		return javaBinPath.toString();
	}
	
	public static void main(String[] args)
	{
		// get cli options
		CliMgr mgr = new CliMgr(args);
		if( mgr.getIsExecContinued())
		{
			UpdateGuyClient client = new UpdateGuyClient(mgr.getClientSettings(), mgr.getRemainingParams());
			while(true)
			{
				client.executeClientLoop();
			}
		}
	}
	
	private final ClientSettings settings;
	private final List<String> remainingParams;
	private String jarFileName;
	
	public UpdateGuyClient(ClientSettings settings, String[] remainingParams)
	{
		this.settings = settings;
		this.remainingParams = Arrays.asList(remainingParams);
	}
	
	public void executeClientLoop()
	{
		// get current active releaseId from server
		ManifestEntry release = requestActiveRelease();
		// request checksum for activeRelease->role->file
		String remoteChecksum = requestRemoteChecksum(release);
		// get checksum for role->localFile
		String localChecksum = getLocalChecksum();
		// if checksums don't match, download role file
		if(localChecksum.equals(remoteChecksum) == false)
		{
			downloadRoleFile();
		}
		//	launch role file with remaining cli options
		launchExecutable(remainingParams);
	}
	
	private ManifestEntry requestActiveRelease()
	{
		// TODO Auto-generated method stub
		return null;
	}
	private String requestRemoteChecksum(ManifestEntry release)
	{
		// TODO Auto-generated method stub
		return null;
	}
	private String getLocalChecksum()
	{
		// TODO Auto-generated method stub
		return null;
	}
	private void downloadRoleFile()
	{
		// TODO Auto-generated method stub
	}
	
	private void launchExecutable(List<String> remainingParams)
	{
		List<String> paramList = new ArrayList<String>();
		paramList.add(getJavaBinPath());
		paramList.add(JAVA_JAR_OPTION);
		paramList.add(jarFileName);
		paramList.addAll(remainingParams);
		ProcessBuilder pb = new ProcessBuilder(paramList);
		pb.directory(new File(settings.getCachePath()));
		try
		{
			Process p = pb.start();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
