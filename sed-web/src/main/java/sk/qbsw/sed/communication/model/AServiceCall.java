package sk.qbsw.sed.communication.model;

import java.lang.reflect.Type;

import org.apache.http.entity.ContentType;

import sk.qbsw.sed.client.response.AResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.api.CApiClient;
import sk.qbsw.sed.communication.http.IHttpApiRequest;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

/**
 * 
 * @author Peter Bozik
 *
 * @param <C> request entity type
 * @param <T> response content type
 * @param <O> object type to return
 */
public abstract class AServiceCall<C, T extends AResponseContent, O> {
	
	public O call(IHttpApiRequest request, String url, C requestEntity, Type type) throws CBussinessDataException {
		CApiClient<C, CResponse<T>> apiClient = new CApiClient<>();
		CResponse<T> response;

		response = apiClient.makeCallExc(request, url, requestEntity, type, ContentType.create("application/json", "UTF-8"));
		return getContentObject(response.getContent());
	}

	public abstract O getContentObject(T content);
}
