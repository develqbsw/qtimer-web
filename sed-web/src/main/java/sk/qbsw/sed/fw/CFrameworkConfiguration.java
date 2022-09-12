package sk.qbsw.sed.fw;

/**
 * 
 * @author Peter Božík
 * @version 0.1
 * @since 0.1
 *
 */
public class CFrameworkConfiguration {
	
	public static final String HTML_CLASS_ATTRIBUTE_SEPARATOR = " ";
	public static final Integer ROWS_PER_TABLE = 10;
	public static final Boolean USE_BREADCRUMB = true;
	public static final boolean TABLE_SIZE_WITH_DATA = true;
	public static final boolean CHECK_TABLE_ORDER_PARAMETERS = false;
	public static final Boolean SHOW_PAGE_SMALL_TITLE = false;
	public static final Integer TABLE_BUTTON_COLLAPSE_THRESHOLD = 2;// if this value is reached, buttons are collapsed intto one

	public static final boolean DISPLAY_COMPONENT_ERROR_MESSAGES_IN_FEEDBACKPANEL = false;

	public static final String PAGE_URL_UPDATE = "update";
	public static final String PAGE_URL_DETAIL = "detail";
	public static final String PAGE_URL_CREATE = "create";
	
	private CFrameworkConfiguration() {
		// Auto-generated constructor stub
	}
}
