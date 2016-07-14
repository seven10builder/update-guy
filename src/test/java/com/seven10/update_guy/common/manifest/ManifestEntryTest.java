/**
 * 
 */
package com.seven10.update_guy.common.manifest;

import static org.junit.Assert.*;
import static com.seven10.update_guy.common.ManifestEntryHelpers.*;
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
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.TestConstants;
import com.seven10.update_guy.common.manifest.ManifestEntry;

/**
 * @author kmm
 *
 */
public class ManifestEntryTest
{
	private static final Logger logger = LogManager.getFormatterLogger(ManifestEntryTest.class);
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testSerializeManifestEntry() throws IOException
	{
		String testName = "serialize-me";
		
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addRoleInfo(role, path));
		
		Gson gson = GsonFactory.getGson();		
		String json = gson.toJson(versionEntry);		
		assertNotNull(json);
		assertFalse(json.isEmpty());
		
		ManifestEntry actual = gson.fromJson(json, ManifestEntry.class);	
		assertNotNull(actual);
		assertEquals(versionEntry, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#ManifestVersionEntry()}.
	 */
	@Test
	public void testManifestVersionEntry()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		assertNotNull(versionEntry);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getVersion()}
	 * and {@link com.seven10.update_guy.common.manifest.ManifestEntry#setVersion(java.lang.String)}.
	 */
	@Test
	public void testGetAndSetVersion()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = "getAndSetVersion";
		versionEntry.setVersion(expected);
		String actual = versionEntry.getVersion();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#setVersion(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetVersion_null()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = null;
		versionEntry.setVersion(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#setVersion(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetVersion_empty()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = "";
		versionEntry.setVersion(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getPublishDate()}
	 * and {@link com.seven10.update_guy.common.manifest.ManifestEntry#setPublishDate(java.util.Date)}.
	 */
	@Test
	public void testGetAndSetPublishDate()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		Date expected = new Date(TestConstants.valid_timestamp);
		versionEntry.setPublishDate(expected);
		Date actual = versionEntry.getPublishDate();
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#setPublishDate(java.util.Date)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetPublishDate_null()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		Date expected = null;
		versionEntry.setPublishDate(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getRoleInfo(java.lang.String)}
	 * and Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#addRoleInfo(java.lang.String, java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetAndAddPath() throws IOException
	{
		String testName = "testGetAndAddPath";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addRoleInfo(role, path));
		
		roleMap.keySet().forEach(
				role->
				{
					Path actual = versionEntry.getRoleInfo(role).getFilePath();
					assertEquals(rootPath.resolve(role + ".txt"), actual);
				});
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getRoleInfo(java.lang.String)}.
	 */
	@Test
	public void testGetPath_keyNotFound()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = "getPath";
		// nothing in there
		UpdateGuyRole actual = versionEntry.getRoleInfo(expected);
		assertNull(actual);
		// add something
		versionEntry.addRoleInfo("notExpected", new UpdateGuyRole(Paths.get("notExpected"), new ArrayList<String>()));
		actual = versionEntry.getRoleInfo(expected);
		// still not in there
		assertNull(actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getRoleInfo(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetPath_key_null()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = null;
		versionEntry.getRoleInfo(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getRoleInfo(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetPath_key_empty()
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = "";
		versionEntry.getRoleInfo(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#addRoleInfo(java.lang.String, java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddPath_key_null() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = null;
		Path expectedPath = folder.newFolder().toPath();
		versionEntry.addRoleInfo(expected, new UpdateGuyRole(expectedPath, new ArrayList<String>()));
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#addRoleInfo(java.lang.String, java.nio.file.Path)}.
	 * @throws IOException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddPath_key_empty() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expected = "";
		Path expectedPath = folder.newFolder().toPath();
		versionEntry.addRoleInfo(expected, new UpdateGuyRole(expectedPath, new ArrayList<String>()));
	}
	
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getRoles()}.
	 * @throws IOException 
	 */
	@Test
	public void testGetRoles() throws IOException
	{
		String testName = "testGetRoles";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, roleInfo)->versionEntry.addRoleInfo(role, roleInfo));
		
		List<String> expectedRoles = new ArrayList<String>(roleMap.keySet());
		List<String> actualRoles = versionEntry.getRoles();
		
		assertTrue(expectedRoles.containsAll(actualRoles));
		assertTrue(actualRoles.containsAll(expectedRoles));
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getRolePaths(java.util.Set)}.
	 * @throws IOException 
	 */
	@Test
	public void testGetPaths() throws IOException
	{
		String testName = "getPaths";
		ManifestEntry versionEntry = new ManifestEntry();
		
		Path rootFolder = folder.newFolder(testName).toPath();
		Map<String, UpdateGuyRole> rolePathList = create_entry_folder_list(TestConstants.version_entry_count, rootFolder);
		// add role/paths to entry
		rolePathList.forEach((key, value)-> versionEntry.addRoleInfo(key, value));

		List<String> roles_subset = create_subset_of_roles(rolePathList);
		logger.debug(".testGetPaths(): roles = %s", Arrays.toString(roles_subset.toArray()) ); 
		
		List<Entry<String, UpdateGuyRole>> actual = versionEntry.getRoleInfos(roles_subset);
		
		// convert entries to list of keys
		List<String> actualKeys = actual.stream().map(entry->entry.getKey()).collect(Collectors.toList());
		logger.debug(".testGetPaths(): actualKeys = %s", Arrays.toString(actualKeys.toArray()) ); 
		
		// all of the keys returned schould be in the roles subset that was requested
		boolean rolesContainsKeys = roles_subset.containsAll(actualKeys);
 		assertTrue(rolesContainsKeys);
		// test each entry to make sure the path returned is the path that we expect
 		actual.forEach(entry->
				{
					UpdateGuyRole roleInfo = versionEntry.getRoleInfo(entry.getKey());
					assertEquals(roleInfo, entry.getValue());
				});
	}

	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getAllRoleInfos()}.
	 * @throws IOException 
	 */
	@Test
	public void testGetAllPaths() throws IOException
	{
		String testName = "getAllPaths";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addRoleInfo(role, path));
		
		List<Entry<String, UpdateGuyRole>> actual = versionEntry.getAllRoleInfos();
		List<Entry<String, UpdateGuyRole>> expected = roleMap.entrySet().stream().collect(Collectors.toList());
		assertTrue(expected.containsAll(actual));
		assertTrue(actual.containsAll(expected));
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_self() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();
		
		// same should equal
		boolean actual = versionEntry.equals(versionEntry);
		assertTrue(actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_clone() throws IOException
	{
		String testName = "testEquals-clone";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addRoleInfo(role, path));
		
		//clone should be equal
		ManifestEntry cloneManifest = new ManifestEntry(versionEntry);
		assertEquals(versionEntry, cloneManifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_versionDiff() throws IOException
	{
		String testName = "testEquals-created";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addRoleInfo(role, path));
		
		ManifestEntry cloneManifest = new ManifestEntry(versionEntry);
		// different object should be different
		cloneManifest.setVersion("something_else");
		assertNotEquals(versionEntry, cloneManifest);
	}

	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_publishedDiff() throws IOException
	{
		String testName = "testEquals-retr";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addRoleInfo(role, path));
		
		ManifestEntry cloneManifest = new ManifestEntry(versionEntry);
		// different object should be different
		cloneManifest.setPublishDate(new Date(31337));
		
		assertNotEquals(versionEntry, cloneManifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_fileMapDiff() throws IOException
	{
		String testName = "testEquals-retr";
		Path rootPath = folder.newFolder(testName).toPath();
		ManifestEntry versionEntry = new ManifestEntry();
		// create roleMap and add to the versionEntry
		Map<String, UpdateGuyRole> roleMap = create_entry_folder_list(TestConstants.version_entry_count, rootPath);
		roleMap.forEach((role, path)->versionEntry.addRoleInfo(role, path));
		
		ManifestEntry cloneManifest = new ManifestEntry(versionEntry);
		assertEquals(versionEntry, cloneManifest);
		// different object should be different
		cloneManifest.addRoleInfo("some-role", new UpdateGuyRole(folder.newFolder("jibberish").toPath(), new ArrayList<String>()));
		
		assertNotEquals(versionEntry, cloneManifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_null() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();

		boolean isEqual = versionEntry.equals(null);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_diffClass() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();
		// different object should be different
		ManifestEntryTest other =  new ManifestEntryTest();
		boolean isEqual = versionEntry.equals(other);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#getReleaseFamily()}
	 * and {@link com.seven10.update_guy.common.manifest.ManifestEntry#setReleaseFamily(java.lang.String)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testGetSetReleaseFamily() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();
		assertEquals("unknown", versionEntry.getReleaseFamily());
		String expectedReleaseFamily = "some-new-family";
		versionEntry.setReleaseFamily(expectedReleaseFamily);
		String actualReleaseFamily = versionEntry.getReleaseFamily();
		assertEquals(expectedReleaseFamily, actualReleaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#setReleaseFamily(java.lang.String)}.
	 * @throws IOException 
	 * 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetReleaseFamily_null() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expectedReleaseFamily = null;
		versionEntry.setReleaseFamily(expectedReleaseFamily);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.common.manifest.ManifestEntry#setReleaseFamily(java.lang.String)}.
	 * @throws IOException 
	 * 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetReleaseFamily_empty() throws IOException
	{
		ManifestEntry versionEntry = new ManifestEntry();
		String expectedReleaseFamily = "";
		versionEntry.setReleaseFamily(expectedReleaseFamily);
	}
}
