/**
 * 
 */
package com.seven10.update_guy.client.local;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonSyntaxException;
import com.seven10.update_guy.common.FileFingerPrint;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.ReleaseFamilyEntryHelpers;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;
import com.seven10.update_guy.client.ClientSettings;
import com.seven10.update_guy.client.local.LocalCacheUtils;
import com.seven10.update_guy.client.exceptions.FatalClientException;


/**
 * @author kmm
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FileFingerPrint.class)
public class LocalCacheUtilsTest
{

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private ClientSettings load_valid_settings() throws IOException, JsonSyntaxException
	{
		Path destFile = Paths.get("src", "test", "resources", "clientConfig", "valid.json");
		String json = FileUtils.readFileToString(destFile.toFile(), GsonFactory.encodingType);
		ClientSettings expected = GsonFactory.getGson().fromJson(json, ClientSettings.class);
		return expected;
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#getJavaBinPath()}.
	 * @throws IOException 
	 */
	@Test
	public void testGetJavaBinPath() throws IOException
	{

		String javaHome = System.getProperty("java.home");
		Path javaBinPath = Paths.get(javaHome).resolve("bin").resolve("java.exe");
		String expected =  javaBinPath.toString();

		String actual = LocalCacheUtils.getJavaBinPath();
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#LocalCacheUtils(com.seven10.update_guy.client.ClientSettings)}.
	 * @throws IOException 
	 */
	@Test
	public void testLocalCacheUtils_valid() throws IOException
	{
		ClientSettings expected = load_valid_settings();
		
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(expected);
		assertNotNull(localCacheUtils);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#LocalCacheUtils(com.seven10.update_guy.client.ClientSettings)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testLocalCacheUtils_null_settings() throws IOException
	{
		ClientSettings expected = null;
		new LocalCacheUtils(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#getLocalChecksum(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testGetLocalChecksum_valid() throws FatalClientException, FileNotFoundException, IOException
	{
		PowerMockito.mockStatic(FileFingerPrint.class);
		String expected = "this-represents-a-fingerprint";
		Mockito.when(FileFingerPrint.create(Mockito.any(Path.class))).thenReturn(expected);
		
		ClientSettings settings = load_valid_settings();
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
		
		Path jarFilePath = Paths.get("src","test","resources", "repoPaths", "1.0", "file1");
		String actual = localCacheUtils.getLocalChecksum(jarFilePath);
		
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#getLocalChecksum(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetLocalChecksum_null_path() throws FatalClientException, FileNotFoundException, IOException
	{
		ClientSettings settings = load_valid_settings();
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
		Path jarFilePath = null;
		localCacheUtils.getLocalChecksum(jarFilePath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#getLocalChecksum(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testGetLocalChecksum_not_found() throws FatalClientException, FileNotFoundException, IOException
	{
		String expected = "";
		ClientSettings settings = load_valid_settings();
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
		
		Path jarFilePath = Paths.get("path","that", "time","forgot");
		String actual = localCacheUtils.getLocalChecksum(jarFilePath);
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#getLocalChecksum(java.nio.file.Path)}.
	 * @throws FatalClientException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test(expected=FatalClientException.class)
	public void testGetLocalChecksum_io_exception() throws FatalClientException, FileNotFoundException, IOException
	{
		
		PowerMockito.mockStatic(FileFingerPrint.class);
		Mockito.when(FileFingerPrint.create(Mockito.any(Path.class))).thenThrow(new IOException());
		
		ClientSettings settings = load_valid_settings();
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
		Path jarFilePath = Paths.get("src","test","resources", "repoPaths", "1.0", "file1");
		localCacheUtils.getLocalChecksum(jarFilePath);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#buildTargetPath(com.seven10.update_guy.common.release_family.ReleaseFamilyEntry)}.
	 * @throws IOException 
	 * @throws JsonSyntaxException 
	 * @throws FatalClientException 
	 */
	@Test
	public void testBuildTargetPath_valid() throws JsonSyntaxException, IOException, FatalClientException
	{
		String testName = "buildTargetPath";
		Path rootFolder = folder.newFolder(testName).toPath();
		ReleaseFamilyEntry release = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		
		ClientSettings settings = load_valid_settings();
		settings.roleName = "role_1";
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
		
		Path expected = settings.getCachePath().resolve("role_1.txt");
		Path actual = localCacheUtils.buildTargetPath(release);
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#buildTargetPath(com.seven10.update_guy.common.release_family.ReleaseFamilyEntry)}.
	 * @throws IOException 
	 * @throws JsonSyntaxException 
	 * @throws FatalClientException 
	 */
	@Test(expected=FatalClientException.class)
	public void testBuildTargetPath_roleName_not_found() throws JsonSyntaxException, IOException, FatalClientException
	{
		String testName = "buildTargetPath";
		Path rootFolder = folder.newFolder(testName).toPath();
		ReleaseFamilyEntry release = ReleaseFamilyEntryHelpers.create_valid_release_family_entry(testName, 1, rootFolder);
		
		ClientSettings settings = load_valid_settings();
		settings.roleName = "some_stupid_role";
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
		
		localCacheUtils.buildTargetPath(release);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.local.LocalCacheUtils#buildTargetPath(com.seven10.update_guy.common.release_family.ReleaseFamilyEntry)}.
	 * @throws IOException 
	 * @throws JsonSyntaxException 
	 * @throws FatalClientException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testBuildTargetPath_null_release() throws JsonSyntaxException, IOException, FatalClientException
	{
		ReleaseFamilyEntry release = null;
		
		ClientSettings settings = load_valid_settings();
		settings.roleName = "role_1";
		LocalCacheUtils localCacheUtils = new LocalCacheUtils(settings);
		
		localCacheUtils.buildTargetPath(release);
	}
	
}
