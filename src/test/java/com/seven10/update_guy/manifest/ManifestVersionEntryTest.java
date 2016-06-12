/**
 * 
 */
package com.seven10.update_guy.manifest;

import static org.junit.Assert.*;
import static com.seven10.update_guy.ManifestEntryHelpers.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import com.seven10.update_guy.TestConstants;
import com.seven10.update_guy.exceptions.RepositoryException;

/**
 * @author kmm
 *
 */
public class ManifestVersionEntryTest
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestVersionEntryTest.class);
	
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
		String testName = "serialize-me";
		
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		Gson gson = GsonFactory.getGson();		
		String json = gson.toJson(versionEntry);		
		assertNotNull(json);
		assertFalse(json.isEmpty());
		
		ManifestVersionEntry actual = gson.fromJson(json, ManifestVersionEntry.class);	
		assertNotNull(actual);
		assertEquals(versionEntry, actual);
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
		Date expected = new Date(TestConstants.valid_timestamp);
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
		String testName = "testGetAndAddPath";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		roleMap.keySet().forEach(
				role->
				{
					Path actual = versionEntry.getPath(role);
					assertEquals(rootPath.resolve(role + ".txt"), actual);
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
		String testName = "testGetRoles";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		List<String> expectedRoles = new ArrayList<String>(roleMap.keySet());
		List<String> actualRoles = versionEntry.getRoles();
		
		assertTrue(expectedRoles.containsAll(actualRoles));
		assertTrue(actualRoles.containsAll(expectedRoles));
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getRolePaths(java.util.Set)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetPaths() throws IOException
	{
		String testName = "getPaths";
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		
		Path rootFolder = folder.newFolder(testName).toPath();
		Map<String, Path> rolePathList = create_entry_folder_list(TestConstants.version_entry_count, rootFolder);
		// add role/paths to entry
		rolePathList.forEach((key, value)-> versionEntry.addPath(key, value));

		List<String> roles_subset = create_subset_of_roles(rolePathList);
		logger.debug(".testGetPaths(): roles = %s", Arrays.toString(roles_subset.toArray()) ); 
		
		List<Entry<String, Path>> actual = versionEntry.getRolePaths(roles_subset);
		
		// convert entries to list of keys
		List<String> actualKeys = actual.stream().map(entry->entry.getKey()).collect(Collectors.toList());
		logger.debug(".testGetPaths(): actualKeys = %s", Arrays.toString(actualKeys.toArray()) ); 
		
		// all of the keys returned schould be in the roles subset that was requested
		boolean rolesContainsKeys = roles_subset.containsAll(actualKeys);
 		assertTrue(rolesContainsKeys);
		// test each entry to make sure the path returned is the path that we expect
 		actual.forEach(entry->
				{
					Path path = versionEntry.getPath(entry.getKey());
					assertEquals(path, entry.getValue());
				});
	}

	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#getAllRolePaths()}.
	 * @throws IOException 
	 */
	@Test
	public void testGetAllPaths() throws IOException
	{
		String testName = "getAllPaths";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		List<Entry<String, Path>> actual = versionEntry.getAllRolePaths();
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
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		
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
		String testName = "testEquals-clone";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		//clone should be equal
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		assertEquals(versionEntry, cloneManifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_versionDiff() throws IOException
	{
		String testName = "testEquals-created";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		// different object should be different
		cloneManifest.setVersion("something_else");
		assertNotEquals(versionEntry, cloneManifest);
	}

	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_publishedDiff() throws IOException
	{
		String testName = "testEquals-retr";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		// different object should be different
		cloneManifest.setPublishDate(new Date(31337));
		
		assertNotEquals(versionEntry, cloneManifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_fileMapDiff() throws IOException
	{
		String testName = "testEquals-retr";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// create roleMap and add to the versionEntry
		Map<String, Path> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		ManifestVersionEntry cloneManifest = new ManifestVersionEntry(versionEntry);
		assertEquals(versionEntry, cloneManifest);
		// different object should be different
		cloneManifest.addPath("some-role", folder.newFolder("jibberish").toPath());
		
		assertNotEquals(versionEntry, cloneManifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.ManifestVersionEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_null() throws IOException
	{
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();

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
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		// different object should be different
		ManifestVersionEntryTest other =  new ManifestVersionEntryTest();
		boolean isEqual = versionEntry.equals(other);
		assertFalse(isEqual);
	}
}
