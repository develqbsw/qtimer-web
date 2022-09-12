package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;
import sk.qbsw.sed.client.model.request.CSubordinateRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.request.CBrwSubordinateRequestCountRequest;
import sk.qbsw.sed.client.request.CBrwSubordinateRequestFetchRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwRequestFetchResponseContent;
import sk.qbsw.sed.client.response.CRequestForGraphResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwRequestClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CBrwRequestClientService extends AClientService implements IBrwRequestClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwRequestClientService() {
		super(CApiUrl.BRW_REQUEST);
	}

	@Override
	public List<CRequestRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwSubordinateRequestFetchRequest requestEntity = new CBrwSubordinateRequestFetchRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc, criteria);

		Type type = new TypeToken<CResponse<CBrwRequestFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwSubordinateRequestFetchRequest, CBrwRequestFetchResponseContent, List<CRequestRecord>> call = new AServiceCall<CBrwSubordinateRequestFetchRequest, CBrwRequestFetchResponseContent, List<CRequestRecord>>() {

			@Override
			public List<CRequestRecord> getContentObject(CBrwRequestFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_REQUEST_LOAD_DATA), requestEntity, type); // CBrwSubordinateRequestController.loadData()
	}

	@Override
	public Long count(IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwSubordinateRequestCountRequest requestEntity = new CBrwSubordinateRequestCountRequest(criteria);

		Type type = new TypeToken<CResponse<CBrwCountResponseContent>>() {
		}.getType();
		AServiceCall<CBrwSubordinateRequestCountRequest, CBrwCountResponseContent, Long> call = new AServiceCall<CBrwSubordinateRequestCountRequest, CBrwCountResponseContent, Long>() {

			@Override
			public Long getContentObject(CBrwCountResponseContent content) {
				return content.getCount();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_REQUEST_COUNT), requestEntity, type); // CBrwSubordinateRequestController.count()
	}

	@Override
	public List<CRequestRecordForGraph> loadDataForGraph(IFilterCriteria criteria) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwSubordinateRequestFetchRequest requestEntity = new CBrwSubordinateRequestFetchRequest();
		requestEntity.setCriteria((CSubordinateRequestsBrwFilterCriteria) criteria);

		Type type = new TypeToken<CResponse<CRequestForGraphResponseContent>>() {
		}.getType();
		AServiceCall<CBrwSubordinateRequestFetchRequest, CRequestForGraphResponseContent, List<CRequestRecordForGraph>> call = new AServiceCall<CBrwSubordinateRequestFetchRequest, CRequestForGraphResponseContent, List<CRequestRecordForGraph>>() {

			@Override
			public List<CRequestRecordForGraph> getContentObject(CRequestForGraphResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_REQUEST_LOAD_DATA_FOR_GRAPH), requestEntity, type); // CBrwSubordinateRequestController.loadDataForGraph()
	}
}
