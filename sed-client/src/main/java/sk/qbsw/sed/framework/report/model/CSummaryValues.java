package sk.qbsw.sed.framework.report.model;

import java.io.Serializable;

/**
 * output object for summary month employee report and accountant month report
 * 
 * @author rosenberg
 * 
 */
public class CSummaryValues implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 697742403481686902L;

	/**
	 * employee name
	 */
	String employeeName = "";

	/**
	 * id of employment type name
	 */
	Long employmentTypeId;

	/**
	 * description of employment type name
	 */
	String employmentTypeDescription = "";

	/**
	 * sum of employee alertness work hours in the month
	 */
	String sumOfAlertnessWorkHours = "";

	/**
	 * sum of employee interactive work hours in the month
	 */
	String sumOfInteractiveWorkHours = "";

	/**
	 * sum of employee working hours in the month - for employee month report only
	 */
	String sumOfWorkHours = "";

	/**
	 * sum of employee working hours in work days in the month - for accountant
	 * month report only
	 */
	String sumOfWorkHours_WorkDays = "";

	/**
	 * sum of employee working hours in holidays in the month - for accountant month
	 * report only
	 */
	String sumOfWorkHours_Holidays = "";

	/**
	 * average of employee working hours in the month for the day
	 * 
	 */
	String averageHoursForDay = "";

	/**
	 * number of employee work days
	 * 
	 * - for employee month report only
	 * 
	 * - equals to numberWorkedEmployeeWorkDaysOnly
	 */
	String numberEmployeeWorkDays = "";

	/**
	 * number of employee work days when worked
	 * 
	 * - for accountant month report only
	 * 
	 * - equals to numberWorkedEmployeeWorkDaysOnly
	 */
	String numberEmployeeWorkDays_WorkDaysOnly = "";

	/**
	 * number of employee holidays when worked
	 * 
	 * - for accountant month report only
	 */
	String numberEmployeeWorkDays_HolidaysOnly = "";

	/**
	 * Počet D za mesiac: počet dovoleniek, ktoré zamestnanec čerpal počas mesiaca
	 * (dovolenka padla na pracovný deň) Pozn. V prípade pol-dňa dovolenky sa do
	 * počtu započítava hodnota 0,5.
	 */
	String numberEmployeeDDays = "";

	/**
	 * Počet NV za mesiac: počet dní náhradného voľna, ktoré zamestnanec čerpal
	 * počas mesiaca (NV padol na pracovný deň)
	 */
	String numberEmployeeNVDays = "";

	/**
	 * Počet PN za mesiac: počet PN dní, ktoré zamestnanec čerpal počas mesiaca (PN
	 * padol na pracovný deň)
	 */
	String numberEmployeePNDays = "";

	/**
	 * Počet PvP za mesiac: počet dní strávených na prekážkach v práci, ktoré
	 * zamestnanec čerpal počas mesiaca (PvP padol na pracovný deň)
	 */
	String numberEmployeePvPDays = "";

	/**
	 * Počet PvP - Navsteva lekara, za mesiac: počet dní (PvP padol na pracovný deň)
	 */
	String numberEmployeePvP_PhysicianVist_Days = "";

	/**
	 * Počet PvP - 60%, za mesiac: počet dní (PvP padol na pracovný deň)
	 */
	String numberEmployeePvP_60Percet_Days = "";

	/**
	 * Počet PvP - Ine, za mesiac: počet dní (PvP padol na pracovný deň)
	 */
	String numberEmployeePvP_Other_Days = "";

	/**
	 * Duration for different user paragraph activities
	 * 
	 * - for accountat report only
	 */
	String workbreakPhysicianVisitDuration = "";

	/**
	 * Duration for different user paragraph activities
	 * 
	 * - for accountat report only
	 */
	String workbreakOtherDuration = "";

	/**
	 * Number of holidays in selected date interval
	 */
	String holidays;

	String employeeFirstName = "";

	String employeeSurname = "";

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmploymentTypeDescription() {
		return employmentTypeDescription;
	}

	public void setEmploymentTypeDescription(String employmentTypeDescription) {
		this.employmentTypeDescription = employmentTypeDescription;
	}

	public Long getEmploymentTypeId() {
		return employmentTypeId;
	}

	public void setEmploymentTypeId(Long employmentTypeId) {
		this.employmentTypeId = employmentTypeId;
	}

	public String getSumOfWorkHours() {
		return sumOfWorkHours;
	}

	public void setSumOfWorkHours(String sumOfWorkHours) {
		this.sumOfWorkHours = sumOfWorkHours;
	}

	public String getAverageHoursForDay() {
		return averageHoursForDay;
	}

	public void setAverageHoursForDay(String averageHoursForDay) {
		this.averageHoursForDay = averageHoursForDay;
	}

	/**
	 * number of employee work days
	 */
	public String getNumberEmployeeWorkDays() {
		return numberEmployeeWorkDays;
	}

	public void setNumberEmployeeWorkDays(String numberEmployeeWorkDays) {
		this.numberEmployeeWorkDays = numberEmployeeWorkDays;
	}

	public String getNumberEmployeeDDays() {
		return numberEmployeeDDays;
	}

	public void setNumberEmployeeDDays(String numberEmployeeDDays) {
		this.numberEmployeeDDays = numberEmployeeDDays;
	}

	public String getNumberEmployeeNVDays() {
		return numberEmployeeNVDays;
	}

	public void setNumberEmployeeNVDays(String numberEmployeeNVDays) {
		this.numberEmployeeNVDays = numberEmployeeNVDays;
	}

	public String getNumberEmployeePNDays() {
		return numberEmployeePNDays;
	}

	public void setNumberEmployeePNDays(String numberEmployeePNDays) {
		this.numberEmployeePNDays = numberEmployeePNDays;
	}

	public String getNumberEmployeePvPDays() {
		return numberEmployeePvPDays;
	}

	public void setNumberEmployeePvPDays(String numberEmployeePvPDays) {
		this.numberEmployeePvPDays = numberEmployeePvPDays;
	}

	public String getSumOfAlertnessWorkHours() {
		return sumOfAlertnessWorkHours;
	}

	public void setSumOfAlertnessWorkHours(String sumOfAlertnessWorkHours) {
		this.sumOfAlertnessWorkHours = sumOfAlertnessWorkHours;
	}

	public String getSumOfInteractiveWorkHours() {
		return sumOfInteractiveWorkHours;
	}

	public void setSumOfInteractiveWorkHours(String sumOfInteractiveWorkHours) {
		this.sumOfInteractiveWorkHours = sumOfInteractiveWorkHours;
	}

	public String getHolidays() {
		return holidays;
	}

	public void setHolidays(String holidays) {
		this.holidays = holidays;
	}

	public String getSumOfWorkHours_WorkDays() {
		return sumOfWorkHours_WorkDays;
	}

	public void setSumOfWorkHours_WorkDays(String sumOfWorkHoursWorkDays) {
		sumOfWorkHours_WorkDays = sumOfWorkHoursWorkDays;
	}

	public String getSumOfWorkHours_Holidays() {
		return sumOfWorkHours_Holidays;
	}

	public void setSumOfWorkHours_Holidays(String sumOfWorkHoursHolidays) {
		sumOfWorkHours_Holidays = sumOfWorkHoursHolidays;
	}

	public String getNumberEmployeeWorkDays_WorkDaysOnly() {
		return numberEmployeeWorkDays_WorkDaysOnly;
	}

	public void setNumberEmployeeWorkDays_WorkDaysOnly(String numberEmployeeWorkDays_WorkDaysOnly) {
		this.numberEmployeeWorkDays_WorkDaysOnly = numberEmployeeWorkDays_WorkDaysOnly;
	}

	public String getNumberEmployeeWorkDays_HolidaysOnly() {
		return numberEmployeeWorkDays_HolidaysOnly;
	}

	public void setNumberEmployeeWorkDays_HolidaysOnly(String numberEmployeeWorkDays_HolidaysOnly) {
		this.numberEmployeeWorkDays_HolidaysOnly = numberEmployeeWorkDays_HolidaysOnly;
	}

	public String getWorkbreakPhysicianVisitDuration() {
		return workbreakPhysicianVisitDuration;
	}

	public void setWorkbreakPhysicianVisitDuration(String workbreakPhysicianVisitDuration) {
		this.workbreakPhysicianVisitDuration = workbreakPhysicianVisitDuration;
	}

	public String getWorkbreakOtherDuration() {
		return workbreakOtherDuration;
	}

	public void setWorkbreakOtherDuration(String workbreakOtherDuration) {
		this.workbreakOtherDuration = workbreakOtherDuration;
	}

	public String getNumberEmployeePvP_PhysicianVist_Days() {
		return numberEmployeePvP_PhysicianVist_Days;
	}

	public void setNumberEmployeePvP_PhysicianVist_Days(String numberEmployeePvP_PhysicianVist_Days) {
		this.numberEmployeePvP_PhysicianVist_Days = numberEmployeePvP_PhysicianVist_Days;
	}

	public String getNumberEmployeePvP_60Percet_Days() {
		return numberEmployeePvP_60Percet_Days;
	}

	public void setNumberEmployeePvP_60Percet_Days(String numberEmployeePvP_60Percet_Days) {
		this.numberEmployeePvP_60Percet_Days = numberEmployeePvP_60Percet_Days;
	}

	public String getNumberEmployeePvP_Other_Days() {
		return numberEmployeePvP_Other_Days;
	}

	public void setNumberEmployeePvP_Other_Days(String numberEmployeePvP_Other_Days) {
		this.numberEmployeePvP_Other_Days = numberEmployeePvP_Other_Days;
	}

	public String getEmployeeFirstName() {
		return employeeFirstName;
	}

	public void setEmployeeFirstName(String employeeFirstName) {
		this.employeeFirstName = employeeFirstName;
	}

	public String getEmployeeSurname() {
		return employeeSurname;
	}

	public void setEmployeeSurname(String employeeSurname) {
		this.employeeSurname = employeeSurname;
	}
}
