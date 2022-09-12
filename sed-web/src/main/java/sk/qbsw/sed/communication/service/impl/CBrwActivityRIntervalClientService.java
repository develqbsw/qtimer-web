package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.client.request.CBrwLoadDataRequest;
import sk.qbsw.sed.client.response.CBrwActivityIntervalFetchResponseContent;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwActivityRIntervalClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author moravcik
 *
 */
@Service
public class CBrwActivityRIntervalClientService extends AClientService implements IBrwActivityRIntervalClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwActivityRIntervalClientService() {
		super(CApiUrl.BRW_ACTIVITY_RESTRICTION_INTERVAL);
	}

	@Override
	public List<CActivityIntervalData> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwLoadDataRequest requestEntity = new CBrwLoadDataRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc);

		Type type = new TypeToken<CResponse<CBrwActivityIntervalFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwLoadDataRequest, CBrwActivityIntervalFetchResponseContent, List<CActivityIntervalData>> call = new AServiceCall<CBrwLoadDataRequest, CBrwActivityIntervalFetchResponseContent, List<CActivityIntervalData>>() {

			@Override
			public List<CActivityIntervalData> getContentObject(CBrwActivityIntervalFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_ACTIVITY_RESTRICTION_INTERVAL_LOAD_DATA), requestEntity, type); // CBrwActivityIntervalController.loadData()
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
		return call.call(request, getUrl(CApiUrl.BRW_ACTIVITY_RESTRICTION_INTERVAL_COUNT), null, type); // CBrwActivityIntervalController.count()
	}
}
