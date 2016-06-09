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

import com.google.gson.Gson;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.exceptions.RepositoryException;
import com.seven10.update_guy.test_helpers.Factories;
import com.seven10.update_guy.test_helpers.TestHelpers;

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
		Manifest manifest = new Manifest();
		assertNotNull(manifest);
	}

	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#Manifest(com.seven10.update_guy.manifest.Manifest)}.
	 * @throws IOException 
	 */
	@Test
	public void testManifestManifest_valid_manifest() throws IOException
	{
		String releaseFamily = "manifest-ctor";
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expectedManifest = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));	
		Manifest  actual = new Manifest(expectedManifest);
		assertEquals(expectedManifest, actual);
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
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#getReleaseFamily()}
	 * and {@link com.seven10.update_guy.manifest.Manifest#setReleaseFamily(java.lang.String)}.
	 */
	@Test
	public void testGetAndSetReleaseFamily()
	{
		String expected = "getReleaseFamily";
		Manifest manifest = new Manifest();
		
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
		Manifest manifest = new Manifest();
		
		String expected = null;
		manifest.setReleaseFamily(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setReleaseFamily(java.lang.String)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetReleaseFamily_empty()
	{
		Manifest manifest = new Manifest();
		
		String expected = "";
		manifest.setReleaseFamily(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#getCreated()}.
	 */
	@Test
	public void testGetCreated()
	{
		Manifest manifest = new Manifest();
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
		Manifest manifest = new Manifest();
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
		Manifest manifest = new Manifest();
		Date expected = null;
		manifest.setCreated(expected);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#getRetrieved()}.
	 */
	@Test
	public void testGetRetrieved()
	{
			Manifest manifest = new Manifest();
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
		Manifest manifest = new Manifest();
		Date expected = new Date(TestHelpers.validDateTimestamp);
		manifest.setRetrieved(expected);
		Date actual = manifest.getRetrieved();
		assertEquals(expected,actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#setCreated(java.util.Date)}.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetRetrieved_null_date()
	{
		Manifest manifest = new Manifest();
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
			Manifest manifest = new Manifest();
			Collection<ManifestVersionEntry> expected = Factories.createValidManifestEntries(releaseFamily, i, rootPath);
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
		Manifest manifest = new Manifest();
		ManifestVersionEntry expected = null;
		manifest.addVersionEntry(expected);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testSerializeManifest() throws IOException, RepositoryException
	{
		String releaseFamily = "serialize";
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expectedManifest = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));			
		
		Gson gson = GsonFactory.getGson();
		
		String json = gson.toJson(expectedManifest);
		assertNotNull(json);
		assertFalse(json.isEmpty());
		
		Manifest actual = gson.fromJson(json, Manifest.class);	
		assertNotNull(actual);
		assertEquals(expectedManifest, actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#loadFromFile(java.nio.file.Path)}.
	 * @throws IOException 
	 * @throws RepositoryException 
	 */
	@Test
	public void testWriteToLoadFromFile_valid() throws IOException, RepositoryException
	{
		String releaseFamily = "wt-lf-rvalid";
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expected = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));	
		
		Path targetFile = TestHelpers.combineToManifestFileName(releaseFamily, folder.newFolder().toPath());
		Manifest.writeToFile(targetFile, expected);
		Manifest actual = Manifest.loadFromFile(targetFile);
		
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
		Path filePath = folder.newFolder("loadFromFile_invalid").toPath();
		//create known bad file
		Factories.createInvalidManifestFile(filePath);
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
	public void testLoadFromFile_path_notFound() throws RepositoryException
	{
		// create path that is known NOT to be good
		Path filePath = Paths.get("/no_updateguy_here"); 
		Manifest.loadFromFile(filePath);
	}
	
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_self() throws IOException
	{
		String releaseFamily = "testEquals-self";
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expectedManifest = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));	
		
		// same should equal
		boolean actual = expectedManifest.equals(expectedManifest);
		assertTrue(actual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_clone() throws IOException
	{
		String releaseFamily = "testEquals-clone";
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expectedManifest = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));	
		
		//clone should be equal
		Manifest cloneManifest = new Manifest(expectedManifest);
		boolean isEqual = expectedManifest.equals(cloneManifest);
		assertTrue(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_createdDiff() throws IOException
	{
		String releaseFamily = "testEquals-created";
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expectedManifest = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));	
		Manifest cloneManifest = new Manifest(expectedManifest);
		// different object should be different
		cloneManifest.setCreated(new Date(31337));
		
		boolean isEqual = expectedManifest.equals(cloneManifest);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_releaseFamilyDiff() throws IOException
	{
		String releaseFamily = "testEquals-rf";
		
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expectedManifest = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));	
		
		Manifest cloneManifest = new Manifest(expectedManifest);
		// different object should be different
		cloneManifest.setReleaseFamily("somethingelse");
		
		boolean isEqual = expectedManifest.equals(cloneManifest);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_retrievedDiff() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		Path rootFolder = Factories.createManifestFileForReleaseFamily(releaseFamily, folder);
		Manifest expectedManifest = Factories.createValidManifest(releaseFamily, TestHelpers.combineToManifestFileName(releaseFamily, rootFolder));	
		Manifest cloneManifest = new Manifest(expectedManifest);
		// different object should be different
		cloneManifest.setRetrieved(new Date(31337));
		
		boolean isEqual = expectedManifest.equals(cloneManifest);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_versionsDiff() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		Path manifestPath = ManifestHelpers.build_manifest_path_by_testname(releaseFamily, folder);
		ManifestHelpers.copy_manifest_to_path(ManifestHelpers.validManifestFileName, manifestPath);
		Manifest ours = ManifestHelpers.load_manifest_from_path(manifestPath);
		
			
		Manifest other = new Manifest(ours);
		// different object should be different
		ManifestVersionEntry versionEntry = Factories.createValidManifestEntry(releaseFamily, 0);
		other.addVersionEntry(versionEntry);
		
		boolean isEqual = ours.equals(other);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_null() throws IOException
	{
		Manifest expectedManifest = new Manifest();
		boolean isEqual = expectedManifest.equals(null);
		assertFalse(isEqual);
	}
	/**
	 * Test method for {@link com.seven10.update_guy.manifest.Manifest#equals(Object)}.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testEquals_diffClass() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		Path manifestPath = ManifestHelpers.build_manifest_path_by_testname(releaseFamily, folder);
		ManifestHelpers.copy_manifest_to_path(ManifestHelpers.validManifestFileName, manifestPath);
		Manifest ours = ManifestHelpers.load_manifest_from_path(manifestPath);
		
		ManifestVersionEntry other = new ManifestVersionEntry();
		boolean isEqual = ours.equals(other);
		assertFalse(isEqual);
	}
}
