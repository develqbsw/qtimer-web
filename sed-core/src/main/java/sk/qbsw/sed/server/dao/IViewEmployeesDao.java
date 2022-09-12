package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.server.model.report.CViewEmployees;

public interface IViewEmployeesDao extends IDao<CViewEmployees> {

	public List<CReportModel> findUsersRecordsForEmployeesReport(Long clientId, Boolean onlyValid);
}
