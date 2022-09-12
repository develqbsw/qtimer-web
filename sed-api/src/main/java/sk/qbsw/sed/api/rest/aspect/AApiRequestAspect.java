package sk.qbsw.sed.api.rest.aspect;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;

/**
 * Abstract class, that encapsulates commons for api request aspects.
 *
 * @author Marek Martinkovic
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class AApiRequestAspect {
	
	/** Indicate if aspect logs messages. */
	@Value("${aspect.logging}")
	protected Boolean logging;

	/**
	 * Poincut to all controllers with first parameter HttpServletResponse
	 *
	 */
	@Pointcut("execution(* sk.qbsw.sed.api.rest.controller.*.*(String,javax.servlet.http.HttpServletResponse)) && args(json,response)")
	public void apiControllerCallFull(String json, HttpServletResponse response) {
	}

	/**
	 * Poincut to all controller methods with first parameter string
	 */
	@Pointcut("execution(* sk.qbsw.sed.api.rest.controller.*.*(String, ..)) && args(json,..)")
	public void apiControllerCallSimple(String json) {
	}

	/**
	 * pointcut to security calls
	 *
	 * @param json     body of json request
	 * @param session  actual session
	 * @param response actual response
	 */
	@Pointcut("execution(* sk.qbsw.sed.api.rest.controller.*.*(String,javax.servlet.http.HttpSession,javax.servlet.http.HttpServletResponse)) && args(json,session,response)")
	public void apiControllerSecurityCall(String json, HttpSession session, HttpServletResponse response) {
	}

	/**
	 * Log debug message.
	 *
	 * @param <T>        the class used as logger name
	 * @param loggerName the logger name
	 * @param logMessage the log message
	 */

	protected <T> void log(Class<T> loggerName, Level logLevel, String logMessage) {
		log(loggerName, logLevel, logMessage, null);
	}

	protected <T> void log(Class<T> loggerName, Level logLevel, String logMessage, Throwable e) {
		if (logging) {
			Logger.getLogger(loggerName).log(logLevel, logMessage, e);
		}
	}
}
