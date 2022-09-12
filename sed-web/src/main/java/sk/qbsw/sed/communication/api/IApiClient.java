package sk.qbsw.sed.communication.api;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.http.entity.ContentType;

import sk.qbsw.sed.communication.http.IHttpApiRequest;

/**
 * Interface for API Client - interface which defines methods with which api
 * client can be used.
 * 
 * @param <I> input class
 * @param <O> output class
 * @author Michal Lacko
 * @version 1.10.0
 * @since 1.10.0
 */
public interface IApiClient<I, O> {

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
	public abstract O makeCall(IHttpApiRequest request, String url, I input, Type returnType, ContentType contentType) throws IOException;

	/**
	 * Make call to API.
	 * 
	 * @param request    the request
	 * @param url        the url
	 * @param input      the input
	 * @param returnType the return type
	 * @return the o
	 */
	public abstract O makeCall(IHttpApiRequest request, String url, I input, Type returnType) throws IOException;

	/**
	 * Makes call with specific content.
	 * 
	 * @param request     request to use
	 * @param url         URL to API
	 * @param input       input parameter
	 * @param contentType the content type
	 * @param headers     the headers
	 * @return Returned object (instance of returnClass)
	 */
	public abstract String makeCall(IHttpApiRequest request, String url, I input, ContentType contentType) throws IOException;

	/**
	 * Make call to API.
	 * 
	 * @param request the request
	 * @param url     the url
	 * @param input   the input
	 * @return the string
	 */
	public abstract String makeCall(IHttpApiRequest request, String url, I input) throws IOException;

	/**
	 * Makes call to API.
	 * 
	 * @param request     request to use
	 * @param url         URL to API
	 * @param input       input parameter
	 * @param returnClass Class to return
	 * @return Returned object (instance of returnClass)
	 */
	public abstract O makeCall(IHttpApiRequest request, String url, I input, Class<O> returnClass) throws IOException;

	/**
	 * Make call with specific content.
	 * 
	 * @param request     the request
	 * @param url         the url
	 * @param input       the input
	 * @param returnClass the return class
	 * @param type        the type
	 * @return the o
	 */
	public abstract O makeCall(IHttpApiRequest request, String url, I input, Class<O> returnClass, ContentType type) throws IOException;
}
