package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;
import sk.qbsw.sed.client.request.CBrwLoadDataRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwRequestReasonFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwRequestReasonClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CBrwRequestReasonClientService extends AClientService implements IBrwRequestReasonClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwRequestReasonClientService() {
		super(CApiUrl.BRW_REQUEST_REASON);
	}

	@Override
	public List<CRequestReasonRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwLoadDataRequest requestEntity = new CBrwLoadDataRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc);

		Type type = new TypeToken<CResponse<CBrwRequestReasonFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwLoadDataRequest, CBrwRequestReasonFetchResponseContent, List<CRequestReasonRecord>> call = new AServiceCall<CBrwLoadDataRequest, CBrwRequestReasonFetchResponseContent, List<CRequestReasonRecord>>() {

			@Override
			public List<CRequestReasonRecord> getContentObject(CBrwRequestReasonFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_REQUEST_REASON_LOAD_DATA), requestEntity, type); // CBrwRequestReasonController.loadData()
	}

	@Override
	public Long count() throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CBrwCountResponseContent>>() {
		}.getType();
		AServiceCall<Void, CBrwCountResponseContent, Long> call = new AServiceCall<Void, CBrwCountResponseContent, Long>() {

			@Override
			public Long getContentObject(CBrwCountResponseContent content) {
				return content.getCount();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_REQUEST_REASON_COUNT), null, type); // CBrwRequestReasonController.count()
	}
}
