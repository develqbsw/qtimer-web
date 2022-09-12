package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.ITimeSheetRecordStates;
import sk.qbsw.sed.client.model.message.CMessageRecord;
import sk.qbsw.sed.client.model.message.IMessageType;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IMessageService;
import sk.qbsw.sed.common.utils.CDateRange;
import sk.qbsw.sed.common.utils.CDateRangeUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.ITimesheetRecordDao;
import sk.qbsw.sed.server.dao.ITimesheetStateDao;
import sk.qbsw.sed.server.dao.IViewOrganizationTreeDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CTimeSheetRecordStatus;
import sk.qbsw.sed.server.nameday.INameDayService;
import sk.qbsw.sed.server.service.CTimeUtils;

@Service(value = "messageService")
public class CMessageServiceImpl implements IMessageService {

	@Autowired
	private ITimesheetStateDao stateDao;

	/**
	 * timesheet dao
	 */
	@Autowired
	private ITimesheetRecordDao timesheetDao;

	@Autowired
	private INameDayService nameDayService;

	@Autowired
	private IViewOrganizationTreeDao viewOrgTreeDao;

	@Transactional(readOnly = true)
	public List<CMessageRecord> getMessages(Locale locale) throws CBusinessException {
		List<CMessageRecord> messages = new ArrayList<>();
		CTimeSheetRecordStatus newStatus = stateDao.findById(ITimeSheetRecordStates.ID_NEW);
		CTimeSheetRecordStatus confirmedByEmployeeStatus = stateDao.findById(ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE);
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		CDateRange previousMonth = CDateRangeUtils.getDateRangeForPreviousMonth();
		Calendar firstDayOfPreviousMonth = previousMonth.getDateFrom();
		CTimeUtils.convertToStartDate(firstDayOfPreviousMonth);

		Calendar lastDayOfPreviousMonth = previousMonth.getDateTo();
		CTimeUtils.convertToEndDate(lastDayOfPreviousMonth);

		if (timesheetDao.countRecordsInDateIntervalByStatus(loggedUser.getUserId(), firstDayOfPreviousMonth, lastDayOfPreviousMonth, newStatus) > 0) {
			messages.add(new CMessageRecord(IMessageType.GENERATE_MONTH_REPORT, firstDayOfPreviousMonth, lastDayOfPreviousMonth));
		}

		CDateRange previousWeek = CDateRangeUtils.getDateRangeForPreviousWeek();
		Calendar firstDayOfPreviousWeek = previousWeek.getDateFrom();
		CTimeUtils.convertToStartDate(firstDayOfPreviousWeek);

		Calendar lastDayOfPreviousWeek = previousWeek.getDateTo();
		CTimeUtils.convertToEndDate(lastDayOfPreviousWeek);

		if (timesheetDao.countRecordsInDateIntervalByStatus(loggedUser.getUserId(), firstDayOfPreviousWeek, lastDayOfPreviousWeek, newStatus) > 0) {
			messages.add(new CMessageRecord(IMessageType.CONFIRM_TIMESTAMPS, firstDayOfPreviousWeek, lastDayOfPreviousWeek));
		}

		if (loggedUser.getDirectSubordinates() != null && !loggedUser.getDirectSubordinates().isEmpty()
				&& timesheetDao.existsSubordinateRecordsForConfirmation(loggedUser.getDirectSubordinates(), firstDayOfPreviousWeek, lastDayOfPreviousWeek, confirmedByEmployeeStatus)) {
			messages.add(new CMessageRecord(IMessageType.CONFIRM_SUBORDINATE_TIMESTAMPS, firstDayOfPreviousWeek, lastDayOfPreviousWeek));
		}

		return messages;
	}

	@Transactional(readOnly = true)
	public String getNamesDayMessage(Locale locale) throws CBusinessException {

		return nameDayService.getNamesday(locale.getLanguage());
	}
}
