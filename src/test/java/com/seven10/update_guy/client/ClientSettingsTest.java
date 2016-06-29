/**
 * 
 */
package com.seven10.update_guy.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.ClientParameterException;

/**
 * @author kmm
 *
 */
public class ClientSettingsTest
{
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.ClientSettings#loadConfig(java.nio.file.Path)}.
	 * @throws ClientParameterException 
	 * @throws IOException 
	 */
	@Test
	public void testLoadConfig_valid() throws ClientParameterException, IOException
	{
		Path destFile = Paths.get("src", "test", "resources", "clientConfig", "valid.json");
		String json = FileUtils.readFileToString(destFile.toFile(), GsonFactory.encodingType);
		ClientSettings expected = GsonFactory.getGson().fromJson(json, ClientSettings.class);
		
		ClientSettings actual = ClientSettings.loadConfig(destFile);
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.client.ClientSettings#loadConfig(java.nio.file.Path)}.
	 * @throws ClientParameterException 
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testLoadConfig_null_path() throws ClientParameterException, IOException
	{
		
		Path destFile = null;
		ClientSettings.loadConfig(destFile);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.ClientSettings#loadConfig(java.nio.file.Path)}.
	 * @throws ClientParameterException 
	 * @throws IOException 
	 */
	@Test(expected=ClientParameterException.class)
	public void testLoadConfig_file_not_exist() throws ClientParameterException, IOException
	{
		
		Path destFile = Paths.get("src", "test", "resources", "clientConfig", "this-doesnt-exist.json");
		ClientSettings.loadConfig(destFile);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.client.ClientSettings#equals(java.Object)}.
	 * @throws ClientParameterException 
	 * @throws IOException 
	 */
	@Test
	public void testEquals_valid() throws ClientParameterException, IOException
	{
		Path destFile = Paths.get("src", "test", "resources", "clientConfig", "valid.json");
		String json = FileUtils.readFileToString(destFile.toFile(), GsonFactory.encodingType);
		ClientSettings expected = GsonFactory.getGson().fromJson(json, ClientSettings.class);
		// test same are equal
		assertEquals(expected, expected);
		
		ClientSettings actual = GsonFactory.getGson().fromJson(json, ClientSettings.class);
		// test if equal objects are equal
		assertEquals(expected, actual);
		
		actual.setReleaseFamily(actual.releaseFamily + "modified");
		assertNotEquals(expected, actual);
		actual.setReleaseFamily(expected.releaseFamily);
		assertEquals(expected, actual);
		
		actual.setRepoId(actual.repoId + "modified");
		assertNotEquals(expected, actual);
		actual.setRepoId(expected.repoId);
		assertEquals(expected, actual);
		
		actual.setRoleName(actual.roleName + "modified");
		assertNotEquals(expected, actual);
		actual.setRoleName(expected.roleName);
		assertEquals(expected, actual);
		
		actual.setServerAddress(actual.serverAddress + "modified");
		assertNotEquals(expected, actual);
		actual.setServerAddress(expected.serverAddress);
		assertEquals(expected, actual);
		
		actual.setServerPort(actual.serverPort + 1);
		assertNotEquals(expected, actual);
		actual.setServerPort(expected.serverPort);
		assertEquals(expected, actual);
		
		// test nulls are never equal
		actual = null;
		assertNotEquals(expected, actual);
		
		// test different types are not the same
		assertNotEquals(expected, this);
	}
	
}
