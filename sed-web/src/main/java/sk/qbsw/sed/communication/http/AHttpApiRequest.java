package sk.qbsw.sed.communication.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import sk.qbsw.sed.communication.model.CInvokerInfo;

public abstract class AHttpApiRequest implements IHttpApiRequest {
	protected final CInvokerInfo info;

	/** The repeat count. */
	private int repeatCount = 1;

	/** Timetout of HTTP request. */
	private int timeout = 30000;

	/** The proxy. */
	private HttpHost proxy;

	/**
	 * strings which are append to http header
	 */
	private Map<String, String> headers;

	private CookieStore cookies;

	public AHttpApiRequest(CInvokerInfo info) {
		super();
		this.info = info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.api.client.http.IHttpApiRequest#setRepeatCount(int)
	 */
	@Override
	public void setRepeatCount(int repeat) {
		this.repeatCount = repeat;
	}

	/**
	 * Gets the timeout.
	 *
	 * @return the timeout
	 */
	protected int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the timeout in milliseconds.
	 *
	 * @param timeout the new timeout in milliseconds
	 */
	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.api.client.http.IHttpApiRequest#makeCall(java.lang.String,
	 * org.apache.http.entity.ContentType, java.lang.String)
	 */
	@Override
	public final String makeCall(String url, ContentType contentType, String entity) throws IOException {
		CApiHttpException lastEx;

		try {
			// try call
			return makeOneCall(url, contentType, entity);
		} catch (CApiHttpException ex) {
			lastEx = ex;
		}

		for (int counter = 1; counter < repeatCount; counter++) {
			try {
				// try call
				return makeOneCall(url, contentType, entity);
			} catch (CApiHttpException ex) {
				lastEx = ex;
			}
		}

		// throws last exception
		throw new CApiHttpException("Repeated call failed", lastEx, lastEx.getHttpErrorCode(), lastEx.getResponse());
	}

	/**
	 * Sets the proxy.
	 *
	 * @param proxy the new proxy
	 */
	@Override
	public void setProxy(HttpHost proxy) {
		this.proxy = proxy;
	}

	/**
	 * Gets the proxy.
	 *
	 * @return the proxy
	 */
	protected HttpHost getProxy() {
		return this.proxy;
	}

	/**
	 * Apply timeouts settings.
	 *
	 */
	protected RequestConfig getRequestConfig() {
		return RequestConfig.custom().setConnectTimeout(getTimeout()).setSocketTimeout(getTimeout()).setConnectionRequestTimeout(getTimeout()).build();
	}

	/**
	 * Make one call of API request.
	 *
	 * @param url          the url
	 * @param contentType  the content type
	 * @param entityInJSon the entity in j son
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected abstract String makeOneCall(String url, ContentType contentType, String entityInJSon) throws IOException;

	/**
	 * Gets the entity content.
	 *
	 * @param response the response
	 * @return the entity content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected String getEntityContent(HttpResponse response) throws IOException {
		StringBuilder output = new StringBuilder();

		// create readers
		try (Reader inputReader = createInputStreamReader(response.getEntity())) {

			// reads output
			try (BufferedReader br = new BufferedReader(inputReader)) {
				String line;
				while ((line = br.readLine()) != null) {
					output.append(line);
				}
			}
		}
		return output.toString();
	}

	/**
	 * Creates the input stream reader.
	 *
	 * @param httpEntity the http entity
	 * @return the reader
	 * @throws IllegalStateException the illegal state exception
	 * @throws IOException           Signals that an I/O exception has occurred.
	 */
	private Reader createInputStreamReader(HttpEntity httpEntity) throws IOException {
		// get charset from response
		ContentType contentType = ContentType.get(httpEntity);
		Charset charset = (contentType != null) ? contentType.getCharset() : null;

		// create input stream with of without charset
		if (charset != null) {
			return new InputStreamReader(httpEntity.getContent(), charset);
		} else {
			return new InputStreamReader(httpEntity.getContent());
		}
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * Sets parameter for content. The content will send as Entity in the POST
	 * request.
	 */
	@Override
	public void setContentParameter(String parameterName) {
		// nothing to do
	}

	public HttpResponse execute(CloseableHttpClient httpClient, HttpUriRequest request) throws IOException {
		HttpClientContext context = HttpClientContext.create();

		if (info != null && info.getCookies() != null) {
			cookies = info.getCookies();
			context.setCookieStore(cookies);
		}
		CloseableHttpResponse response = httpClient.execute(request, context);
		cookies = context.getCookieStore();
		return response;
	}

	public CookieStore getCookies() {
		return cookies;
	}
}
