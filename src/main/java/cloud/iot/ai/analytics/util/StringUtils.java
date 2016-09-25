package cloud.iot.ai.analytics.util;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StringUtils {
	private static Logger logger = Logger.getLogger(StringUtils.class.getName());
	public static final String lineSeparator = System.getProperty("line.separator", "\n");

	public static byte[] appendByteArray(byte[] oldContent, byte[] appendingContent) {
		int newLength = 0;
		if (oldContent != null) {
			newLength = oldContent.length;
		}
		newLength += appendingContent.length;
		byte[] newContent = new byte[newLength];

		int appendPos = 0;
		if (oldContent != null) {
			appendPos = oldContent.length;
			System.arraycopy(oldContent, 0, newContent, 0, appendPos);
		}
		System.arraycopy(appendingContent, 0, newContent, appendPos, appendingContent.length);

		return newContent;
	}

	public static String encode(String str) {
		String METHOD_NAME = "encode";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("StringUtils", METHOD_NAME);
		}
		String result = null;
		if (str != null) {
			StringBuffer sb = new StringBuffer();
			int len = str.length();
			for (int i = 0; i < len; i++) {
				char c = str.charAt(i);
				switch (c) {
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\n':
					if ((i == 0) || (str.charAt(len - 1) != '\r'))
						sb.append(lineSeparator);
					else
						sb.append('\n');
					break;
				default:
					sb.append(c);
				}
			}

			result = sb.toString();
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("StringUtils", METHOD_NAME);
		}
		return result;
	}

	public static String normalize(String str) {
		String METHOD_NAME = "normalize";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("StringUtils", METHOD_NAME);
		}
		String result = null;
		if (str != null) {
			StringBuffer sb = new StringBuffer();
			int len = str.length();
			for (int i = 0; i < len; i++) {
				char c = str.charAt(i);
				switch (c) {
				case '"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					sb.append(c);
				}
			}

			result = sb.toString();
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("StringUtils", METHOD_NAME);
		}
		return result;
	}

	public static List<String> parseTokens(String content, String token) {
		String METHOD_NAME = "parseTokens";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("StringUtils", METHOD_NAME);
		}
		StringTokenizer strTok = new StringTokenizer(content, token);
		List<String> tokens = new Vector<String>();

		while (strTok.hasMoreTokens()) {
			tokens.add(strTok.nextToken());
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("StringUtils", METHOD_NAME);
		}
		return tokens;
	}

	public static List<String> parseNMTokens(String content) {
		String METHOD_NAME = "parseNMTokens";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("StringUtils", METHOD_NAME);
		}
		List<String> tokens = parseTokens(content, " ");

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("StringUtils", METHOD_NAME);
		}
		return tokens;
	}

	public static String getTokens(List<String> list, String token) {
		String METHOD_NAME = "getTokens";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("StringUtils", METHOD_NAME);
		}
		String result = null;
		if (list != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				sb.append((i > 0 ? token : "") + (String) list.get(i));
			}

			result = sb.toString();
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("StringUtils", METHOD_NAME);
		}
		return result;
	}

	public static String getNMTokens(List<String> list) {
		String METHOD_NAME = "getNMTokens";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("StringUtils", METHOD_NAME);
		}
		String result = getTokens(list, " ");

		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("StringUtils", METHOD_NAME);
		}
		return result;
	}
}
