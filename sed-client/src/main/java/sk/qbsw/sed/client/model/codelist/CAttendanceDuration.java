package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class CAttendanceDuration implements Serializable {
	private Calendar day;
	private Long duration;

	public Calendar getDay() {
		return day;
	}

	public void setDay(Calendar day) {
		this.day = day;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
}
