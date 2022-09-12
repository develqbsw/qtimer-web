package sk.qbsw.sed.server.service.system;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IShiftDayConstant;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CTimeUtils;
import sk.qbsw.sed.server.service.business.ITimesheetBaseService;

@Service(value = "automaticTimestampGenerator")
public class CTimestampAutomaticGeneratorService implements ITimestampAutomaticGeneratorService {

	@Autowired
	IClientDao clientDao;

	@Autowired
	IUserDao userDao;

	@Autowired
	IHolidayDao holidayDao;

	@Autowired
	ITimesheetBaseService timesheetBaseService;

	private final Logger logger = Logger.getLogger(CTimestampAutomaticGeneratorService.class.getName());

	/**
	 * @see ITimesheetBaseService#generateApprovedEmployeesAbsenceRecords(Long, Date)
	 */
	@Transactional
	@Override
	public void processService() {
		processMyService();
	}

	private void processMyService() {
		Calendar currentDateTime = Calendar.getInstance();
		Calendar currentDate = (Calendar) currentDateTime.clone();
		CTimeUtils.convertToStartDate(currentDate);

		// check all clients
		List<CClient> clients = clientDao.getApplicationClients();
		for (CClient client : clients) {
			// cez volne dni klienta a vseobecne nepracovne dni nic PRE KLIENTA nerobim
			boolean isActionDay = actionIsAvailable4Day(currentDate, client);
			if (!isActionDay) {
				break;
			}

			// is required to send automatic email?
			if (client.getTimeAutoGenerateTimestamps() != null) {
				// prepare target day
				Long targetDayShift = client.getDayShiftAutoGenerateTimestamps();
				if (targetDayShift == null) {
					// today
					targetDayShift = IShiftDayConstant.TODAY;
				}
				boolean available4targetDay = true;
				Calendar targetDay = (Calendar) currentDate.clone();
				if (IShiftDayConstant.YESTERADAY.equals(targetDayShift)) {
					// set previous day
					targetDay.add(Calendar.DAY_OF_MONTH, IShiftDayConstant.YESTERADAY.intValue());
					// check the day: work day and not holiday
					while (!actionIsAvailable4Day(targetDay, client)) {
						// set previous day
						targetDay.add(Calendar.DAY_OF_MONTH, IShiftDayConstant.YESTERADAY.intValue());
					}
				} else {
					// today
					available4targetDay = actionIsAvailable4Day(targetDay, client);
				}

				if (available4targetDay) {

					CUser administrator = this.userDao.findClientAdministratorAccount(client.getId());

					int currentHours = currentDateTime.get(Calendar.HOUR_OF_DAY);
					int currentMinutes = currentDateTime.get(Calendar.MINUTE);
					int currentAllMinutes = currentHours * 60 + currentMinutes;

					Calendar generationTime = client.getTimeAutoGenerateTimestamps();
					int generationTimeHours = generationTime.get(Calendar.HOUR_OF_DAY);
					int generationTimeMinutes = generationTime.get(Calendar.MINUTE);
					int generationTimeAllMinutes = generationTimeHours * 60 + generationTimeMinutes;

					// is the time interval for automatic time stamps generating process?
					if ((generationTimeAllMinutes - currentAllMinutes) >= 0 && ((generationTimeAllMinutes - currentAllMinutes) < 5)) {
						// yes, ...

						Calendar cDateFrom = (Calendar) targetDay.clone();
						CTimeUtils.convertToStartDate(cDateFrom);
						Calendar cDateTo = (Calendar) cDateFrom.clone();

						// process
						try {
							if (administrator != null) {
								this.timesheetBaseService.generateApprovedEmployeesAbsenceRecords(administrator.getId(), cDateFrom.getTime(), cDateTo.getTime());
								logger.info("AUTOMATIC TIMESTAMP GENERATING - SUCCESS: client=" + infoMsg(client.getId(), cDateFrom, cDateTo));
							}
						} catch (CBusinessException be) {
							logger.error("AUTOMATIC TIMESTAMP GENERATING - BUSINESS ERROR: " + infoMsg(client.getId(), cDateFrom, cDateTo) + "  message:" + be.getMessage(), be);
						} catch (Exception ex) {
							logger.error("AUTOMATIC TIMESTAMP GENERATING - ERROR: " + infoMsg(client.getId(), cDateFrom, cDateTo), ex);
						}
					}
				}
			}
		}
	}

	/**
	 * returns true in case: day IS NOT a holiday and IS work day
	 * 
	 * @param testDate
	 * @param client
	 * @return
	 */
	private boolean actionIsAvailable4Day(Calendar testDate, CClient client) {
		boolean isWorkingDay = CDateUtils.isWorkingDay(testDate.getTime());

		boolean isClientHoliday = isHoliday(client.getId(), testDate);

		return !isClientHoliday && isWorkingDay;
	}

	private boolean isHoliday(Long clientId, Calendar testDay) {
		boolean retValIsHoliday = false;

		Integer selectedYear = testDay.get(Calendar.YEAR);
		// check holiday date
		int currYear = testDay.get(Calendar.YEAR);
		int currMonth = testDay.get(Calendar.MONTH);
		int currDay = testDay.get(Calendar.DAY_OF_MONTH);

		List<CHoliday> clientHolidays = holidayDao.findAllValidClientsHolidays(clientId, selectedYear);
		for (CHoliday holiday : clientHolidays) {
			Calendar hDay = holiday.getDay();
			int tmpYear = hDay.get(Calendar.YEAR);
			int tmpMonth = hDay.get(Calendar.MONTH);
			int tmpDay = hDay.get(Calendar.DAY_OF_MONTH);

			if (tmpYear == currYear && tmpMonth == currMonth && tmpDay == currDay) {
				retValIsHoliday = true;
				break;
			}
		}

		return retValIsHoliday;
	}

	private String infoMsg(Long clienId, Calendar from, Calendar to) {
		return "clientId=" + clienId + " from=" + from.getTime() + " to=" + to.getTime();
	}
}
