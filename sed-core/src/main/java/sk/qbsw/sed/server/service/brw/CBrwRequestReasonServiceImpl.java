package sk.qbsw.sed.server.service.brw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;
import sk.qbsw.sed.client.service.brw.IBrwRequestReasonService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.dao.IRequestReasonDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.codelist.CRequestReason;

/**
 * 
 * @author rosenberg
 * @version 1.6.6.2
 * @since 1.6.6.2
 */
@Service(value = "brwRequestReasonService")
public class CBrwRequestReasonServiceImpl implements IBrwRequestReasonService {
	@Autowired
	IRequestReasonDao requestReasonDao;

	@Transactional(readOnly = true)
	@Override
	public List<CRequestReasonRecord> loadData(Integer startRow, Integer endRow, String sortProperty, boolean sortAsc) throws CBusinessException {

		final Long clientId = CServletSessionUtils.getLoggedUser().getClientInfo().getClientId();

		final List<CRequestReason> reasons = this.requestReasonDao.findClientOrSystemRecords(clientId, startRow, endRow, sortProperty, sortAsc);

		final List<CRequestReasonRecord> retVal = new ArrayList<>();
		for (final CRequestReason reason : reasons) {
			retVal.add(reason.convert());
		}

		return retVal;
	}

	@Transactional(readOnly = true)
	@Override
	public Long count() throws CBusinessException {
		final Long clientId = CServletSessionUtils.getLoggedUser().getClientInfo().getClientId();
		return this.requestReasonDao.count(clientId);
	}
}
