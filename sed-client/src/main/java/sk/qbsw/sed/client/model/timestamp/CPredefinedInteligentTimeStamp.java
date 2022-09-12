package sk.qbsw.sed.client.model.timestamp;

import java.io.Serializable;
import java.util.Date;

/**
 * Model used for predefine values for adding new timesheet in inteligent mode
 * 
 * @author Dalibor Rak
 * @version 1.1
 * @since 1.1
 */
@SuppressWarnings("serial")
public class CPredefinedInteligentTimeStamp implements Serializable {
	// mode
	public static final int MODE_NOTHING = 1;
	public static final int MODE_WORK_STARTED = 2;
	public static final int MODE_WORK_FINISHED = 3;
	public static final int MODE_WORKBREAK_STARTED = 4;
	public static final int MODE_WORKBREAK_FINISHED = 5;
	public static final int MODE_INVALID_PROJECT_OR_ACTIVITY = 6;

	// non work activities
	public static final int ACTIVITY_BREAK = -1;
	public static final int ACTIVITY_PARAGRAPH = -7;
	public static final int ACTIVITY_WORK_ALERTNESS = -8; // pohotovost
	public static final int ACTIVITY_WORK_INTERACTIVE = -9; // zasah
	public static final int ACTIVITY_PHYSICIAN_VISIT = -10; // Návšteva lekára
	public static final int ACTIVITY_ACCOMPANYING = -11; // Sprevádzanie rodinného príslušníka
	public static final int ACTIVITY_PRENATAL_MEDICAL_CARE = -12; // Prenatalána lekárska starostlivosť

	// other useful constants
	public static final int ACTIVITY_WORKING_DEFAULT = -101;
	public static final int ACTIVITY_NON_WORKING_DEFAULT = -102;

	/**
	 * Model on the screen
	 */
	private CTimeStampAddRecord model;

	/**
	 * Useful flag of the original mode , when project/activity is valid
	 */
	private int originalMode;

	/**
	 * date of the previous unclosed record
	 */
	private Date unclosedDate;

	/**
	 * Mode of the screen
	 */
	private int mode;

	private Boolean inteligentStopWorkFlag = Boolean.FALSE;

	private Long durationOfLastTimestamp; // in miliseconds, vrati trvanie casovej znacky za dnestny den

	private Long timeStampId;

	public CTimeStampAddRecord getModel() {
		return this.model;
	}

	public void setModel(final CTimeStampAddRecord model) {
		this.model = model;
	}

	public int getMode() {
		return this.mode;
	}

	public void setMode(final int mode) {
		this.mode = mode;
	}

	public int getOriginalMode() {
		return this.originalMode;
	}

	public void setOriginalMode(int originalMode) {
		this.originalMode = originalMode;
	}

	public Date getUnclosedDate() {
		return unclosedDate;
	}

	public void setUnclosedDate(Date unclosedDate) {
		this.unclosedDate = unclosedDate;
	}

	public CPredefinedInteligentTimeStamp getClone() {
		CPredefinedInteligentTimeStamp retVal = new CPredefinedInteligentTimeStamp();
		retVal.setMode(mode);
		retVal.setOriginalMode(originalMode);
		if (unclosedDate != null) {
			retVal.setUnclosedDate(new Date(unclosedDate.getTime()));
		}
		if (model != null) {
			retVal.setModel(model.getClone());
		}

		return retVal;
	}

	public Boolean getInteligentStopWorkFlag() {
		return inteligentStopWorkFlag;
	}

	public void setInteligentStopWorkFlag(Boolean inteligentStopWorkFlag) {
		this.inteligentStopWorkFlag = inteligentStopWorkFlag;
	}

	public Long getDurationOfLastTimestamp() {
		return durationOfLastTimestamp;
	}

	public void setDurationOfLastTimestamp(Long durationOfLastTimestamp) {
		this.durationOfLastTimestamp = durationOfLastTimestamp;
	}

	public Long getTimeStampId() {
		return timeStampId;
	}

	public void setTimeStampId(Long timeStampId) {
		this.timeStampId = timeStampId;
	}
}
