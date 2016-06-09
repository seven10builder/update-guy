/**
 * 
 */
package com.seven10.update_guy.manifest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.test_helpers.Factories;
import com.seven10.update_guy.test_helpers.TestHelpers;

/**
 * @author kmm
 *
 */
public class ManifestVersionEntryTest
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestVersionEntryTest.class);
	
	private List<String> createTestRoleSubset(Map<String, Path> roleMap)
	{
		// filter out keys with odd hashs. This should give us a subset of the keys
		return roleMap.keySet().stream().filter(key->key.hashCode()%2 == 0).collect(Collectors.toList());
	}
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testSerializeManifestEntry() throws IOException, RepositoryException
	{
		String releaseFamily = "serialize-me";
		
		ManifestVersionEntry expected = Factories.createValidManifestEntry(releaseFamily, 1);
		
		Gson gson = GsonFactory.getGson();		
		String json = gson.toJson(expected);		
		assertNotNull(json);
		assertFalse(json.isEmpty());
		
		ManifestVersionEntry actual = gson.fromJson(json, ManifestVersionEntry.class);	
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#ManifestVersionEntry()}.
	 */
	@Test
	public void testManifestVersionEntry()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		assertNotNull(versionEntry);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getVersion()}
	 * and {@link com.seven10.update_guy.manifest.ManifestVersionEntry#setVersion(java.lang.String)}.
	 */
	@Test
	public void testGetAndSetVersion()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "getAndSetVersion";
		versionEntry.setVersion(expected);
		String actual = versionEntry.getVersion();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#setVersion(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetVersion_null()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = null;
		versionEntry.setVersion(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#setVersion(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetVersion_empty()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "";
		versionEntry.setVersion(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getPublishDate()}
	 * and {@link com.seven10.update_guy.manifest.ManifestVersionEntry#setPublishDate(java.util.Date)}.
	 */
	@Test
	public void testGetAndSetPublishDate()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		Date expected = new Date(TestHelpers.validDateTimestamp);
		versionEntry.setPublishDate(expected);
		Date actual = versionEntry.getPublishDate();
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#setPublishDate(java.util.Date)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetPublishDate_null()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		Date expected = null;
		versionEntry.setPublishDate(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getPath(java.lang.String)}
	 * and Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#addPath(java.lang.String, java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetAndAddPath() throws IOException
	{
		Path rootPath = folder.newFolder("testGetAndAddPath").toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "getAndAddPath";
		Map<String, Path> roleMap = Factories.createValidRolePaths(expected);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		roleMap.keySet().forEach(
				role->
				{
					Path actual = versionEntry.getPath(role);
					assertEquals(rootPath.resolve(role), actual);
				});
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getPath(java.lang.String)}.
	 */
	@Test
	public void testGetPath_keyNotFound()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "getPath";
		// nothing in there
		Path actual = versionEntry.getPath(expected);
		assertNull(actual);
		// add something
		versionEntry.addPath("notExpected", Paths.get("notExpected"));
		actual = versionEntry.getPath(expected);
		// still not in there
		assertNull(actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getPath(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetPath_key_null()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = null;
		versionEntry.getPath(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getPath(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetPath_key_empty()
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "";
		versionEntry.getPath(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#addPath(java.lang.String, java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddPath_key_null() throws IOException
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = null;
		Path expectedPath = folder.newFolder().toPath();
		versionEntry.addPath(expected, expectedPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#addPath(java.lang.String, java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddPath_key_empty() throws IOException
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "";
		Path expectedPath = folder.newFolder().toPath();
		versionEntry.addPath(expected, expectedPath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#addPath(java.lang.String, java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddPath_path_null() throws IOException
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "validEntry";
		Path expectedPath = null;
		versionEntry.addPath(expected, expectedPath);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getRoles()}.
	 * @throws IOException 
	 */
	@Test
	public void testGetRoles() throws IOException
	{
		Path rootPath = folder.newFolder("testGetRoles").toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "getRoles";
		Map<String, Path> roleMap = Factories.createValidRolePaths(expected);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		roleMap.keySet().forEach(
				role->
				{
					Path actual = rootPath.resolve(versionEntry.getPath(role).toAbsolutePath());
					assertEquals(rootPath.resolve(role), actual);
				});
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getPaths(java.util.Set)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetPaths() throws IOException
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "getPaths";
		Map<String, Path> roleMap = Factories.createValidRolePaths(expected);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));

		List<String> roles = createTestRoleSubset(roleMap);
		logger.debug(".testGetPaths(): roles = %s", Arrays.toString(roles.toArray()) ); 
		
		List<Entry<String, Path>> actual = versionEntry.getPaths(roles);
		
		List<String> actualKeys = actual.stream().map(entry->entry.getKey()).collect(Collectors.toList());
		logger.debug(".testGetPaths(): actualKeys = %s", Arrays.toString(actualKeys.toArray()) ); 
		
		boolean rolesContainsKeys = roles.containsAll(actualKeys);
 		assertTrue(rolesContainsKeys);
		actual.forEach(entry->
				{
					Path path = versionEntry.getPath(entry.getKey());
					assertEquals(path, entry.getValue());
				});
	}

	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getAllPaths()}.
	 * @throws IOException 
	 */
	@Test
	public void testGetAllPaths() throws IOException
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String testName = "getAllPaths";
		Map<String, Path> roleMap = Factories.createValidRolePaths(testName);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		List<Entry<String, Path>> actual = versionEntry.getAllPaths();
		List<Entry<String, Path>> expected = roleMap.entrySet().stream().collect(Collectors.toList());
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_self() throws IOException
	{
		String releaseFamily = "testEquals-self";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		
		// same should equal
		boolean actual = versionEntry.equals(versionEntry);
		assertTrue(actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_clone() throws IOException
	{
		String releaseFamily = "testEquals-clone";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		
		//clone should be equal
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		boolean isEqual = versionEntry.equals(cloneManifest);
		assertTrue(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_versionDiff() throws IOException
	{
		String releaseFamily = "testEquals-created";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		// different object should be different
		cloneManifest.setVersion("something_else");
		
		boolean isEqual = versionEntry.equals(cloneManifest);
		assertFalse(isEqual);
	}

	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_publishedDiff() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		// different object should be different
		cloneManifest.setPublishDate(new Date(31337));
		
		boolean isEqual = versionEntry.equals(cloneManifest);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_fileMapDiff() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		// different object should be different
		cloneManifest.addPath("some-role", folder.newFolder("jibberish").toPath());
		
		boolean isEqual = versionEntry.equals(cloneManifest);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_null() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		// different object should be different
	
		boolean isEqual = versionEntry.equals(null);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_diffClass() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 1);
		// different object should be different
		ManifestVersionEntryTest other =  new ManifestVersionEntryTest();
		boolean isEqual = versionEntry.equals(other);
		assertFalse(isEqual);
	}
}
