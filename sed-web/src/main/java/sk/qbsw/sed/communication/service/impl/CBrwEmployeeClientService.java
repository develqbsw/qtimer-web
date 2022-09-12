package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.request.CBrwEmployeeCountRequest;
import sk.qbsw.sed.client.request.CBrwEmployeeLoadDataRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CEmployeeRecordListResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwEmployeeClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CBrwEmployeeClientService extends AClientService implements IBrwEmployeeClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwEmployeeClientService() {
		super(CApiUrl.BRW_EMPLOYEE);
	}

	@Override
	public List<CEmployeeRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, String name) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwEmployeeLoadDataRequest requestEntity = new CBrwEmployeeLoadDataRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc, name);

		Type type = new TypeToken<CResponse<CEmployeeRecordListResponseContent>>() {
		}.getType();
		AServiceCall<CBrwEmployeeLoadDataRequest, CEmployeeRecordListResponseContent, List<CEmployeeRecord>> call = new AServiceCall<CBrwEmployeeLoadDataRequest, CEmployeeRecordListResponseContent, List<CEmployeeRecord>>() {

			@Override
			public List<CEmployeeRecord> getContentObject(CEmployeeRecordListResponseContent content) {
				return content.getEmployeeRecordList();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_EMPLOYEE_LOAD_DATA), requestEntity, type); // CBrwEmployeeController.loadData()
	}

	@Override
	public Long count(String name) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwEmployeeCountRequest requestEntity = new CBrwEmployeeCountRequest(name);

		Type type = new TypeToken<CResponse<CBrwCountResponseContent>>() {
		}.getType();
		AServiceCall<CBrwEmployeeCountRequest, CBrwCountResponseContent, Long> call = new AServiceCall<CBrwEmployeeCountRequest, CBrwCountResponseContent, Long>() {

			@Override
			public Long getContentObject(CBrwCountResponseContent content) {
				return content.getCount();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_EMPLOYEE_COUNT), requestEntity, type); // CBrwEmployeeController.count()
	}
}
