package sk.qbsw.sed.server.service.business;

/**
 * User request status constants
 * 
 * @author rosenberg
 *
 */
public class IRequestStatusConstant {
	
	public static final String RQSTATE_CREATED = "RQSTATE_CREATED";
	public static final String RQSTATE_CANCELLED = "RQSTATE_CANCELLED";
	public static final String RQSTATE_APPROVED = "RQSTATE_APPROVED";
	public static final String RQSTATE_DECLINED = "RQSTATE_DECLINED";

	public static final Long CREATED = new Long(1);
	public static final Long CANCELLED = new Long(2);
	public static final Long APPROVED = new Long(3);
	public static final Long DECLINED = new Long(4);
	
	private IRequestStatusConstant() {
		// Auto-generated constructor stub
	}
}
