package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.request.CGenerateBrwTimeStampFetchRequest;
import sk.qbsw.sed.client.response.CBrwTimeStampGenerateFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CTmpTimeSheetResponseContent;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwTimeStampGenerateService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CBrwTimeStampGenerateClientService extends AClientService implements IBrwTimeStampGenerateService {
	
	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwTimeStampGenerateClientService() {
		super(CApiUrl.BRW_MY_TIME_STAMP_GENERATE);
	}

	@Override
	public List<CTmpTimeSheet> fetch(int startRow, int endRow, String sortProperty, Boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGenerateBrwTimeStampFetchRequest requestEntity = new CGenerateBrwTimeStampFetchRequest(startRow, endRow, sortProperty, sortAsc, criteria);

		Type type = new TypeToken<CResponse<CBrwTimeStampGenerateFetchResponseContent>>() {
		}.getType();
		AServiceCall<CGenerateBrwTimeStampFetchRequest, CBrwTimeStampGenerateFetchResponseContent, List<CTmpTimeSheet>> call = new AServiceCall<CGenerateBrwTimeStampFetchRequest, CBrwTimeStampGenerateFetchResponseContent, List<CTmpTimeSheet>>() {

			@Override
			public List<CTmpTimeSheet> getContentObject(CBrwTimeStampGenerateFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_GENERATE_FETCH), requestEntity, type); // CBrwMyTimeStampGenerateController.fetch()
	}

	@Override
	public CTmpTimeSheet add(CTmpTimeSheet record) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CTmpTimeSheetResponseContent>>() {
		}.getType();
		AServiceCall<CTmpTimeSheet, CTmpTimeSheetResponseContent, CTmpTimeSheet> call = new AServiceCall<CTmpTimeSheet, CTmpTimeSheetResponseContent, CTmpTimeSheet>() {

			@Override
			public CTmpTimeSheet getContentObject(CTmpTimeSheetResponseContent content) {
				return content.getTmpTimeSheet();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_GENERATE_ADD), record, type); // CBrwMyTimeStampGenerateController.add()
	}

	@Override
	public CTmpTimeSheet update(CTmpTimeSheet record) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CTmpTimeSheetResponseContent>>() {
		}.getType();
		AServiceCall<CTmpTimeSheet, CTmpTimeSheetResponseContent, CTmpTimeSheet> call = new AServiceCall<CTmpTimeSheet, CTmpTimeSheetResponseContent, CTmpTimeSheet>() {

			@Override
			public CTmpTimeSheet getContentObject(CTmpTimeSheetResponseContent content) {
				return content.getTmpTimeSheet();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_GENERATE_UPDATE), record, type); // CBrwMyTimeStampGenerateController.update()
	}

	public CTmpTimeSheet delete(CTmpTimeSheet record) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CTmpTimeSheetResponseContent>>() {
		}.getType();
		AServiceCall<CTmpTimeSheet, CTmpTimeSheetResponseContent, CTmpTimeSheet> call = new AServiceCall<CTmpTimeSheet, CTmpTimeSheetResponseContent, CTmpTimeSheet>() {

			@Override
			public CTmpTimeSheet getContentObject(CTmpTimeSheetResponseContent content) {
				return content.getTmpTimeSheet();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_GENERATE_DELETE), record, type); // CBrwMyTimeStampGenerateController.delete()
	}

}
