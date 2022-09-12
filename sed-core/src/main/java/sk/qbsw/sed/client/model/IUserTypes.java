package sk.qbsw.sed.client.model;

/**
 * Constants list
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public interface IUserTypes {
	
	/**
	 * application administrator (1)
	 */
	public static final Long SYS_MAN = 1l;
	/**
	 * client administratoir (2)
	 */
	public static final Long ORG_MAN = 2l;
	/**
	 * zamestanec (3)
	 */
	public static final Long EMPLOYEE = 3l;
	/**
	 * uctovnik (4)
	 */
	public static final Long ACCOUNTANT = 4l;
	/**
	 * ucet recepcie (5)
	 */
	public static final Long RECEPTION = 5l;
	/**
	 * zamestnanec s podriadenymi (1001)
	 */
	public static final Long EMPLOYEE_WITH_SUB = 1001l;
	/**
	 * zamstnanec bez podriadnych (1002)
	 */
	public static final Long EMPLOYEE_WO_SUB = 1002l;
}
