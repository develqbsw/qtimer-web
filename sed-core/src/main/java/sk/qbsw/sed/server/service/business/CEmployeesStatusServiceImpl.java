package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.client.model.IStatusConstants;
import sk.qbsw.sed.client.model.brw.CEmployeesStatus;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CEmployeesStatusBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.service.business.IEmployeesStatusService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.ITimesheetRecordDao;
import sk.qbsw.sed.server.dao.IViewEmployeesStatusDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.brw.CViewEmployeesStatus;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;

@Service(value = "employeesStatusService")
public class CEmployeesStatusServiceImpl implements IEmployeesStatusService {
	@Autowired
	private IViewEmployeesStatusDao viewEmployeesStatusDao;

	@Autowired
	private ITimesheetRecordDao timesheetDao;

	private static Map<Long, Map<Long, CEmployeesStatusNew>> map = new HashMap<>();

	@Override
	public void clear() {
		synchronized (map) {
			map = new HashMap<>();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CEmployeesStatusNew> fetch() throws CBusinessException {
		Map<Long, CEmployeesStatusNew> mapOfEmployees = map.get(getClientId());

		if (mapOfEmployees == null) {
			mapOfEmployees = new HashMap<>();
			final List<CViewEmployeesStatus> employeeStatusList = this.viewEmployeesStatusDao.findAll(this.getClientId());

			for (final CViewEmployeesStatus item : employeeStatusList) {
				mapOfEmployees.put(item.getId(), item.convertToEmployeesStatusNew());
			}
			map.put(getClientId(), mapOfEmployees);
		}
		return new ArrayList<>(mapOfEmployees.values());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CEmployeesStatus> fetchByCriteria(CEmployeesStatusBrwFilterCriteria criteria) throws CBusinessException {
		Map<Long, CEmployeesStatusNew> mapOfEmployees = map.get(getClientId());

		if (mapOfEmployees == null) {
			fetch();
			mapOfEmployees = map.get(getClientId());
		}

		List<CEmployeesStatus> list = new LinkedList<>();

		for (CEmployeesStatusNew statusNew : mapOfEmployees.values()) {
			if (criteria.getZoneId() != null) {
				if (statusNew.getZoneId() != null && statusNew.getZoneId().equals(criteria.getZoneId())) {
					if (criteria.getAtWorkplace() != null && criteria.getAtWorkplace()) {
						if (IStatusConstants.IN_WORK.equals(statusNew.getStatus()) || IStatusConstants.WORK_BREAK.equals(statusNew.getStatus())) {
							list.add(statusNew.convert());
						}
					} else {
						list.add(statusNew.convert());
					}
				}
			} else {
				if (criteria.getAtWorkplace() != null && criteria.getAtWorkplace()) {
					if (IStatusConstants.IN_WORK.equals(statusNew.getStatus()) || IStatusConstants.WORK_BREAK.equals(statusNew.getStatus())) {
						list.add(statusNew.convert());
					}
				} else {
					list.add(statusNew.convert());
				}
			}
		}

		// SED-757 - Zoznam zamestnancov : zle zoradovanie - Mena v zozname
		// zamestnancov (cervena obrazovka) nie su zoradene abecedne
		Collections.sort(list, new Comparator<CEmployeesStatus>() {
			@Override
			public int compare(CEmployeesStatus es1, CEmployeesStatus es2) {
				int res = es1.getSurname().compareToIgnoreCase(es2.getSurname());
				if (res != 0) {
					return res;
				}
				return es1.getSurname().compareToIgnoreCase(es2.getSurname());
			}
		});

		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public String checkStatus(Long userId) throws CBusinessException {
		String status = getStatus(userId);

		Map<Long, CEmployeesStatusNew> mapOfEmployees = map.get(getClientId());
		if (mapOfEmployees == null) {
			fetch();
			mapOfEmployees = map.get(getClientId());
		}

		CEmployeesStatusNew employeesStatus = mapOfEmployees.get(userId);
		if (employeesStatus == null) {
			// pravdepodobne bol vytvoreny novy zamestnanec a nie je este v zozname
			clear();
			fetch();
			mapOfEmployees = map.get(getClientId());
			employeesStatus = mapOfEmployees.get(userId);
		}

		if (employeesStatus.getStatus().equals(status)) {
			return null;
		} else {
			employeesStatus.setStatus(status);
			return status;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public String checkStatus(CTimeStampRecord record) throws CBusinessException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(record.getDateFrom());

		if (DateUtils.isSameDay(cal, Calendar.getInstance())) {
			return checkStatus(record.getEmployeeId());
		} else {
			return null;
		}
	}

	/**
	 * Gets id of logged user
	 * 
	 * @param crit
	 * @return
	 */
	private Long getClientId() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser();
		return loggedUser.getClientInfo().getClientId();
	}

	private String getStatus(Long userId) {
		Calendar now = Calendar.getInstance();

		CTimeSheetRecord record = timesheetDao.findLast(userId, now);

		if (record != null && DateUtils.isSameDay(record.getTimeFrom(), Calendar.getInstance())) {
			// posledny zaznam je za dnes

			if (record.getActivity().getWorking()) {
				if (record.getTimeTo() != null) {
					return IStatusConstants.OUT_OF_WORK;
				} else {
					String status = IStatusConstants.IN_WORK;

					if (record.getOutside()) {
						status = IStatusConstants.MEETING;
					} else if (record.getHomeOffice()) {
						status = IStatusConstants.HOME_OFFICE;
					}

					return status;
				}
			} else {
				if (record.getTimeTo() != null) {
					return IStatusConstants.OUT_OF_WORK;
				} else {
					return IStatusConstants.WORK_BREAK;
				}
			}
		} else {
			return IStatusConstants.NOT_IN_WORK;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void setStatus(Long userId, String status) throws CBusinessException {
		Map<Long, CEmployeesStatusNew> mapOfEmployees = map.get(getClientId());
		if (mapOfEmployees == null) {
			fetch();
			mapOfEmployees = map.get(getClientId());
		}

		CEmployeesStatusNew employeesStatus = mapOfEmployees.get(userId);
		if (employeesStatus == null) {
			// pravdepodobne bol vytvoreny novy zamestnanec a nie je este v zozname
			clear();
			fetch();
			mapOfEmployees = map.get(getClientId());
			employeesStatus = mapOfEmployees.get(userId);
		}

		employeesStatus.setStatus(status);
	}
}
