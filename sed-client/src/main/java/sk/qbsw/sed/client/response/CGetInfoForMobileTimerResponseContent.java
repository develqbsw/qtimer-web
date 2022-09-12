package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CGetInfoForMobileTimerResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Long sumOfWorkHoursPerMonth;
	Long sumOfWorkHoursPerWeek;
	Long sumOfWorkHoursPerDay;

	Long averageDurationPerMonth;
	Long averageDurationPerWeek;

	Integer workingDaysInActualWeek;
	Integer workingDaysInActualMonth;

	public Long getSumOfWorkHoursPerMonth() {
		return sumOfWorkHoursPerMonth;
	}

	public void setSumOfWorkHoursPerMonth(Long sumOfWorkHoursPerMonth) {
		this.sumOfWorkHoursPerMonth = sumOfWorkHoursPerMonth;
	}

	public Long getSumOfWorkHoursPerWeek() {
		return sumOfWorkHoursPerWeek;
	}

	public void setSumOfWorkHoursPerWeek(Long sumOfWorkHoursPerWeek) {
		this.sumOfWorkHoursPerWeek = sumOfWorkHoursPerWeek;
	}

	public Long getSumOfWorkHoursPerDay() {
		return sumOfWorkHoursPerDay;
	}

	public void setSumOfWorkHoursPerDay(Long sumOfWorkHoursPerDay) {
		this.sumOfWorkHoursPerDay = sumOfWorkHoursPerDay;
	}

	public Long getAverageDurationPerMonth() {
		return averageDurationPerMonth;
	}

	public void setAverageDurationPerMonth(Long averageDurationPerMonth) {
		this.averageDurationPerMonth = averageDurationPerMonth;
	}

	public Long getAverageDurationPerWeek() {
		return averageDurationPerWeek;
	}

	public void setAverageDurationPerWeek(Long averageDurationPerWeek) {
		this.averageDurationPerWeek = averageDurationPerWeek;
	}

	public Integer getWorkingDaysInActualWeek() {
		return workingDaysInActualWeek;
	}

	public void setWorkingDaysInActualWeek(Integer workingDaysInActualWeek) {
		this.workingDaysInActualWeek = workingDaysInActualWeek;
	}

	public Integer getWorkingDaysInActualMonth() {
		return workingDaysInActualMonth;
	}

	public void setWorkingDaysInActualMonth(Integer workingDaysInActualMonth) {
		this.workingDaysInActualMonth = workingDaysInActualMonth;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
