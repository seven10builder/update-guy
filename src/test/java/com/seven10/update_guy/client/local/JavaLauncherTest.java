/**
 * 
 */
package com.seven10.update_guy.client.local;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.seven10.update_guy.client.local.JavaLauncher;
import com.seven10.update_guy.client.exceptions.FatalClientException;


/**
 * @author kmm
 *
 */
public class JavaLauncherTest
{
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.JavaLauncher#JavaLauncher()}.
	 */
	@Test
	public void testJavaLauncher()
	{
		JavaLauncher javaLauncher = new JavaLauncher();
		assertNotNull(javaLauncher);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.JavaLauncher#launchExecutable(java.lang.ProcessBuilder)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testLaunchExecutable_valid() throws IOException, FatalClientException, InterruptedException
	{
		ProcessBuilder pb = new ProcessBuilder("dir")
				.directory(new File("."))
				.inheritIO();
		
		JavaLauncher javaLauncher = new JavaLauncher();
		boolean expected = javaLauncher.launchExecutable(pb);
		assertTrue(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.JavaLauncher#launchExecutable(java.lang.ProcessBuilder)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 * @throws InterruptedException 
	 */
	@Test(expected=FatalClientException.class)
	public void testLaunchExecutable_io_exception_handled() throws FatalClientException, InterruptedException, IOException
	{
		ProcessBuilder pb = new ProcessBuilder("dir")
				.directory(new File("doesnotexist"))
				.inheritIO();
		
		JavaLauncher javaLauncher = new JavaLauncher();
		javaLauncher.launchExecutable(pb);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.JavaLauncher#launchExecutable(java.lang.ProcessBuilder)}.
	 * @throws IOException 
	 * @throws FatalClientException 
	 * @throws InterruptedException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testLaunchExecutable_null_pb() throws FatalClientException, InterruptedException, IOException
	{
		
		JavaLauncher javaLauncher = new JavaLauncher();
		ProcessBuilder processBuilder = null;
		javaLauncher.launchExecutable(processBuilder);
	}
}
