package sk.qbsw.sed.client.model;

/**
 * 
 * @author rosenberg
 * 
 */
public interface IActivityConstant {
	// -- ZASAH --
	public static final Long NOT_WORK_INTERACTIVEWORK = new Long(-9);

	// -- Pohotovost --
	public static final Long NOT_WORK_ALERTNESSWORK = new Long(-8);

	// -- Prekazky v praci --
	public static final Long NOT_WORK_WORKBREAK = new Long(-6);

	// -- Praceneschopnost --
	public static final Long NOT_WORK_SICKNESS = new Long(-5);

	// -- Nahradne volno --
	public static final Long NOT_WORK_REPLWORK = new Long(-4);

	// -- Dovolenka --
	public static final Long NOT_WORK_HOLIDAY = new Long(-3);
}
