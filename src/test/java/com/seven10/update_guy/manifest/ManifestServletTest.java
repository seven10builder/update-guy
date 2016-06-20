/**
 * 
 */
package com.seven10.update_guy.manifest;

import static com.seven10.update_guy.ManifestHelpers.create_manifest_list;
import static com.seven10.update_guy.ManifestHelpers.write_manifest_list_to_folder;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seven10.update_guy.Globals;
import com.seven10.update_guy.GsonFactory;
import com.seven10.update_guy.repository.RepositoryInfo;

/**
 * @author kmm
 *		
 */
public class ManifestServletTest extends JerseyTest
{
	private static final String SHOW_PATH = "/manifest/show";
	private static final String ACTIVE_VERSION_PATH = "/manifest/activeVersion";
	
	private static final String manifestServletName = "manifest_servlet";

	ManifestMgr manifestMgr;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Override
	protected Application configure()
	{
		ResourceConfig resourceConfig = new ResourceConfig(ManifestServlet.class);
		return resourceConfig;
	}
	
	@Before public void setUpManifests() throws IOException
	{
		// create list of valid manifests
		List<Manifest> expected = create_manifest_list(manifestServletName, 5);
		// save list to files in folder
		Path rootPath = folder.newFolder(manifestServletName).toPath();
		write_manifest_list_to_folder(rootPath, expected);
		
		// create globals variable
		Globals globals = Globals.createGlobals();
		globals.setManifestPath(rootPath);
	}
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#ManifestServlet(com.seven10.update_guy.manifest.ManifestMgr)}
	 * .
	 * @throws IOException 
	 */
	@Test
	public void testManifestServlet_valid() throws IOException
	{
		ManifestServlet servlet = new ManifestServlet();
		assertNotNull(servlet);
	}
		
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getManifest(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetManifest_show_specific_valid()
	{
		WebTarget req = target(SHOW_PATH + "/manifestServletName"+1);
		Builder request = req.request();
		Response resp = request.get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String json = resp.readEntity(String.class);
		Gson gson = GsonFactory.getGson();
		Manifest actual = gson.fromJson(json, Manifest.class);
		assertNotNull(actual);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getManifest(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetManifest_specific_family_not_found()
	{
		Response resp = target(SHOW_PATH + "/this-doesnt-exist").request().get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#getManifests()}.
	 */
	@Test
	public void testGetManifests_show_all_valid()
	{
		Response resp = target(SHOW_PATH + "/").request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String json = resp.readEntity(String.class);
		Gson gson = GsonFactory.getGson();
		
		Type collectionType = new TypeToken<List<RepositoryInfo>>()
		{
		}.getType();
		List<Manifest> actual = gson.fromJson(json, collectionType);
		assertNotNull(actual);
		assertFalse(actual.isEmpty());
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#setActiveVersion(java.lang.String)}
	 * and
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#showActiveVersion()}
	 * .
	 */
	@Test
	public void testGetSetActiveVersion_valid()
	{
		String expected = "1.0";
		Response resp = target(ACTIVE_VERSION_PATH).queryParam("version", expected).request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		// no parameter returns the current active version
		resp = target(ACTIVE_VERSION_PATH).request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		
		String actual = resp.readEntity(String.class);
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#setActiveVersion(java.lang.String)}
	 * and
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#showActiveVersion()}
	 * .
	 */
	@Test
	public void testGetSetActiveVersion_version_not_found()
	{
		// get original version
		Response resp = target(ACTIVE_VERSION_PATH).request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String expected = resp.readEntity(String.class);
		// make request to with bogus parameter
		resp = target(ACTIVE_VERSION_PATH).queryParam("version", "silly-version-value").request().get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		// make sure the active version was not changed
		resp = target(ACTIVE_VERSION_PATH).request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String actual = resp.readEntity(String.class);
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Test method for
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#setActiveVersion(java.lang.String)}
	 * and
	 * {@link com.seven10.update_guy.manifest.ManifestServlet#showActiveVersion()}
	 * .
	 */
	@Test
	public void testGetSetActiveVersion_empty_version()
	{
		// get original version
		Response resp = target(ACTIVE_VERSION_PATH).request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String expected = resp.readEntity(String.class);
		// make request to with bogus parameter
		resp = target(ACTIVE_VERSION_PATH).queryParam("version", "").request().get();
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
		// make sure the active version was not changed
		resp = target(ACTIVE_VERSION_PATH).request().get();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		String actual = resp.readEntity(String.class);
		
		assertEquals(expected, actual);
	}
	
}
