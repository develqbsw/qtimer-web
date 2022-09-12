package sk.qbsw.sed.server.service.system;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.service.business.ISendEmailService;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.dao.INotificationDao;
import sk.qbsw.sed.server.dao.IRequestDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IViewEmployeesStatusDao;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;

@Service(value = "automaticEmailSender")
public class CSendEmailAutomaticalyService implements ISendEmailAutomaticalyService {
	
	@Autowired
	private IClientDao clientDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IViewEmployeesStatusDao employeesStatusDao;

	@Autowired
	private IRequestDao requestDao;

	@Autowired
	private INotificationDao notificationDao;

	@Autowired
	private IHolidayDao holidayDao;

	@Autowired
	private ISendEmailService sendEmailService;

	@Transactional
	@Override
	public void processService() {
		processMyService();
	}

	private void processMyService() {
		Calendar currendDateTime = Calendar.getInstance();
		Calendar currendDate = (Calendar) currendDateTime.clone();
		currendDate.set(Calendar.HOUR_OF_DAY, 0);
		currendDate.set(Calendar.MINUTE, 0);
		currendDate.set(Calendar.SECOND, 0);
		currendDate.set(Calendar.MILLISECOND, 0);

		// check not working day
		boolean isWorkingDay = CDateUtils.isWorkingDay(currendDateTime.getTime());
		if (!isWorkingDay)
			return;

		// check all clients
		List<CClient> clients = clientDao.getApplicationClients();
		for (CClient client : clients) {
			boolean available4day = true;

			Integer selectedYear = currendDateTime.get(Calendar.YEAR);
			// check holiday date
			int currYear = currendDate.get(Calendar.YEAR);
			int currMonth = currendDate.get(Calendar.MONTH);
			int currDay = currendDate.get(Calendar.DAY_OF_MONTH);

			List<CHoliday> clientHolidays = holidayDao.findAllValidClientsHolidays(client.getId(), selectedYear);
			for (CHoliday holiday : clientHolidays) {
				Calendar hDay = holiday.getDay();
				int tmpYear = hDay.get(Calendar.YEAR);
				int tmpMonth = hDay.get(Calendar.MONTH);
				int tmpDay = hDay.get(Calendar.DAY_OF_MONTH);

				if (tmpYear == currYear && tmpMonth == currMonth && tmpDay == currDay) {
					available4day = false;
					break;
				}
			}

			// is required to send automatic email?
			if (client.getTimeAutoEmail() != null && available4day) {
				CUser adminUser = this.userDao.findClientAdministratorAccount(client.getId());

				int currentHours = currendDateTime.get(Calendar.HOUR_OF_DAY);
				int currentMinutes = currendDateTime.get(Calendar.MINUTE);
				int currentAllMinutes = currentHours * 60 + currentMinutes;

				Calendar emailTime = client.getTimeAutoEmail();
				int emailTimeHours = emailTime.get(Calendar.HOUR_OF_DAY);
				int emailTimeMinutes = emailTime.get(Calendar.MINUTE);
				int emailTimeAllMinutes = emailTimeHours * 60 + emailTimeMinutes;

				// is the time interval for sending an automatic client email?
				if ((emailTimeAllMinutes - currentAllMinutes) >= 0 && ((emailTimeAllMinutes - currentAllMinutes) < 5)) {
					// yes, ... take target email addresses
					List<CUser> users4Emails = userDao.findBySystemEmailFlag(client.getId());
					for (CUser user : users4Emails) {
						sendEmailService.sendMissingEmployeesEmailMethod(adminUser, user.getEmail());
					}
				}
			}
		}
	}
}
