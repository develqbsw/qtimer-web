package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.request.CBrwHolidayCountRequest;
import sk.qbsw.sed.client.request.CBrwHolidayFetchRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwHolidayFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwHolidayService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author lobb
 *
 */
@Service
public class CBrwHolidayClientService extends AClientService implements IBrwHolidayService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwHolidayClientService() {
		super(CApiUrl.BRW_HOLIDAY);
	}

	/**
	 * @see IBrwHolidayService#loadData(Long, Long, String, boolean,
	 *      IFilterCriteria)
	 */
	@Override
	public List<CHolidayRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwHolidayFetchRequest requestEntity = new CBrwHolidayFetchRequest(first.intValue(), count.intValue() + first.intValue(), sortProperty, sortAsc, criteria);

		Type type = new TypeToken<CResponse<CBrwHolidayFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwHolidayFetchRequest, CBrwHolidayFetchResponseContent, List<CHolidayRecord>> call = new AServiceCall<CBrwHolidayFetchRequest, CBrwHolidayFetchResponseContent, List<CHolidayRecord>>() {

			@Override
			public List<CHolidayRecord> getContentObject(CBrwHolidayFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_HOLIDAY_LOAD_DATA), requestEntity, type); // CBrwHolidayController.loadData()
	}

	/**
	 * @see IBrwHolidayService#count(IFilterCriteria)
	 */
	@Override
	public Long count(IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwHolidayCountRequest requestEntity = new CBrwHolidayCountRequest(criteria);

		Type type = new TypeToken<CResponse<CBrwCountResponseContent>>() {
		}.getType();
		AServiceCall<CBrwHolidayCountRequest, CBrwCountResponseContent, Long> call = new AServiceCall<CBrwHolidayCountRequest, CBrwCountResponseContent, Long>() {

			@Override
			public Long getContentObject(CBrwCountResponseContent content) {
				return content.getCount();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_HOLIDAY_COUNT), requestEntity, type); // CBrwHolidayController.count()
	}
}