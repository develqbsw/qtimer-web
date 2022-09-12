package sk.qbsw.sed.client.ui.screen.codelist.upload;

public interface IUploadConstant {
	public static final long MAX_FILE_SIZE = 2000000l;

	// HTTP request parameter name
	public static final String FILEFLAG = "fileflag";

	// HTTP request parameter values
	public static final String FILEFLAG_ACTIVITIES = "activities";
	public static final String FILEFLAG_PROJECTS = "projects";
	public static final String FILEFLAG_EMPLOYEES = "employees";
	public static final String FILEFLAG_OUT_TEMPLATE = "outTemplate";

	/**
	 * upload process successfully finished
	 */
	public static final String UPLOAD_RESULT_OK = "0";

	/**
	 * upload security error
	 */
	public static final String UPLOAD_RESULT_ERR1 = "1";

	/**
	 * file structure error
	 */
	public static final String UPLOAD_RESULT_ERR2 = "2";

	/**
	 * big file error
	 */
	public static final String UPLOAD_RESULT_ERR3 = "3";

	/**
	 * another upload error
	 */
	public static final String UPLOAD_RESULT_ERR4 = "4";

	public static final String MSG_START = "MSG_START";
	public static final String MSG_STOP = "MSG_STOP";
	public static final String ERR_START = "ERR_START";
	public static final String ERR_STOP = "ERR_STOP";
	public static final String MSG_LABEL = "upload status:";
	public static final String ERR_LABEL = "error:";
}
