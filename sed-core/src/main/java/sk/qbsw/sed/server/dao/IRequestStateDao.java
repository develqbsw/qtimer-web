package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.codelist.CRequestStatus;

/**
 * DAO for accessing user type
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IRequestStateDao extends IDao<CRequestStatus> {
	public List<CRequestStatus> findAllValid();
}
