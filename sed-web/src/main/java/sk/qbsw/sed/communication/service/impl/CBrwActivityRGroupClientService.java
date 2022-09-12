package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.client.request.CBrwLoadDataRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwGroupsAIFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwActivityRGroupClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author moravcik
 *
 */
@Service
public class CBrwActivityRGroupClientService extends AClientService implements IBrwActivityRGroupClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwActivityRGroupClientService() {
		super(CApiUrl.BRW_ACTIVITY_RESTRICTION_GROUP);
	}

	@Override
	public List<CGroupsAIData> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwLoadDataRequest requestEntity = new CBrwLoadDataRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc);

		Type type = new TypeToken<CResponse<CBrwGroupsAIFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwLoadDataRequest, CBrwGroupsAIFetchResponseContent, List<CGroupsAIData>> call = new AServiceCall<CBrwLoadDataRequest, CBrwGroupsAIFetchResponseContent, List<CGroupsAIData>>() {

			@Override
			public List<CGroupsAIData> getContentObject(CBrwGroupsAIFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_ACTIVITY_RESTRICTION_GROUP_LOAD_DATA), requestEntity, type); // CBrwGroupsAIController.loadData()
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
		return call.call(request, getUrl(CApiUrl.BRW_ACTIVITY_RESTRICTION_GROUP_COUNT), null, type); // CBrwGroupsAIController.count()
	}
}
