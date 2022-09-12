package sk.qbsw.sed.communication.model;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.response.CEmptyResponseContent;
import sk.qbsw.sed.client.response.CResponse;
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
public class CServiceCallEmptyResponse<C> extends AServiceCall<C, CEmptyResponseContent, Void> {

	@Override
	public Void getContentObject(CEmptyResponseContent content) {
		return null;
	}

	public void call(IHttpApiRequest request, String url, C requestEntity) throws CBussinessDataException {
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		super.call(request, url, requestEntity, type);
	}
}
