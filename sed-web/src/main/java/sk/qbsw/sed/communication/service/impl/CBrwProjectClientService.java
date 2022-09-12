package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.request.CBrwProjectCountRequest;
import sk.qbsw.sed.client.request.CBrwProjectFetchRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwProjectFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwProjectService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CBrwProjectClientService extends AClientService implements IBrwProjectService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwProjectClientService() {
		super(CApiUrl.BRW_PROJECT);
	}

	@Override
	public List<CProjectRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwProjectFetchRequest requestEntity = new CBrwProjectFetchRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc, criteria);

		Type type = new TypeToken<CResponse<CBrwProjectFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwProjectFetchRequest, CBrwProjectFetchResponseContent, List<CProjectRecord>> call = new AServiceCall<CBrwProjectFetchRequest, CBrwProjectFetchResponseContent, List<CProjectRecord>>() {

			@Override
			public List<CProjectRecord> getContentObject(CBrwProjectFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_PROJECT_LOAD_DATA), requestEntity, type); // CBrwProjectController.loadData()
	}

	@Override
	public Long count(IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwProjectCountRequest requestEntity = new CBrwProjectCountRequest(criteria);

		Type type = new TypeToken<CResponse<CBrwCountResponseContent>>() {
		}.getType();
		AServiceCall<CBrwProjectCountRequest, CBrwCountResponseContent, Long> call = new AServiceCall<CBrwProjectCountRequest, CBrwCountResponseContent, Long>() {

			@Override
			public Long getContentObject(CBrwCountResponseContent content) {
				return content.getCount();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_PROJECT_COUNT), requestEntity, type); // CBrwProjectController.count()
	}
}