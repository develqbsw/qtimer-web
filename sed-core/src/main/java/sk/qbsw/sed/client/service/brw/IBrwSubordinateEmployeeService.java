package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface IBrwSubordinateEmployeeService {

	public List<CUserDetailRecord> fetch() throws CBusinessException, CSecurityException;
}
