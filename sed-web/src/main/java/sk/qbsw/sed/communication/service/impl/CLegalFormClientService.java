package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpGetRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.ILegalFormClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

/**
 * 
 * @author moravcik
 *
 */
@Service
public class CLegalFormClientService extends AClientService implements ILegalFormClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CLegalFormClientService() {
		super(CApiUrl.LEGAL_FORM);
	}

	/**
	 * @see ILegalFormClientService#getValidRecords()
	 */
	@Override
	public List<CCodeListRecord> getValidRecords() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(null);

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.LEGAL_FORM_GET_VALID_RECORDS), null, type); // CLegalFormController.getValidRecords()
	}
}
