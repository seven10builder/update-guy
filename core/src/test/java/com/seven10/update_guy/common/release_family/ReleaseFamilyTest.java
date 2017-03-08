/**
 * 
 */
package com.seven10.update_guy.common.release_family;

import static com.seven10.update_guy.common.ReleaseFamilyHelpers.*;
import static com.seven10.update_guy.common.ReleaseFamilyEntryHelpers.*;
import static com.seven10.update_guy.common.TestConstants.*;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.seven10.update_guy.common.GsonFactory;
import com.seven10.update_guy.common.ReleaseFamilyHelpers;
import com.seven10.update_guy.common.exceptions.UpdateGuyException;
import com.seven10.update_guy.common.release_family.ReleaseFamily;
import com.seven10.update_guy.common.release_family.ReleaseFamilyEntry;

/**
 * @author kmm
 *
 */
public class ReleaseFamilyTest
{
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#ReleaseFamily(java.lang.String)}
	 * .
	 */
	@Test
	public void testReleaseFamily_valid_but_empty_releaseFamily()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		assertNotNull(releaseFamily);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#ReleaseFamily(com.seven10.update_guy.common.release_family.ReleaseFamily)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReleaseFamily_valid_releaseFamily() throws IOException
	{
		String releaseFamily = "releaseFamily-ctor";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily expectedReleaseFamily = load_release_family_file_from_path(releaseFamilyPath);
		ReleaseFamily actual = new ReleaseFamily(expectedReleaseFamily);
		assertEquals(expectedReleaseFamily, actual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#ReleaseFamily(com.seven10.update_guy.common.release_family.ReleaseFamily)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testReleaseFamily_null_releaseFamily()
	{
		ReleaseFamily expected = null;
		new ReleaseFamily(expected);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#getReleaseFamily()} and
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#setReleaseFamily(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetAndSetReleaseFamily()
	{
		String expected = "getReleaseFamily";
		ReleaseFamily releaseFamily = new ReleaseFamily();
		assertEquals("unknown", releaseFamily.getReleaseFamily());

		releaseFamily.setReleaseFamily(expected);
		String actual = releaseFamily.getReleaseFamily();

		assertEquals(expected, actual);
		// now make sure the releaseFamily entries got updated
		for(ReleaseFamilyEntry entry: releaseFamily.getVersionEntries())
		{
			actual = entry.getReleaseFamily();
			assertEquals(expected, actual);
		}
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#setReleaseFamily(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetReleaseFamily_null()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();

		String expected = null;
		releaseFamily.setReleaseFamily(expected);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#setReleaseFamily(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetReleaseFamily_empty()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();

		String expected = "";
		releaseFamily.setReleaseFamily(expected);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#getCreated()}.
	 */
	@Test
	public void testGetCreated()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		Date expected = new Date();
		Date actual = releaseFamily.getCreated();
		assertTrue("Dates Should be within a few seconds of each other",
				(actual.getTime() - expected.getTime()) < 5000);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#setCreated(java.util.Date)}
	 * .
	 */
	@Test
	public void testSetCreated_valid()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		Date expected = new Date(valid_timestamp);
		releaseFamily.setCreated(expected);
		Date actual = releaseFamily.getCreated();
		assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#setCreated(java.util.Date)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetCreated_null_date()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		Date expected = null;
		releaseFamily.setCreated(expected);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#getRetrieved()}.
	 */
	@Test
	public void testGetRetrieved()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		Date expected = new Date();
		Date actual = releaseFamily.getRetrieved();
		assertTrue("Dates Should be within a few seconds of each other",
				(actual.getTime() - expected.getTime()) < 5000);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#setRetrieved(java.util.Date)}
	 * .
	 */
	@Test
	public void testSetRetrieved_valid()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		Date expected = new Date(valid_timestamp);
		releaseFamily.setRetrieved(expected);
		Date actual = releaseFamily.getRetrieved();
		assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#setCreated(java.util.Date)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetRetrieved_null_date()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		Date expected = null;
		releaseFamily.setRetrieved(expected);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily# getVersionEntry(String version)}
	 * .
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 * @throws RepositoryException 
	 */
	@Test
	public void test_getVersionEntry_valid() throws IOException, UpdateGuyException
	{
		String testName = "getVersionEntry";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(testName, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily releaseFamily = load_release_family_file_from_path(releaseFamilyPath);
		
		List<ReleaseFamilyEntry> expectedVersionEntries = releaseFamily.getVersionEntries();
		for(ReleaseFamilyEntry expected: expectedVersionEntries)
		{
			ReleaseFamilyEntry actual = releaseFamily.getVersionEntry(expected.getVersion());
			assertEquals(expected, actual);
		}
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily# getVersionEntry(String version)}
	 * .
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 * @throws RepositoryException 
	 */
	@Test(expected=UpdateGuyException.class)
	public void test_getVersionEntry_not_found_entries() throws IOException, UpdateGuyException
	{
		String testName = "getVersionEntry-nf";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(testName, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily releaseFamily = load_release_family_file_from_path(releaseFamilyPath);
		releaseFamily.getVersionEntry("no-way-this-version-exists");
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily# getVersionEntry(String version)}
	 * .
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 * @throws RepositoryException 
	 */
	@Test(expected=UpdateGuyException.class)
	public void test_getVersionEntry_not_found_no_entries() throws IOException, UpdateGuyException
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		releaseFamily.getVersionEntry("no-way-this-version-exists");
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily# getVersionEntry(String version)}
	 * .
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_getVersionEntry_null() throws IOException, UpdateGuyException
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		String version = null;
		releaseFamily.getVersionEntry(version);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily# getVersionEntry(String version)}
	 * .
	 * @throws IOException 
	 * @throws UpdateGuyException 
	 * @throws RepositoryException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_getVersionEntry_empty() throws IOException, UpdateGuyException
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		String version = "";
		releaseFamily.getVersionEntry(version);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#getVersionEntries()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetAndSetVersionEntries() throws IOException
	{
		String testName = "getAndSetVersionEntries";
		
		Path rootPath = folder.newFolder(testName).toPath();
		
		
		for (int i = 0; i < version_entry_count; i++)
		{
			ReleaseFamily releaseFamily = new ReleaseFamily();
			List<ReleaseFamilyEntry> expected = create_valid_release_family_entries(testName, i, rootPath);
			expected.forEach(versionEntry -> releaseFamily.addVersionEntry(versionEntry));

			Collection<ReleaseFamilyEntry> actual = releaseFamily.getVersionEntries();

			assertNotNull(actual);
			assertTrue("version entries should be equal!",
					expected.containsAll(actual) && actual.containsAll(expected));
		}
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#addVersionEntry(com.seven10.update_guy.common.release_family.ReleaseFamilyEntry)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddVersion_null()
	{
		ReleaseFamily releaseFamily = new ReleaseFamily();
		ReleaseFamilyEntry expected = null;
		releaseFamily.addVersionEntry(expected);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#loadFromFile(java.nio.file.Path)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test
	public void testSerializereleaseFamily() throws IOException
	{
		String releaseFamily = "serialize";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily expectedreleaseFamily = load_release_family_file_from_path(releaseFamilyPath);

		Gson gson = GsonFactory.getGson();

		String json = gson.toJson(expectedreleaseFamily);
		assertNotNull(json);
		assertFalse(json.isEmpty());

		ReleaseFamily actual = gson.fromJson(json, ReleaseFamily.class);
		assertNotNull(actual);
		assertEquals(expectedreleaseFamily, actual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#loadFromFile(java.nio.file.Path)}
	 * .
	 * 
	 * @throws IOException
	 * @throws RepositoryException
	 */
	@Test
	public void testWriteToLoadFromFile_valid() throws IOException, UpdateGuyException
	{
		String releaseFamily = "wt-lf-rvalid";

		// setup a releaseFamily to get
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		Path validPath = get_valid_release_family_file_path();
		ReleaseFamily expected = load_release_family_file_from_path(validPath);

		ReleaseFamily.writeToFile(releaseFamilyPath, expected);
		ReleaseFamily actual = ReleaseFamily.loadFromFile(releaseFamilyPath);

		assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#loadFromFile(java.nio.file.Path)}
	 * .
	 * 
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws UpdateGuyException 
	 */
	@Test(expected = UpdateGuyException.class)
	public void testLoadFromFile_invalid_object() throws IOException, UpdateGuyException
	{
		Path filePath = folder.newFolder("loadFromFile_invalid").toPath();
		// create known bad file
		ReleaseFamilyHelpers.create_invalid_release_family_file(filePath);
		ReleaseFamily.loadFromFile(filePath);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#loadFromFile(java.nio.file.Path)}
	 * .
	 * 
	 * @throws RepositoryException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoadFromFile_null_path() throws UpdateGuyException
	{
		Path filePath = null;
		ReleaseFamily.loadFromFile(filePath);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#loadFromFile(java.nio.file.Path)}
	 * .
	 * 
	 * @throws RepositoryException
	 * @throws UpdateGuyException 
	 */
	@Test(expected = UpdateGuyException.class)
	public void testLoadFromFile_path_notFound() throws UpdateGuyException, Exception
	{
		// create path that is known NOT to be good
		Path filePath = Paths.get("/no_updateguy_here");
		ReleaseFamily.loadFromFile(filePath);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_self() throws IOException
	{
		String releaseFamily = "testEquals-self";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily expectedreleaseFamily = load_release_family_file_from_path(releaseFamilyPath);
		
		// same should equal
		boolean actual = expectedreleaseFamily.equals(expectedreleaseFamily);
		assertTrue(actual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_clone() throws IOException
	{
		String releaseFamily = "testEquals-clone";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily expectedreleaseFamily = load_release_family_file_from_path(releaseFamilyPath);

		// clone should be equal
		ReleaseFamily clonereleaseFamily = new ReleaseFamily(expectedreleaseFamily);
		boolean isEqual = expectedreleaseFamily.equals(clonereleaseFamily);
		assertTrue(isEqual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_createdDiff() throws IOException
	{
		String releaseFamily = "testEquals-created";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily expectedreleaseFamily = load_release_family_file_from_path(releaseFamilyPath);
		ReleaseFamily clonereleaseFamily = new ReleaseFamily(expectedreleaseFamily);
		// different object should be different
		clonereleaseFamily.setCreated(new Date(31337));

		boolean isEqual = expectedreleaseFamily.equals(clonereleaseFamily);
		assertFalse(isEqual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_releaseFamilyDiff() throws IOException
	{
		String releaseFamily = "testEquals-rf";

		// setup a releaseFamily to get
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily expected = load_release_family_file_from_path(releaseFamilyPath);
		
		
		
		
		ReleaseFamily clonereleaseFamily = new ReleaseFamily(expected);
		// different object should be different
		clonereleaseFamily.setReleaseFamily("somethingelse");

		boolean isEqual = expected.equals(clonereleaseFamily);
		assertFalse(isEqual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_retrievedDiff() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily expectedreleaseFamily = load_release_family_file_from_path(releaseFamilyPath);
		
		ReleaseFamily clonereleaseFamily = new ReleaseFamily(expectedreleaseFamily);
		// different object should be different
		clonereleaseFamily.setRetrieved(new Date(31337));

		boolean isEqual = expectedreleaseFamily.equals(clonereleaseFamily);
		assertFalse(isEqual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_versionsDiff() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily ours = load_release_family_file_from_path(releaseFamilyPath);

		ReleaseFamily other = new ReleaseFamily(ours);
		// different object should be different
		ReleaseFamilyEntry versionEntry = new ReleaseFamilyEntry();
		versionEntry.setVersion("something-else");
		other.addVersionEntry(versionEntry);

		boolean isEqual = ours.equals(other);
		assertFalse(isEqual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_null() throws IOException
	{
		ReleaseFamily expectedreleaseFamily = new ReleaseFamily();
		boolean isEqual = expectedreleaseFamily.equals(null);
		assertFalse(isEqual);
	}

	/**
	 * Test method for
	 * {@link com.seven10.update_guy.common.release_family.ReleaseFamily#equals(Object)}.
	 * 
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEquals_diffClass() throws IOException
	{
		String releaseFamily = "testEquals-retr";
		Path releaseFamilyPath = build_release_family_file_path_by_testname(releaseFamily, folder);
		copy_release_family_file_to_path(valid_release_family_name, releaseFamilyPath);
		ReleaseFamily ours = load_release_family_file_from_path(releaseFamilyPath);

		ReleaseFamilyEntry other = new ReleaseFamilyEntry();
		boolean isEqual = ours.equals(other);
		assertFalse(isEqual);
	}
}
