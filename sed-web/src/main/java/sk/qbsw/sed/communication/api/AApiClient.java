package sk.qbsw.sed.communication.api;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.http.entity.ContentType;

import sk.qbsw.sed.communication.http.CApiHttpException;
import sk.qbsw.sed.communication.http.IHttpApiRequest;

public abstract class AApiClient<I, O> implements IApiClient<I, O> {

	private IApiClientErrorHandler<?> handler;

	/**
	 * Instantiates a new a api client.
	 */
	public AApiClient() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.api.client.Abc#makeCall(sk.qbsw.core.api.client.http.
	 * IHttpApiRequest, java.lang.String, I)
	 */
	@Override
	public String makeCall(IHttpApiRequest request, String url, I input) throws IOException {
		return this.makeCall(request, url, input, ContentType.APPLICATION_JSON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.api.client.Abc#makeCall(sk.qbsw.core.api.client.http.
	 * IHttpApiRequest, java.lang.String, I, java.lang.Class)
	 */
	@Override
	public O makeCall(IHttpApiRequest request, String url, I input, Class<O> returnClass) throws IOException {
		return makeCall(request, url, input, (Type) returnClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.api.client.Abc#makeCall(sk.qbsw.core.api.client.http.
	 * IHttpApiRequest, java.lang.String, I, java.lang.Class,
	 * org.apache.http.entity.ContentType)
	 */
	@Override
	public O makeCall(IHttpApiRequest request, String url, I input, Class<O> returnClass, ContentType type) throws IOException {
		return makeCall(request, url, input, (Type) returnClass, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sk.qbsw.core.api.client.IApiClient#makeCall(sk.qbsw.core.api.client.http.
	 * IHttpApiRequest, java.lang.String, java.lang.Object, java.lang.reflect.Type,
	 * org.apache.http.entity.ContentType)
	 */

	public String makeCall(IHttpApiRequest request, String url, ContentType contentType, String inputData) throws IOException {
		try {

			// process request
			return request.makeCall(url, contentType, inputData);

		} catch (CApiHttpException e) {
			// if handler is not null then invoke handler to deserialize response
			if (handler != null) {
				e.setObjectResponse(handler.handleError(e.getHttpErrorCode(), e.getResponse()));
			}
			throw e;
		}
	}

	/**
	 * @return the handler
	 */
	public IApiClientErrorHandler<?> getHandler() {
		return handler;
	}

	/**
	 * @param handler the handler to set
	 */
	public void setHandler(IApiClientErrorHandler<?> handler) {
		this.handler = handler;
	}
}
