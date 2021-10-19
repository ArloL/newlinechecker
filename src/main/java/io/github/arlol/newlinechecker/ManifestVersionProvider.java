package io.github.arlol.newlinechecker;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * {@link IVersionProvider} implementation that returns version information from
 * the {@code /META-INF/MANIFEST.MF} file.
 */
public class ManifestVersionProvider {

	public String getVersion() throws Exception {
		Enumeration<URL> resources = ManifestVersionProvider.class
				.getClassLoader()
				.getResources("META-INF/MANIFEST.MF");
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			try {
				Manifest manifest = new Manifest(url.openStream());
				if (isApplicableManifest(manifest)) {
					Attributes attr = manifest.getMainAttributes();
					return get(attr, "Implementation-Title") + " version \""
							+ get(attr, "Implementation-Version") + "\"";
				}
			} catch (IOException ex) {
				return "Unable to read from " + url + ": " + ex;
			}
		}
		return "";
	}

	private boolean isApplicableManifest(Manifest manifest) {
		Attributes attributes = manifest.getMainAttributes();
		return "newlinechecker".equals(get(attributes, "Implementation-Title"));
	}

	private static Object get(Attributes attributes, String key) {
		return attributes.get(new Attributes.Name(key));
	}

}
