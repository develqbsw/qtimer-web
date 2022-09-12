package sk.qbsw.sed.server.service.codelist;

/**
 * 
 * @author rosenberg
 * 
 */
public interface IActivityConstant {
	
	/**
	 * Návšteva lekára alebo Sprevádzanie rodinného príslušníka
	 */
	public static final String NOT_WORK_WORKBREAK_PHYSICIAN_VISIT_CODE = "P";

	// BREAK
	public static final Long BREAK = new Long(-1);

	// -- ZASAH --
	/**
	 * NOT_WORK_INTERACTIVE = Long(-9)
	 */
	public static final Long NOT_WORK_INTERACTIVEWORK = new Long(-9);

	// -- NOT_WORK_MINCHECK_VALUE --
	/**
	 * !!! UPDATE THIS EVERYTIME IF NEW NOT_WORK ITEM WILL BE ADDED !!! current
	 * value: NOT_WORK_INTERACTIVEWORK = (-9)
	 */
	public static final Long NOT_WORK_MINCHECK_VALUE = NOT_WORK_INTERACTIVEWORK;

	// -- Pohotovost --
	/**
	 * NOT_WORK_ALERTNESS = Long(-8)
	 */
	public static final Long NOT_WORK_ALERTNESSWORK = new Long(-8);

	/**
	 * Konstanta urcenena pre identifikovanie aktivity Prekazka v praci - 60%
	 */
	public static final String NOT_WORK_WORKBREAK_PERCET60_CODE = "NOT_WORK_WORKBREAK_PERCET60_CODE";

	// -- Prekazky v praci --
	/**
	 * NOT_WORK_WORKBREAK = Long(-6)
	 */
	public static final Long NOT_WORK_WORKBREAK = new Long(-6);
	/**
	 * Prekazky v praci
	 */
	public static final String NOT_WORK_WORKBREAK_CODE = "PVP";

	// -- Praceneschopnost --
	/**
	 * NOT_WORK_SICKNESS = Long(-5)
	 */
	public static final Long NOT_WORK_SICKNESS = new Long(-5);
	/**
	 * Praceneschopnost
	 */
	public static final String NOT_WORK_SICKNESS_CODE = "PN";

	// -- Nahradne volno --
	/**
	 * NOT_WORK_REPLWORK = Long(-4)
	 */
	public static final Long NOT_WORK_REPLWORK = new Long(-4);
	/**
	 * Nahradne volno
	 */
	public static final String NOT_WORK_REPLWORK_CODE = "NV";

	// -- Dovolenka --
	/**
	 * NOT_WORK_HOLIDAY = Long(-3)
	 */
	public static final Long NOT_WORK_HOLIDAY = new Long(-3);
	/**
	 * Dovolenka
	 */
	public static final String NOT_WORK_HOLIDAY_CODE = "D";

	// -- standartny den pracovneho pokoja: sobota, nedela, statny sviatok --
	/**
	 * standartny den pracovneho pokoja: sobota, nedela, statny sviatok
	 */
	public static final String NOT_WORK_FREE_DAY = "SV";

	// -------- activity hot keys for reception user
	public static final String ACTIVITY_WORK_START = "p";
	public static final String ACTIVITY_WORK_STOP = "l";
	public static final String ACTIVITY_LUNCH_START = "o";
	public static final String ACTIVITY_LUNCH_STOP = "k";
	public static final String ACTIVITY_BREAK_START = "i";
	public static final String ACTIVITY_BREAK_STOP = "j";
	public static final String ACTIVITY_WORK_OUTSIDE_START = "u";
	public static final String ACTIVITY_WORK_OUTSIDE_STOP = "h";
}
