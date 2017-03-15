package com.seven10.update_guy.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/*")
public class ApplicationConfig extends Application
{
	@Override
	public Set<Class<?>> getClasses()
	{
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(org.glassfish.jersey.server.mvc.jsp.JspMvcFeature.class);
		return s;
	}
}
