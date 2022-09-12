package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.service.brw.IBrwHolidayService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IHolidayDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CHoliday;

/**
 * 
 * @author lobb
 *
 */
@Service(value = "brwHolidayService")
public class CBrwHolidayServiceImpl implements IBrwHolidayService {

	@Autowired
	private IHolidayDao holidayDao;

	/**
	 * @see IBrwHolidayService#loadData(Integer, Integer, String, boolean,
	 *      IFilterCriteria)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CHolidayRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();

		final List<CHoliday> holidays = this.holidayDao.findAllClientsHolidays(loggedUser.getClientInfo().getClientId(), criteria, startRow, endRow, sortProperty, sortAsc);

		final List<CHolidayRecord> retVal = new ArrayList<>();

		for (final CHoliday cHoliday : holidays) {
			retVal.add(cHoliday.convert());
		}

		return retVal;
	}

	/**
	 * @see IBrwHolidayService#count(IFilterCriteria)
	 */
	@Override
	@Transactional(readOnly = true)
	public Long count(IFilterCriteria criteria) throws CBusinessException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return this.holidayDao.count(loggedUser.getClientInfo().getClientId(), criteria);
	}
}
