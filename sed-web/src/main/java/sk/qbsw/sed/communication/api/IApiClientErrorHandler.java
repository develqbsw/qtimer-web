package sk.qbsw.sed.communication.api;

/**
 * Interface error hander for API Client - interface which defines methods to
 * handle error from webservice
 * 
 * @param <E> error class to which are data serialized
 * @author Michal Lacko
 * @version 1.10.0
 * @since 1.10.0
 */
public interface IApiClientErrorHandler<E> {

	/**
	 * Makes call with specific content.
	 * 
	 * @param request     request to use
	 * @param url         URL to API
	 * @param input       input parameter
	 * @param returnType  Type to return
	 * @param contentType the content type
	 * @return Returned object (instance of returnClass)
	 */
	public abstract E handleError(int errorCode, String response);
}
