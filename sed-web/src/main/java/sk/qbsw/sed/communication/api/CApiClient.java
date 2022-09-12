package sk.qbsw.sed.communication.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CResponseBase;
import sk.qbsw.sed.communication.http.CApiHttpException;
import sk.qbsw.sed.communication.http.CRequestModificator;
import sk.qbsw.sed.communication.http.IHttpApiRequest;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionModel;
import sk.qbsw.sed.fw.exception.EClientErrorCode;

public class CApiClient<I, O> extends AApiClient<I, O> {
	protected static final Logger LOGGER = LoggerFactory.getLogger(CApiClient.class);

	private GsonBuilder builder;

	/** The encode api value. */
	boolean encodeApiValue;

	/**
	 * Instantiates a new api client.
	 */
	public CApiClient() {
		// create builder
		this.builder = new GsonBuilder();
		// initialize builder and register default adapters
		this.builder = prepareGsonBuilder(builder);
		CRequestModificator.createBuilder(this.builder);
	}

	/**
	 * Instantiates a new c api client.
	 * 
	 * @param encodeApiValue the encode api value
	 */
	public CApiClient(boolean encodeApiValue) {
		this();
		this.encodeApiValue = encodeApiValue;
	}

	@SuppressWarnings("unchecked")
	public O makeCallExc(IHttpApiRequest request, String url, I input, Type returnType, ContentType contentType) throws CBussinessDataException {
		// create gson from builder
		Gson gson = this.builder.create();

		// process request
		String response = "";
		
		try {
			response = makeCall(request, url, input, contentType);
			/*
			 * process response have to be in try catch block, because in rare situation no
			 * exception is rised during makeCall, but the response is not valid json object
			 * so the exception is thrown in fromJson methods below
			 */
			O responseObject = (O) gson.fromJson(response, returnType);
			CResponseBase errorResponse = gson.fromJson(response, CResponseBase.class);
			if (isError(errorResponse)) {
				if ("USER_TIMEOUT".equals(errorResponse.getErrorMessage())) {
					throw new CBussinessDataException(new CBussinessDataExceptionModel(EClientErrorCode.NEEDS_TO_LOGIN));
				}
				throw new CBussinessDataException(new CBussinessDataExceptionModel(errorResponse));
			}
			return responseObject;
		} catch (Exception e) {
			LOGGER.error("error in api call on url: " + url, e);
			if (e instanceof IllegalArgumentException) {
				throw new CBussinessDataException(new CBussinessDataExceptionModel(EClientErrorCode.BAD_URL));
			}
			if (e instanceof CBussinessDataException) {
				throw (CBussinessDataException) e;
			}
			if (e instanceof CApiHttpException) {
				CApiHttpException ex = (CApiHttpException) e;
				CResponseBase errorResponse = null;
				if (ex.getHttpErrorCode() == 401) {
					throw new CBussinessDataException(new CBussinessDataExceptionModel(EClientErrorCode.NEEDS_TO_LOGIN));
				}
				try {
					errorResponse = gson.fromJson(ex.getResponse(), CResponse.class);
				} catch (Exception exc) {
					LOGGER.error("proccesing error response from: " + response, exc);
				}
				if (isError(errorResponse)) {
					throw new CBussinessDataException(new CBussinessDataExceptionModel(errorResponse));
				} else {
					throw new CBussinessDataException(new CBussinessDataExceptionModel(EClientErrorCode.CONNECTION_FALIED));

				}

			} else {
				throw new CBussinessDataException(new CBussinessDataExceptionModel(EClientErrorCode.CONNECTION_FALIED));

			}
		}
	}

	private boolean isError(CResponseBase errorResponse) {
		return errorResponse != null && (errorResponse.getErrorCode() != null || errorResponse.getErrorMessage() != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sk.qbsw.core.api.client.IApiClient#makeCall(sk.qbsw.core.api.client.http.
	 * IHttpApiRequest, java.lang.String, I, java.lang.reflect.Type,
	 * org.apache.http.entity.ContentType)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public O makeCall(IHttpApiRequest request, String url, I input, Type returnType, ContentType contentType) throws IOException {
		// create gson from builder
		Gson gson = this.builder.create();

		// process request
		String response = makeCall(request, url, input, contentType);

		// process response
		O responseObject = (O) gson.fromJson(response, returnType);
		return responseObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sk.qbsw.core.api.client.IApiClient#makeCall(sk.qbsw.core.api.client.http.
	 * IHttpApiRequest, java.lang.String, I, java.lang.reflect.Type)
	 */
	@Override
	public O makeCall(IHttpApiRequest request, String url, I input, Type returnType) throws IOException {
		return this.makeCall(request, url, input, returnType, ContentType.APPLICATION_JSON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sk.qbsw.core.api.client.IApiClient#makeCall(sk.qbsw.core.api.client.http.
	 * IHttpApiRequest, java.lang.String, I, org.apache.http.entity.ContentType)
	 */
	@Override
	public String makeCall(IHttpApiRequest request, String url, I input, ContentType contentType) throws IOException {
		// create gson from builder
		Gson gson = this.builder.create();

		// prepare request
		String requestJson = gson.toJson(input);

		if (encodeApiValue) {
			requestJson = URLEncoder.encode(requestJson, "UTF8");
		}

		// process request
		return makeCall(request, url, contentType, requestJson);
	}

	/**
	 * Initializes Gson Builder - register standard date serializer/deserializer.
	 * 
	 * @param builder builder to initialize
	 * @return the gson builder
	 */
	protected GsonBuilder prepareGsonBuilder(GsonBuilder builder) {
		return builder;
	}

	/**
	 * register adapter to request on server(If class implements JsonSerializer) and
	 * for response from server(If class implements JsonDeserializer).
	 * 
	 * @param type        for which is adapter registered
	 * @param typeAdapter adapter to register
	 */
	public void registerTypeAdapter(Type type, Object typeAdapter) {
		this.builder = builder.registerTypeAdapter(type, typeAdapter);
	}
}
