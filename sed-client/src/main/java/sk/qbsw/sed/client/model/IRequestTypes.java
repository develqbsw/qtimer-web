package sk.qbsw.sed.client.model;

/**
 * Request types constants
 * 
 * @author Dalibor Rak
 * @Version 1.0
 * @since 0.1
 * 
 */
public interface IRequestTypes {
	/**
	 * dovolenka
	 */
	public static final Long ID_H = 1l;

	/**
	 * praceneschopnost/lekar
	 */
	public static final Long ID_SD = 2l;

	/**
	 * nahradne volno
	 */
	public static final Long ID_FP = 3l;

	/**
	 * pracovna cesta
	 */
	public static final Long ID_WT = 4l;

	/**
	 * prekazka v praci (60%)
	 */
	public static final Long ID_WB = 5l;

	/**
	 * skolenie
	 */
	public static final Long ID_ST = 6l;

	/**
	 * praca z domu
	 */
	public static final Long ID_WAH = 7l;
}
