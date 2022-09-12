package sk.qbsw.sed.client.model;

/**
 * Constants used for searching and not entered in the db model
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface ISearchConstants {
	
	/**
	 * All records
	 */
	public static Long ALL = -100l;

	/**
	 * All non-working records
	 */
	public static Long ALL_WORKING = -101l;

	/**
	 * All working records
	 */
	public static Long ALL_NON_WORKING = -102l;

	/**
	 * No record
	 */
	public static Long NONE = -103l;

	public static Long PROJECT_GROUP_MY = -105l;

	/**
	 * Last used
	 */
	public static Long PROJECT_GROUP_LAST_USED = -106l;

	/**
	 * Last used
	 */
	public static Long PROJECT_GROUP_ALL_OTHER = -107l;

	/**
	 * All active items with flag ("valid", "active", etc. ) = true obviously
	 */
	public static Long ALL_ACTIVE = -108l;

	/**
	 * All not active items with flag ("valid", "active", etc. ) = false obviously
	 */
	public static Long ALL_NOT_ACTIVE = -109l;
	public static Long ALL_MY_PROJECTS = -110l;
	public static Long ALL_MY_ACTIVITIES = -111l;
	public static Long ACTIVITY_GROUP_MY = -112l;
	public static Long ACTIVITY_GROUP_LAST_USED = -113l;
	public static Long ACTIVITY_GROUP_ALL_OTHER = -114l;
	
	// Pohotovosť/Zásah - filter na obrazovke výkazu práce
	public static Long ALL_ALERTNESS_INTERACT_WORK = -115l;
}
