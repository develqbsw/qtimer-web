package sk.qbsw.sed.client.model.timestamp;

import java.io.Serializable;

/**
 * Predefined version of timestamp
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
public class CPredefinedTimeStamp implements Serializable {
	/**
	 * ID of timesheet to load on modify
	 */
	private Long id;

	/**
	 * Screen mode to switch to
	 */
	private int mode;

	/**
	 * Record to show
	 */
	private CTimeStampRecord recordToShow;

	/**
	 * Constructor for serialization
	 */
	public CPredefinedTimeStamp() {
	}

	/**
	 * Constructor for development
	 */
	public CPredefinedTimeStamp(Long id, int screenMode, CTimeStampRecord recordToShow) {
		this.id = id;
		this.mode = screenMode;
		this.recordToShow = recordToShow;
	}

	public Long getId() {
		return id;
	}

	public int getMode() {
		return mode;
	}

	public CTimeStampRecord getRecordToShow() {
		return recordToShow;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setRecordToShow(CTimeStampRecord recordToShow) {
		this.recordToShow = recordToShow;
	}
}
