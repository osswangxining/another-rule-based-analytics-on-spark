package cloud.iot.ai.analytics.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class URLUtils {
	private static Logger logger = Logger.getLogger(URLUtils.class.getName());

	public static URL getURL(URL contextURL, String relativePath) throws MalformedURLException {
		String METHOD_NAME = "getURL";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("URLUtils", METHOD_NAME);
		}
		URL result = null;
		try {
			result = new URL(contextURL, relativePath);
		} catch (MalformedURLException e) {
			result = new File(relativePath).toURI().toURL();
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("URLUtils", METHOD_NAME);
		}
		return result;
	}
}
