package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.brw.CUserActivityRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.request.CBrwActivityCountRequest;
import sk.qbsw.sed.client.request.CBrwActivityFetchRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwUserActivitiesFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwUserActivityService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author bado
 *
 */
@Service
public class CBrwUserActivityClientService extends AClientService implements IBrwUserActivityService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwUserActivityClientService() {
		super(CApiUrl.BRW_USER_ACTIVITY);
	}

	@Override
	public List<CUserActivityRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwActivityFetchRequest requestEntity = new CBrwActivityFetchRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc, criteria);

		Type type = new TypeToken<CResponse<CBrwUserActivitiesFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwActivityFetchRequest, CBrwUserActivitiesFetchResponseContent, List<CUserActivityRecord>> call = new AServiceCall<CBrwActivityFetchRequest, CBrwUserActivitiesFetchResponseContent, List<CUserActivityRecord>>() {

			@Override
			public List<CUserActivityRecord> getContentObject(CBrwUserActivitiesFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_USER_ACTIVITY_LOAD_DATA), requestEntity, type); // CBrwUserActivityController.loadData()
	}

	@Override
	public Long count(IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwActivityCountRequest requestEntity = new CBrwActivityCountRequest(criteria);

		Type type = new TypeToken<CResponse<CBrwCountResponseContent>>() {
		}.getType();
		AServiceCall<CBrwActivityCountRequest, CBrwCountResponseContent, Long> call = new AServiceCall<CBrwActivityCountRequest, CBrwCountResponseContent, Long>() {

			@Override
			public Long getContentObject(CBrwCountResponseContent content) {
				return content.getCount();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_USER_ACTIVITY_COUNT), requestEntity, type); // CBrwUserActivityController.count()
	}
}
