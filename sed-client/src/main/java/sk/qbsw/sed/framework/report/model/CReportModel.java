package sk.qbsw.sed.framework.report.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

public class CReportModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8619184605885325930L;

	private final HashMap<Integer, Object> values = new HashMap<>();

	public void setValue(final Integer key, final Object value) {
		this.values.put(key, value);
	}

	public HashMap<Integer, Object> getValues() {
		return values;
	}

	public String getString(final Integer key) {
		return (String) this.values.get(key);
	}

	public Calendar getCalendar(final Integer key) {
		Object value = this.values.get(key);
		Calendar cal = Calendar.getInstance();
		if (value instanceof Long) {
			cal.setTimeInMillis((Long) this.values.get(key));
		} else {
			cal = (Calendar) this.values.get(key);
		}
		return cal;
	}

	public Double getDouble(final Integer key) {
		return (Double) this.values.get(key);
	}

	public Object getObject(final Integer key) {
		return this.values.get(key);
	}
}
