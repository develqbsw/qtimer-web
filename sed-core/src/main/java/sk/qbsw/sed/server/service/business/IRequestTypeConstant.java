package sk.qbsw.sed.server.service.business;

/**
 * User request type constants
 * 
 * @author rosenberg
 *
 */
public interface IRequestTypeConstant {
	
	/**
	 * dovolenka
	 */
	public static final String RQTYPE_VACATION = "RQTYPE_VACATION";
	public static final String RQTYPE_VACATION_CODE = "D";
	public static final int RQTYPE_VACATION_ID = 1;

	/**
	 * praceneschopnost/lekar
	 */
	public static final String RQTYPE_SICKNESS = "RQTYPE_SICKNESS";
	public static final String RQTYPE_SICKNESS_CODE = "PN";
	public static final int RQTYPE_SICKNESS_ID = 2;

	/**
	 * nahradne volno
	 */
	public static final String RQTYPE_REPLWORK = "RQTYPE_REPLWORK";
	public static final String RQTYPE_REPLWORK_CODE = "NV";
	public static final int RQTYPE_REPLWORK_ID = 3;

	/**
	 * pracovna cesta
	 */
	public static final String RQTYPE_BUSTRIP = "RQTYPE_BUSTRIP";
	public static final String RQTYPE_BUSTRIP_CODE = "SC";
	public static final int RQTYPE_BUSTRIP_ID = 4;

	/**
	 * prekazka v praci (60% a ine)
	 */
	public static final String RQTYPE_WORKBREAK = "RQTYPE_WORKBREAK";
	public static final String RQTYPE_WORKBREAK_CODE = "PVP";
	public static final int RQTYPE_WORKBREAK_ID = 5;

	/**
	 * skolenie
	 */
	public static final String RQTYPE_STAFF_TRAINING = "RQTYPE_STAFF_TRAINING";
	public static final String RQTYPE_STAFF_TRAINING_CODE = "ST";
	public static final int RQTYPE_STAFF_TRAINING_ID = 6;

	/**
	 * praca z domu
	 */
	public static final String RQTYPE_WORK_AT_HOME = "RQTYPE_WORK_AT_HOME";
	public static final String RQTYPE_WORK_AT_HOME_CODE = "WAH";
	public static final int RQTYPE_WORK_AT_HOME_ID = 7;

	/**
	 * pomocny rozlisovak: pouzivatel je nepritomny a nema ziadnu ziadost
	 */
	public static final String RQTYPE_NO_REQUEST = "RQTYPE_NO_REQUEST";

	/**
	 * praca mimo pracoviska
	 */
	public static final String RQTYPE_OUT_OF_OFFICE = "RQTYPE_OUT_OF_OFFICE";
	public static final String RQTYPE_OUT_OF_OFFICE_CODE = "OOO";
	public static final int RQTYPE_OUT_OF_OFFICE_ID = 8;

	/**
	 * pouzivatel je neprítomný z dôvodu návštevy lekára / sprevádzania rod.
	 * príslušníka
	 */
	public static final String RQTYPE_NO_REQUEST_DOCTOR_VISIT = "RQTYPE_NO_REQUEST_DOCTOR_VISIT";
	
}
