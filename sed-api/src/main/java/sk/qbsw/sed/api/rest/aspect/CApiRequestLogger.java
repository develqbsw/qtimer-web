package sk.qbsw.sed.api.rest.aspect;

import org.apache.log4j.Level;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Aspect logger for api requests.
 *
 * @author martinkovic
 * @since 2.0.0
 * @version 2.0.0
 */
@Aspect
public class CApiRequestLogger extends AApiRequestAspect {
	
	/**
	 * Log at the beginning of the request.
	 *
	 * @param joinPoint the join point
	 * @param request the request
	 */
	@Before("apiControllerCallSimple(json)")
	public void logBeginning(JoinPoint joinPoint, String json) {
		StringBuilder builder = new StringBuilder();

		if (json != null && json.contains("password") && json.contains("\"")) {
			// password nechceme logovat
			int indexOfPassword = json.indexOf("password");
			String jsonStartingWithPassword = json.substring(indexOfPassword + 11);
			String password = jsonStartingWithPassword.substring(0, jsonStartingWithPassword.indexOf("\""));

			json = json.replace(password, "******");
		}

		if (json != null && json.contains("photo\":[")) {
			// byte array nechceme logovat
			json = json.replaceAll("photo\":\\[[0-9,-]*\\]", "photo\":[byteArray]");
		}

		builder.append("Begining of the API method ").append(joinPoint.getSignature().getName()).append(" with request: ").append(json);

		log(joinPoint.getTarget().getClass(), Level.DEBUG, builder.toString());
	}

	/**
	 * Log end of the request.
	 *
	 * @param joinPoint the join point
	 * @param request   the request
	 */
	@AfterReturning("apiControllerCallSimple(json)")
	public void logEnd(JoinPoint joinPoint, String json) {
		StringBuilder builder = new StringBuilder();
		builder.append("End of the API method ").append(joinPoint.getSignature().getName());

		log(joinPoint.getTarget().getClass(), Level.DEBUG, builder.toString());
	}
}
