package com.seven10.update_guy.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.seven10.update_guy.common.Globals;
import com.seven10.update_guy.server.manifest.ManifestServlet;
import com.seven10.update_guy.server.release.ReleaseServlet;
import com.seven10.update_guy.server.repository.RepositoryServlet;

public class UpdateGuyServer
{
	private static final Logger logger = LogManager.getFormatterLogger(UpdateGuyServer.class);
	private static String createServletsString()
	{
		List<String> servlets = new ArrayList<String>();
		servlets.add(RepositoryServlet.class.getCanonicalName());
		servlets.add(ManifestServlet.class.getCanonicalName());
		servlets.add(ReleaseServlet.class.getCanonicalName());
		
		String s = String.join("; ", servlets);
		return s;
	}
	
	public static void main(String[] args) throws Exception
	{
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		Server jettyServer = new Server(Globals.getServerPort());
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		String servlets = createServletsString();
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", servlets);

		try
		{
			jettyServer.start();
			jettyServer.join();
		}
		catch(Exception ex)
		{
			logger.error(".main(): caught exception - %s ", ex.getMessage());
		}
		finally
		{
			jettyServer.destroy();
		}
	}
}