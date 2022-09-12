package sk.qbsw.sed.api.rest.aspect;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Level;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import sk.qbsw.sed.api.rest.service.CResponseService;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.response.EErrorCode;

/**
 * Aspect processor for api requests.
 *
 * @author Marek Martinkovic
 * @version 2.0.0
 * @since 2.0.0
 */
@Aspect
public class CApiRequestProcessor extends AApiRequestAspect {

	@Autowired
	protected CResponseService responseService;

	@Around(value = "apiControllerCallFull(json,response)")
	public Object catchExceptions(final ProceedingJoinPoint pjp, final String json, final HttpServletResponse response) {
		Object result = null;

		try {
			result = pjp.proceed();
		} catch (CApiException e) {
			responseService.writeToResponse(response, responseService.createResponseError(e));
			log(pjp.getTarget().getClass(), Level.ERROR, "Api error: " + e.getMessage(), e);
		} catch (Throwable e) {
			CApiException apiException = new CApiException(EErrorCode.SYSTEM_ERROR);
			responseService.writeToResponse(response, responseService.createResponseError(apiException));
			log(pjp.getTarget().getClass(), Level.ERROR, "Unspecified system error", e);
		}
		return result;
	}

	/**
	 * processor for security/login and security/logout api call
	 *
	 * @param pjp      proceeding join point
	 * @param json     body of json request
	 * @param session  actual session
	 * @param response actual response
	 * @return result of pjp
	 */
	@Around(value = "apiControllerSecurityCall(json,session,response)")
	public Object catchExceptions(final ProceedingJoinPoint pjp, final String json, final HttpSession session, final HttpServletResponse response) {
		Object result = null;

		try {
			result = pjp.proceed();
		} catch (CApiException e) {
			responseService.writeToResponse(response, responseService.createResponseError(e));
			log(pjp.getTarget().getClass(), Level.ERROR, "Api error: " + e.getMessage(), e);
		} catch (Throwable e) {
			CApiException apiException = new CApiException(EErrorCode.SYSTEM_ERROR);
			responseService.writeToResponse(response, responseService.createResponseError(apiException));
			log(pjp.getTarget().getClass(), Level.ERROR, "Unspecified system error", e);
		}
		return result;
	}
}
