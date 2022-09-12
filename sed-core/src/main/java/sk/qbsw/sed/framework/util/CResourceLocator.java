package sk.qbsw.sed.framework.util;

import java.io.InputStream;
import java.net.URL;

public class CResourceLocator {

	private static final CResourceLocator instance = new CResourceLocator();

	public static InputStream getResourceAsInputStream(final String fileName) {
		final ClassLoader classLoader = instance.getClass().getClassLoader();
		return classLoader.getResourceAsStream(fileName);
	}

	public static URL getResourceURL(final String fileName) {
		final ClassLoader classLoader = instance.getClass().getClassLoader();
		return classLoader.getResource(fileName);
	}

	private CResourceLocator() {
		// do nothing
	}
	
}
