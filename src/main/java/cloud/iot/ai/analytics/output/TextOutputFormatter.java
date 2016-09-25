package cloud.iot.ai.analytics.output;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextOutputFormatter implements Formatter {
	private static Logger logger = Logger.getLogger(TextOutputFormatter.class.getName());

	private List<String> result = new ArrayList<String>();
	
	public void format(String output) {
		String METHOD_NAME = "format";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("TextOutputFormatter", METHOD_NAME);
		}
		
		result.add(output);
		
		if (logger.isLoggable(Level.FINER))
			logger.exiting("TextOutputFormatter", METHOD_NAME);
	}
	
	/**
	 * @return the result
	 */
	public List<String> getResult() {
		return result;
	}
}
