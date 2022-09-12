package sk.qbsw.sed.server.dao.hibernate;

import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.server.dao.ITimesheetStateDao;
import sk.qbsw.sed.server.model.codelist.CTimeSheetRecordStatus;

@Repository
public class CTimesheetStateDao extends AHibernateDao<CTimeSheetRecordStatus> implements ITimesheetStateDao {
}
