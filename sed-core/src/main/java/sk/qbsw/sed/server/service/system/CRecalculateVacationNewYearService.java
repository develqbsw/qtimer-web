package sk.qbsw.sed.server.service.system;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.IUserTypes;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * class called by trigger every year 1.1.20XX 00:01
 * 
 * @author lobb
 *
 */
@Service(value = "recalculateVacationNewYear")
public class CRecalculateVacationNewYearService {
	@Autowired
	private IClientDao clientDao;

	@Autowired
	private IUserDao userDao;

	private final Logger logger = Logger.getLogger(CRecalculateVacationNewYearService.class.getName());

	/**
	 * start point
	 */
	@Transactional
	public void processService() {
		recalculateVacationNewYear();
	}

	/**
	 * na začiatku pre platných používateľov pripočítať k hodnote t_user.c_vacation
	 * hodnotu v t_user.c_vacation_next_year. Toto vlastne znamená pripočítať k
	 * zostatku z predošlého roka zostatok na tento rok
	 */
	public void recalculateVacationNewYear() {
		// check all clients
		List<CClient> clients = clientDao.getApplicationClients();

		// ziskam si 0-teho usera lebo to setuje system
		final CUser changedBy = this.userDao.findById(0L);
		for (CClient client : clients) {
			String bonusVacation = client.getBonusVacation();

			if (bonusVacation != null) {
				List<CUser> users = this.userDao.findAllEmployeesByValidFlag(client.getId(), true, IUserTypes.EMPLOYEE);

				for (CUser user : users) {
					Double vacation = user.getVacation();
					Double vacationNextYear = user.getVacationNextYear();

					if (vacation != null && vacationNextYear != null) {
						logger.info("recalculateVacationNewYear user: " + user.getName() + " " + user.getSurname());

						// pripočítať k hodnote t_user.c_vacation hodnotu v
						// t_user.c_vacation_next_year
						user.setVacation(vacation + vacationNextYear, changedBy);

						user.setVacationNextYear(recalculateVacationNextYear(user.getBirthDate(), user.getWorkStartDate(), bonusVacation), changedBy);

						userDao.saveOrUpdate(user);
					}
				}
			}
		}
	}

	/**
	 * 20 dní - vždy 5 dní - ak rozdiel medzi posledným dňom aktuálneho roka a
	 * dátumu v t_user.c_birth_date je vačší alebo rovný 33 rokom (napríklad ak by
	 * to bolo pustené na začiatku tohto roka čiže 1.1.2017, tak od posledného dňa
	 * roka čo je 31.12.2017 odpočítať dátum narodenia), bonusové dni - podľa
	 * t_client.c_bonus_vacation where pk_id= [t_user.fk_client] si určiť roky,
	 * podľa ktorých majú byť určené bonusové dni (v poli sú roky oddelené
	 * bodkočiarkou). Následne za každý z rokov určených z poľa c_bonus_vacation
	 * pridať jeden deň dovolenky, ak rozdiel medzi posledným dňom aktuálneho roka a
	 * dňom v t_user.c_work_start_date je väčší alebo rovný ako daný rok pre ktorý
	 * sa porovnávanie vykonáva.
	 * 
	 * počet nových dní, ktoré sa pôvodne pripočítali k hodnote t_user.c_vacation
	 * teraz zapísať do stĺpca t_user.c_vacation_next_year (pôvodná hodnota v poli
	 * sa prepíše novou). Pre výpočet dovolenky na nasledujúci rok, samozrejme
	 * použiť dátumy z ďalšieho roku, čiže napríklad ak by trigger bolo pustený na
	 * začiatku tohto roka čiže 1.1.2017, tak pri výpočte bonusových dní podľa veku
	 * od posledného dňa roka 2018 čo je 31.12.2018 odpočítať dátum narodenia
	 * (predtým by sa použil rok 31.12.2017 ). To vlastne znamená, že sa nám
	 * vypočíta hodnota dovolenky na nasledujúci rok (ak to bolo pustené 1.1.2017
	 * tak na rak 2018).
	 */
	private Double recalculateVacationNextYear(Calendar birthDate, Calendar workStartDate, String bonusVacation) {

		if (birthDate != null && workStartDate != null) {
			Double vacationNextYear = Double.valueOf(20);

			Calendar now = Calendar.getInstance();
			LocalDate lastDayOfNextYear = LocalDate.of(now.get(Calendar.YEAR) + 1, 12, 31);
			LocalDate birthLocalDate = LocalDate.of(birthDate.get(Calendar.YEAR), birthDate.get(Calendar.MONTH) + 1, birthDate.get(Calendar.DAY_OF_MONTH));

			boolean reach33 = calculateAge(birthLocalDate, lastDayOfNextYear) >= 33;
			logger.info("Reach 33 years: " + reach33);

			if (reach33) {
				vacationNextYear += 5;
			}

			LocalDate workStartLocalDate = LocalDate.of(workStartDate.get(Calendar.YEAR), workStartDate.get(Calendar.MONTH) + 1, workStartDate.get(Calendar.DAY_OF_MONTH));
			int yearsInCompanyNextYear = calculateAge(workStartLocalDate, lastDayOfNextYear);
			logger.info("Years in company next year: " + yearsInCompanyNextYear);

			String[] bonus = bonusVacation.split(";");

			for (int i = 0; i < bonus.length; i++) {
				if (yearsInCompanyNextYear >= Integer.valueOf(bonus[i])) {
					vacationNextYear++;
				}
			}

			return vacationNextYear;
		}

		return null;
	}

	/**
	 * 
	 * @param birthDate
	 * @param currentDate
	 * @return
	 */
	public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
		if ((birthDate != null) && (currentDate != null)) {
			return Period.between(birthDate, currentDate).getYears();
		} else {
			return 0;
		}
	}
}
