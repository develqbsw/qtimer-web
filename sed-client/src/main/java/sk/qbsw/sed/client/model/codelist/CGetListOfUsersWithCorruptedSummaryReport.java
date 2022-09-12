package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.common.utils.CRange;

@SuppressWarnings("serial")
public class CGetListOfUsersWithCorruptedSummaryReport implements Serializable {

	private Long userId;
	private Date from;
	private Date to;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public String getDateInput() {
		if (from != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			return sdf.format(this.from) + " - " + sdf.format(this.to);
		}

		return "";
	}

	public void setDateInput(final String dateSting) throws ParseException {
		CRange range = CDateUtils.parseRange(dateSting);
		from = range.getFromDate();
		to = range.getToDate();
	}
}
