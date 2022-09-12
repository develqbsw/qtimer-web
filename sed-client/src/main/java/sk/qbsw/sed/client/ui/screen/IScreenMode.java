package sk.qbsw.sed.client.ui.screen;

/**
 * Interface for identifying screen mode
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface IScreenMode {
	
	/**
	 * Screen mode add
	 */
	public static final int MODE_ADD = 3;
	/**
	 * Screen mode detail
	 */
	public static final int MODE_DETAIL = 1;

	/**
	 * Screen mode delete
	 */
	public static final int MODE_DELETE = 4;

	/**
	 * Screen mode modify
	 */
	public static final int MODE_MODIFY = 2;
}
