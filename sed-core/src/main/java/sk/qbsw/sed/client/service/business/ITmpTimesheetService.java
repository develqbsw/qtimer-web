package sk.qbsw.sed.client.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTmpTimeStampAddRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.4.1
 */
public interface ITmpTimesheetService {
	
	public CLockRecord add(final CTmpTimeSheet record) throws CBusinessException, CSecurityException;

	public CLockRecord addNoTransaction(final CTmpTimeSheet tableRecord) throws CBusinessException, CSecurityException;

	public CLockRecord update(final CTmpTimeSheet tableRecord) throws CBusinessException, CSecurityException;

	public void updateDurationCorrected(final CTmpTimeSheet tableRecord) throws CBusinessException, CSecurityException;

	public CLockRecord updateNoTransaction(final CTmpTimeSheet tableRecord) throws CBusinessException, CSecurityException;

	public CTmpTimeStampAddRecord getNewEmptyRecord(final Long userId, final IFilterCriteria filter) throws CBusinessException, CSecurityException;

	public void deleteAll() throws CBusinessException, CSecurityException;

	public void deleteById(final Long recordId) throws CBusinessException, CSecurityException;

	public void createCopyById(final Long recordId) throws CBusinessException, CSecurityException;

	public List<CCodeListRecord> findRealizedGenerationProcesses(Long userId);
}
