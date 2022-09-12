package sk.qbsw.sed.client.model.codelist;

/**
 * 
 * @author rosenberg
 *
 */
@SuppressWarnings("serial")
public class CHolidayBrwFilterCriteria implements IHolidayBrwFilterCriteria {
	private Long year;

	public Long getYear() {
		return this.year;
	}

	public void setYear(Long year) {
		this.year = year;
	}
}
