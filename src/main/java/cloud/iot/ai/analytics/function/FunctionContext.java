package cloud.iot.ai.analytics.function;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

public class FunctionContext implements XPathFunctionResolver {
	private static Logger logger = Logger.getLogger(FunctionContext.class.getName());
	XPathFunctionResolver[] externalFunctionResolvers = null;

	public FunctionContext(XPathFunctionResolver[] externalFunctionResolvers) {
		this.externalFunctionResolvers = externalFunctionResolvers;
	}

	public XPathFunction resolveFunction(QName functionName, int arity) {
		String METHOD_NAME = "resolveFunction";

		if (logger.isLoggable(Level.FINER)) {
			logger.entering("FunctionContext", METHOD_NAME);
		}
		XPathFunction result = null;

		if ((this.externalFunctionResolvers != null) && (this.externalFunctionResolvers.length > 0)) {
			int size = this.externalFunctionResolvers.length;
			for (int i = 0; i < size; i++) {
				if (this.externalFunctionResolvers[i] != null) {
					result = this.externalFunctionResolvers[i].resolveFunction(functionName, arity);
					if (result != null) {
						break;
					}
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.exiting("FunctionContext", METHOD_NAME);
		}
		return result;
	}
}
