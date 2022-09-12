package sk.qbsw.sed.server.dao;

import java.util.Date;
import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.domain.CRequestForEmail;

public interface IRequestForEmailDao extends IDao<CRequestForEmail> {
	
	/**
	 * Finds all records not canceled and matching time interval for selected client
	 * 
	 * @param dateFrom
	 * @param clientId
	 * @return
	 */
	public List<CRequestForEmail> findAllNotCancelledInTimeInterval(Date dateFrom, Long clientId);
}
