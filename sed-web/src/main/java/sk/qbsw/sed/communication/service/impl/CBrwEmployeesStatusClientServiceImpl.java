package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.client.response.CBrwEmployeesStatusNewFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwEmployeesStatusClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service(value = "brwEmployeeStatusClientService")
public class CBrwEmployeesStatusClientServiceImpl extends AClientService implements IBrwEmployeesStatusClientService {
	
	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwEmployeesStatusClientServiceImpl() {
		super(CApiUrl.BRW_EMPLOYEES_STATUS);
	}

	@Override
	public List<CEmployeesStatusNew> fetch() throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CBrwEmployeesStatusNewFetchResponseContent>>() {
		}.getType();
		AServiceCall<Void, CBrwEmployeesStatusNewFetchResponseContent, List<CEmployeesStatusNew>> call = new AServiceCall<Void, CBrwEmployeesStatusNewFetchResponseContent, List<CEmployeesStatusNew>>() {
			@Override
			public List<CEmployeesStatusNew> getContentObject(CBrwEmployeesStatusNewFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_EMPLOYEES_STATUS_FETCH_NEW), null, type); // CBrwEmployeesStatusController.fetchCached()
	}
}
