package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.codelist.CRequestType;

/**
 * DAO for accessing user type
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IRequestTypeDao extends IDao<CRequestType> {
	
	public List<CRequestType> findAllValid();

	public List<CRequestType> findRecordsForRequestReason();

	public CRequestType findRecordById(Long id);

	/**
	 * finds request type for activity
	 * 
	 * @param activityId
	 * @return
	 */
	public CRequestType findRecordByActivity(Long activityId);
}
