/**
 * 
 */
package com.seven10.update_guy.manifest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.seven10.update_guy.TestHelpers;
import com.seven10.update_guy.exceptions.RepositoryException;

/**
 * @author kmm
 *
 */
public class ManifestTest
{
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#Manifest(java.lang.String)}.
	 */
	@Test
	public void testManifest_valid_releaseFamily()
	{
		String releaseFamily = "releaseFamily";
		Manifest manifest = new Manifest(releaseFamily);
		assertNotNull(manifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#Manifest(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifest_null_releaseFamily()
	{
		String releaseFamily = null;
		Manifest manifest = new Manifest(releaseFamily);
		assertNotNull(manifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#Manifest(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifest_empty_releaseFamily()
	{
		String releaseFamily = "";
		Manifest manifest = new Manifest(releaseFamily);
		assertNotNull(manifest);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#Manifest(com.seven10.update_guy.manifest.Manifest)}.
	 */
	@Test
	public void testManifestManifest_valid_manifest()
	{
		Manifest expected = TestHelpers.createMockedManifest("valid_ctor",  new Date(), TestHelpers.versionEntryCount);
		Manifest  actual = new Manifest(expected);
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#Manifest(com.seven10.update_guy.manifest.Manifest)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testManifestManifest_null_manifest()
	{
		Manifest expected = null;
		new Manifest(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#getReleaseFamily()}.
	 */
	@Test
	public void testGetReleaseFamily()
	{
		String expected = "getReleaseFamily";
		Manifest manifest = new Manifest(expected);
		
		String actual = manifest.getReleaseFamily();
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setReleaseFamily(java.lang.String)}.
	 */
	@Test
	public void testSetReleaseFamily_valid()
	{
		Manifest manifest = new Manifest("replaceMe");
		
		String expected = "setReleaseFamily";
		manifest.setReleaseFamily(expected);
		
		String actual = manifest.getReleaseFamily();
		assertEquals(expected, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setReleaseFamily(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetReleaseFamily_null()
	{
		Manifest manifest = new Manifest("replaceMe");
		
		String expected = null;
		manifest.setReleaseFamily(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setReleaseFamily(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetReleaseFamily_empty()
	{
		Manifest manifest = new Manifest("replaceMe");
		
		String expected = "";
		manifest.setReleaseFamily(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#getCreated()}.
	 */
	@Test
	public void testGetCreated()
	{
		Manifest manifest = new Manifest("getCreated");
		Date expected = new Date();
		Date actual = manifest.getCreated();
		assertTrue("Dates Should be within a few seconds of each other", (actual.getTime() - expected.getTime()) < 5000);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setCreated(java.util.Date)}.
	 */
	@Test
	public void testSetCreated_valid()
	{
		Manifest manifest = new Manifest("setCreated");
		Date expected = new Date(TestHelpers.validDateTimestamp);
		manifest.setCreated(expected);
		Date actual = manifest.getCreated();
		assertEquals(expected,actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setCreated(java.util.Date)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetCreated_null_date()
	{
		Manifest manifest = new Manifest("setCreated");
		Date expected = null;
		manifest.setCreated(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#getRetrieved()}.
	 */
	@Test
	public void testGetRetrieved()
	{
			Manifest manifest = new Manifest("getRetrieved");
			Date expected = new Date();
			Date actual = manifest.getRetrieved();
			assertTrue("Dates Should be within a few seconds of each other", (actual.getTime() - expected.getTime()) < 5000);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setRetrieved(java.util.Date)}.
	 */
	@Test
	public void testSetRetrieved_valid()
	{
		Manifest manifest = new Manifest("setRetrieved");
		Date expected = new Date(TestHelpers.validDateTimestamp);
		manifest.setRetrieved(expected);
		Date actual = manifest.getCreated();
		assertEquals(expected,actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setCreated(java.util.Date)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetRetrieved_null_date()
	{
		Manifest manifest = new Manifest("setRetrieved");
		Date expected = null;
		manifest.setRetrieved(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#getVersionEntries()}.
	 * @throws IOException 
	 */
	@Test
	public void testGetAndSetVersionEntries() throws IOException
	{
		Path rootPath = folder.newFolder("getAndSetVersionEntries").toPath();
		String releaseFamily = "getVersionEntries";
		for(int i = 0; i < TestHelpers.versionEntryCount; i++)
		{
			Manifest manifest = new Manifest(releaseFamily);
			Collection<ManifestVersionEntry> expected = TestHelpers.createValidManifestEntries(releaseFamily, i, rootPath);
			expected.forEach(versionEntry ->manifest.addVersionEntry(versionEntry));
			
			Collection<ManifestVersionEntry> actual = manifest.getVersionEntries();
			
			assertNotNull(actual);
			assertTrue("version entries should be equal!", expected.containsAll(actual) && actual.containsAll(expected));
		}
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#addVersionEntry(com.seven10.update_guy.manifest.ManifestVersionEntry)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testAddVersion_null()
	{
		String releaseFamily = "addVersionEntry";
		Manifest manifest = new Manifest(releaseFamily);
		ManifestVersionEntry expected = null;
		manifest.addVersionEntry(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testLoadFromFile_valid() throws IOException, RepositoryException
	{
		Manifest expected = TestHelpers.createMockedManifest("loadFromFile", new Date(), TestHelpers.versionEntryCount);
		Path filePath = folder.newFile("loadFromFile.manifest").toPath();
		TestHelpers.createValidManifestFile(expected, filePath);
		//load from file should be successful
		Manifest actual = Manifest.loadFromFile(filePath);
		assertEquals(expected, actual);
	}

	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	@Test(expected=RepositoryException.class)
	public void testLoadFromFile_invalid_object() throws RepositoryException, IOException
	{
		Path filePath = folder.newFile("loadFromFile_invalid.manifest").toPath();
		//create known bad file
		TestHelpers.createInvalidManifestFile(filePath);
		Manifest.loadFromFile(filePath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testLoadFromFile_null_path() throws RepositoryException
	{
		Path filePath = null;
		Manifest.loadFromFile(filePath);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws RepositoryException 
	 */
	@Test(expected=RepositoryException.class)
	public void testLoadFromFile__path_notFound() throws RepositoryException
	{
		// create path that is known NOT to be good
		Path filePath = Paths.get("/no_updateguy_here"); 
		Manifest.loadFromFile(filePath);
	}
}
