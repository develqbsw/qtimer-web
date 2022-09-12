package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CGetSumAndAverageTimeResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Long sumOfWorkHours;
	Long averageDuration;

	public Long getSumOfWorkHours() {
		return sumOfWorkHours;
	}

	public void setSumOfWorkHours(Long sumOfWorkHours) {
		this.sumOfWorkHours = sumOfWorkHours;
	}

	public Long getAverageDuration() {
		return averageDuration;
	}

	public void setAverageDuration(Long averageDuration) {
		this.averageDuration = averageDuration;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
