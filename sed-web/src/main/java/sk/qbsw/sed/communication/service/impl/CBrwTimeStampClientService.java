package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.model.timestamp.ChooseDefaultTimestampFormModelToChange;
import sk.qbsw.sed.client.request.CBrwTimeStampCountRequest;
import sk.qbsw.sed.client.request.CBrwTimeStampFetchRequest;
import sk.qbsw.sed.client.request.CBrwTimeStampMassChangeRequest;
import sk.qbsw.sed.client.request.CGetWorkTimeInIntervalRequest;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwTimeStampFetchResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CTimeStampRecordResponseContent;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IBrwTimeStampService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;
import sk.qbsw.sed.web.ui.components.panel.ChooseDefaultTimestampFormModel;

@Service
public class CBrwTimeStampClientService extends AClientService implements IBrwTimeStampService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CBrwTimeStampClientService() {
		super(CApiUrl.BRW_MY_TIME_STAMP);
	}

	@Override
	public CTimeStampRecord update(CTimeStampRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CTimeStampRecordResponseContent>>() {
		}.getType();
		AServiceCall<CTimeStampRecord, CTimeStampRecordResponseContent, CTimeStampRecord> call = new AServiceCall<CTimeStampRecord, CTimeStampRecordResponseContent, CTimeStampRecord>() {

			@Override
			public CTimeStampRecord getContentObject(CTimeStampRecordResponseContent content) {
				return content.getTimeStampRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_UPDATE), record, type); // CBrwMyTimeStampController.update()
	}

	@Override
	public Long getWorkTimeInInterval(Long userId, Date dateFrom, Date dateTo) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetWorkTimeInIntervalRequest requestEntity = new CGetWorkTimeInIntervalRequest(userId, dateFrom, dateTo);

		Type type = new TypeToken<CResponse<CLongResponseContent>>() {
		}.getType();
		AServiceCall<CGetWorkTimeInIntervalRequest, CLongResponseContent, Long> call = new AServiceCall<CGetWorkTimeInIntervalRequest, CLongResponseContent, Long>() {

			@Override
			public Long getContentObject(CLongResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_GET_TIME_INTERVAL), requestEntity, type); // CBrwMyTimeStampController.getWorkTimeInInterval()
	}

	@Override
	public List<CTimeStampRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwTimeStampFetchRequest requestEntity = new CBrwTimeStampFetchRequest(first.intValue(), count.intValue(), sortProperty, sortAsc, criteria);

		Type type = new TypeToken<CResponse<CBrwTimeStampFetchResponseContent>>() {
		}.getType();
		AServiceCall<CBrwTimeStampFetchRequest, CBrwTimeStampFetchResponseContent, List<CTimeStampRecord>> call = new AServiceCall<CBrwTimeStampFetchRequest, CBrwTimeStampFetchResponseContent, List<CTimeStampRecord>>() {

			@Override
			public List<CTimeStampRecord> getContentObject(CBrwTimeStampFetchResponseContent content) {
				return content.getResult();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_LOAD_DATA), requestEntity, type); // CBrwMyTimeStampController.loadData()
	}

	@Override
	public Long count(IFilterCriteria criteria) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CBrwTimeStampCountRequest requestEntity = new CBrwTimeStampCountRequest(criteria);

		Type type = new TypeToken<CResponse<CBrwCountResponseContent>>() {
		}.getType();
		AServiceCall<CBrwTimeStampCountRequest, CBrwCountResponseContent, Long> call = new AServiceCall<CBrwTimeStampCountRequest, CBrwCountResponseContent, Long>() {

			@Override
			public Long getContentObject(CBrwCountResponseContent content) {
				return content.getCount();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_COUNT), requestEntity, type); // CBrwMyTimeStampController.count()
	}

	@Override
	public CTimeStampRecord getDetail(Long timeStampId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CTimeStampRecordResponseContent>>() {
		}.getType();
		AServiceCall<Long, CTimeStampRecordResponseContent, CTimeStampRecord> call = new AServiceCall<Long, CTimeStampRecordResponseContent, CTimeStampRecord>() {

			@Override
			public CTimeStampRecord getContentObject(CTimeStampRecordResponseContent content) {
				return content.getTimeStampRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_GET_DETAIL), timeStampId, type); // CBrwMyTimeStampController.getDetail()
	}

	@Override
	public Boolean showEditButtonOnDetail(Long timeStampId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CBooleanResponseContent>>() {
		}.getType();
		AServiceCall<Long, CBooleanResponseContent, Boolean> call = new AServiceCall<Long, CBooleanResponseContent, Boolean>() {

			@Override
			public Boolean getContentObject(CBooleanResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_SHOW_EDIT_BUTTON_ON_DETAIL), timeStampId, type); // CBrwMyTimeStampController.showEditButtonOnDetail()
	}

	@Override
	public Long massChangeTimestamps(CSubrodinateTimeStampBrwFilterCriteria filter, ChooseDefaultTimestampFormModel formModel) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		ChooseDefaultTimestampFormModelToChange toChange = new ChooseDefaultTimestampFormModelToChange(formModel.getActivity(), formModel.getProject(), formModel.getPhase(), formModel.getNote(),
				formModel.getActivityChecked(), formModel.getProjectChecked(), formModel.getPhaseChecked(), formModel.getNoteChecked());

		CBrwTimeStampMassChangeRequest requestEntity = new CBrwTimeStampMassChangeRequest(filter, toChange);

		Type type = new TypeToken<CResponse<CLongResponseContent>>() {
		}.getType();
		AServiceCall<CBrwTimeStampMassChangeRequest, CLongResponseContent, Long> call = new AServiceCall<CBrwTimeStampMassChangeRequest, CLongResponseContent, Long>() {

			@Override
			public Long getContentObject(CLongResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.BRW_MY_TIME_STAMP_MASS_CHANGE), requestEntity, type); // CBrwMyTimeStampController.massChangeTimestamps()
	}
}
