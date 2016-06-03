/**
 * 
 */
package com.seven10.update_guy.manifest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.TestHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;

/**
 * @author kmm
 *
 */
public class ManifestVersionEntryTest
{

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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry expected = TestHelpers.createValidVersionEntry(releaseFamily, 1, TestHelpers.versionEntryCount, rootFolder);
		
		Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Path.class, new PathConverter()).create();
		
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
		Map<String, Path> roleMap = TestHelpers.createValidRolePaths(expected, TestHelpers.versionEntryCount, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		roleMap.keySet().forEach(
				role->
				{
					Path actual = versionEntry.getPath(role);
					assertEquals(Paths.get(role), actual);
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
		Map<String, Path> roleMap = TestHelpers.createValidRolePaths(expected, TestHelpers.versionEntryCount, rootPath);
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
		Path rootPath = folder.newFolder("testGetPaths").toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String expected = "getPaths";
		Map<String, Path> roleMap = TestHelpers.createValidRolePaths(expected, TestHelpers.versionEntryCount, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		// filter out keys with odd hashs. This should give us a subset of the keys
		Set<String> roles = roleMap.keySet().stream().filter(key->key.hashCode()%2 == 0).collect(Collectors.toSet());
		
		Set<Entry<String, Path>> actual = versionEntry.getPaths(roles);
		
		Set<String> actualKeys = actual.stream().map(entry->entry.getKey()).collect(Collectors.toSet());
		assertEquals(roles.size(), actual.size());
 		assertTrue(roles.contains(actualKeys));
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
		Path rootPath = folder.newFolder("testGetAllPaths").toPath();
		ManifestVersionEntry versionEntry = new ManifestVersionEntry();
		String testName = "getAllPaths";
		Map<String, Path> roleMap = TestHelpers.createValidRolePaths(testName, TestHelpers.versionEntryCount, rootPath);
		roleMap.forEach((role, path)->versionEntry.addPath(role, path));
		
		Set<Entry<String, Path>> actual = versionEntry.getAllPaths();
		Set<Entry<String, Path>> expected = roleMap.entrySet();
		assertTrue(expected.contains(actual));
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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1, 3, rootFolder);
		
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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1, 3, rootFolder);
		
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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1, 3, rootFolder);
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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1, 3, rootFolder);
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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1, 3, rootFolder);
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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1, 3, rootFolder);
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
		Path rootFolder = folder.newFolder(releaseFamily).toPath();
		ManifestVersionEntry versionEntry = TestHelpers.createValidVersionEntry(releaseFamily, 1, 3, rootFolder);
		// different object should be different
		Manifest other =  TestHelpers.createValidManifest(releaseFamily, rootFolder);
		boolean isEqual = versionEntry.equals(other);
		assertFalse(isEqual);
	}
}