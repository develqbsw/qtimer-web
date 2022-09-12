package sk.qbsw.sed.server.model;

/**
 * 
 * @author rosenberg
 *
 */
public interface IUserRequestStatus {
	
	/**
	 * look into t_ct_request_status
	 */
	static final String RQSTATE_CREATED = "RQSTATE_CREATED";
	static final int RQSTATE_CREATED_ID = 1;

	/**
	 * 
	 */
	static final String RQSTATE_CANCELLED = "RQSTATE_CANCELLED";
	static final int RQSTATE_CANCELLED_ID = 2;

	/**
	 * 
	 */
	static final String RQSTATE_APPROVED = "RQSTATE_APPROVED";
	static final int RQSTATE_APPROVED_ID = 3;

	/**
	 * 
	 */
	static final String RQSTATE_DECLINED = "RQSTATE_DECLINED";
	static final int RQSTATE_DECLINED_ID = 4;
}
