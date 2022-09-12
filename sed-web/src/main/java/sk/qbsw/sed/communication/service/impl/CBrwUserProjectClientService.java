package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.brw.CUserProjectRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.request.CBrwProjectCountRequest;
import sk.qbsw.sed.client.request.CBrwProjectFetchRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwUserProjectsFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwUserProjectService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author lobb
 *
 */
@Service
public class CBrwUserProjectClientService extends AClientService implements IBrwUserProjectService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwUserProjectClientService() {
		super(CApiUrl.BRW_USER_PROJECT);
	}

	@Override
	public List<CUserProjectRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwProjectFetchRequest requestEntity = new CBrwProjectFetchRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc, criteria);

		Type type = new TypeToken<CResponse<CBrwUserProjectsFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwProjectFetchRequest, CBrwUserProjectsFetchResponseContent, List<CUserProjectRecord>> call = new AServiceCall<CBrwProjectFetchRequest, CBrwUserProjectsFetchResponseContent, List<CUserProjectRecord>>() {

			@Override
			public List<CUserProjectRecord> getContentObject(CBrwUserProjectsFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_USER_PROJECT_LOAD_DATA), requestEntity, type); // CBrwUserProjectController.loadData()
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
		return call.call(request, getUrl(CApiUrl.BRW_USER_PROJECT_COUNT), requestEntity, type); // CBrwUserProjectController.count()
	}
}
