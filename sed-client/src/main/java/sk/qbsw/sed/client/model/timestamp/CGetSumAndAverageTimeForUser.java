package sk.qbsw.sed.client.model.timestamp;

public class CGetSumAndAverageTimeForUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	String surname;
	Long sumOfWorkHours;
	Long countOfDays;

	public Long getCountOfDays() {
		return countOfDays;
	}

	public void setCountOfDays(Long countOfDays) {
		this.countOfDays = countOfDays;
	}

	public CGetSumAndAverageTimeForUser(String name, String surname, Long sum, Long count) {
		this.name = name;
		this.surname = surname;
		this.sumOfWorkHours = sum;
		this.countOfDays = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Long getSumOfWorkHours() {
		return sumOfWorkHours;
	}

	public void setSumOfWorkHours(Long sumOfWorkHours) {
		this.sumOfWorkHours = sumOfWorkHours;
	}
}
