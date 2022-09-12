package sk.qbsw.sed.server.service.codelist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.request.CModifyHolidayRequest;
import sk.qbsw.sed.client.service.codelist.IHolidayService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IClientDao;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CHoliday;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.util.CEasterUtils;

@Service(value = "holidayService")
public class CHolidayServiceImpl implements IHolidayService {

	@Autowired
	IClientDao clientDao;

	@Autowired
	IHolidayDao holidayDao;

	/**
	 * @see IHolidayService#add(CHolidayRecord)
	 */
	@Transactional
	@Override
	public Long add(CHolidayRecord record) throws CBusinessException {
		// nove vytiahne client id z prihlaseneho usera
		record.setClientId(CServletSessionUtils.getLoggedUser().getClientInfo().getClientId());
		CHoliday entity = convertToEntity(record);
		holidayDao.saveOrUpdate(entity);
		return entity.getId();
	}

	/**
	 * @see IHolidayService#modify(Long, CHolidayRecord)
	 */
	@Transactional
	@Override
	public void modify(CModifyHolidayRequest newRecord) throws CBusinessException {
		CHoliday entity = holidayDao.findById(newRecord.getId());

		Calendar day = Calendar.getInstance();
		day.setTime(newRecord.getNewRecord().getDay());
		entity.setDay(day);

		entity.setDescription(newRecord.getNewRecord().getDescription());
		entity.setValid(newRecord.getNewRecord().getActive());

		holidayDao.saveOrUpdate(entity);
	}

	/**
	 * @see IHolidayService#getDetail(Long)
	 */
	@Transactional(readOnly = true)
	@Override
	public CHolidayRecord getDetail(Long id) {
		CHoliday entity = holidayDao.findById(id);
		return convertFromEntity(entity);
	}

	/**
	 * @see IHolidayService#getClientRecordsForTheYear(Long, Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<CHolidayRecord> getClientRecordsForTheYear(Long clientId, Integer selectedYearDate) throws CBusinessException {
		List<CHoliday> entities = holidayDao.findAllValidClientsHolidays(clientId, selectedYearDate);
		List<CHolidayRecord> records = new ArrayList<>();
		for (CHoliday entity : entities) {
			records.add(convertFromEntity(entity));
		}

		return records;
	}

	/**
	 * @see IHolidayService#cloneCurrentYearClientRecordsForNextYear(Long, Integer)
	 */
	@Transactional
	@Override
	public List<CHolidayRecord> cloneCurrentYearClientRecordsForNextYear(Long clientId) throws CBusinessException {
		Calendar today = Calendar.getInstance();

		Integer sourceYear = new Integer(today.get(Calendar.YEAR));
		Integer nextYear = sourceYear + 1;

		// if there are records in next year, can't clone
		List<CHoliday> nextYearHolidayList = holidayDao.findAllValidClientsHolidays(clientId, nextYear);
		if (nextYearHolidayList != null && !nextYearHolidayList.isEmpty()) {
			throw new CBusinessException(CClientExceptionsMessages.HOLIDAYS_CANT_BE_GENERATED);
		}

		List<CHoliday> sourceHolidayList = holidayDao.findAllValidClientsHolidays(clientId, today.get(Calendar.YEAR));
		if (sourceHolidayList != null && !sourceHolidayList.isEmpty()) {
			for (CHoliday oldHoliday : sourceHolidayList) {
				CHoliday newHoliday;
				if (compareDays(oldHoliday.getDay(), CEasterUtils.getEasterFriday(sourceYear))) {
					newHoliday = getCloneWithoutId(oldHoliday);
					newHoliday.setDay(CEasterUtils.getEasterFriday(nextYear));

				} else if (compareDays(oldHoliday.getDay(), CEasterUtils.getEasterSunday(sourceYear))) {
					newHoliday = getCloneWithoutId(oldHoliday);
					newHoliday.setDay(CEasterUtils.getEasterSunday(nextYear));
				} else if (compareDays(oldHoliday.getDay(), CEasterUtils.getEasterMonday(sourceYear))) {
					newHoliday = getCloneWithoutId(oldHoliday);
					newHoliday.setDay(CEasterUtils.getEasterMonday(nextYear));
				} else {
					newHoliday = getCloneWithoutId(oldHoliday);
					newHoliday.getDay().set(Calendar.YEAR, nextYear);
				}

				this.holidayDao.saveOrUpdate(newHoliday);
			}
		}
		List<CHoliday> newHolidayList = holidayDao.findAllValidClientsHolidays(clientId, nextYear);
		List<CHolidayRecord> records = new ArrayList<>();
		for (CHoliday entity : newHolidayList) {
			records.add(convertFromEntity(entity));
		}

		return records;
	}

	private CHolidayRecord convertFromEntity(CHoliday entity) {
		CHolidayRecord record = new CHolidayRecord();
		record.setId(entity.getId());
		record.setClientId(entity.getClient().getId());
		record.setDay(entity.getDay().getTime());
		record.setDescription(entity.getDescription());
		record.setActive(entity.getValid());

		return record;
	}

	private CHoliday convertToEntity(CHolidayRecord record) throws CSecurityException {
		CHoliday entity = new CHoliday();

		entity.setId(record.getId());

		CClient client = this.clientDao.findClientById(record.getClientId());
		entity.setClient(client);

		Calendar day = Calendar.getInstance();
		day.setTime(record.getDay());
		entity.setDay(day);

		entity.setDescription(record.getDescription());
		entity.setValid(record.getActive());

		return entity;
	}

	private CHoliday getCloneWithoutId(CHoliday holiday) {
		CHoliday cloned = new CHoliday();
		cloned.setClient(holiday.getClient());
		cloned.setDay((Calendar) holiday.getDay().clone());
		cloned.setDescription(holiday.getDescription());
		cloned.setValid(holiday.getValid());

		return cloned;
	}

	private Boolean compareDays(Calendar c1, Calendar c2) {
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
			if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
				if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
}
