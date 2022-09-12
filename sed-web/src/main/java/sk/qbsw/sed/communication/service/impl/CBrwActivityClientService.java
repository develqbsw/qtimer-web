package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.request.CBrwLoadDataRequest;
import sk.qbsw.sed.client.response.CBrwActivityFetchResponseContent;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwActivityService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author lobb
 *
 */
@Service
public class CBrwActivityClientService extends AClientService implements IBrwActivityService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwActivityClientService() {
		super(CApiUrl.BRW_ACTIVITY);
	}

	@Override
	public List<CActivityRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwLoadDataRequest requestEntity = new CBrwLoadDataRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc);

		Type type = new TypeToken<CResponse<CBrwActivityFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwLoadDataRequest, CBrwActivityFetchResponseContent, List<CActivityRecord>> call = new AServiceCall<CBrwLoadDataRequest, CBrwActivityFetchResponseContent, List<CActivityRecord>>() {

			@Override
			public List<CActivityRecord> getContentObject(CBrwActivityFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_ACTIVITY_LOAD_DATA), requestEntity, type); // CBrwActivityController.loadData()

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
		return call.call(request, getUrl(CApiUrl.BRW_ACTIVITY_COUNT), null, type); // CBrwActivityController.count()
	}
}
