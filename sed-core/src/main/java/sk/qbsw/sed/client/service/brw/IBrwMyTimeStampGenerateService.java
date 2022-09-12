package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.exception.CJiraException;

/**
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.4.1
 */
public interface IBrwMyTimeStampGenerateService {

	public List<CTmpTimeSheet> fetch(int startRow, int count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBusinessException, CJiraException;

	public CTmpTimeSheet add(final CTmpTimeSheet timeStampRecord) throws CBusinessException;

	public CTmpTimeSheet update(CTmpTimeSheet record) throws CBusinessException;

	public void delete(CTmpTimeSheet record) throws CBusinessException;

	public void deleteAllForUser() throws CBusinessException;
}
