package sk.qbsw.sed.server.service.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.Region;

import sk.qbsw.sed.framework.report.export.stream.AXLSStreamReportExporter;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.model.CSummaryValues;
import sk.qbsw.sed.framework.report.model.hssf.CHSSFFile;

/**
 * 
 * @author rosenberg
 *
 */
public class CMonthAccountantXLSReportExporterImpl extends AXLSStreamReportExporter {

	private HSSFCellStyle cellStyleDate = null;
	private HSSFCellStyle cellStyleTime = null;
	private HSSFCellStyle cellStyleSumTime = null;
	private HSSFCellStyle cellStyleFloat = null;
	private HSSFCellStyle cellStyleInt = null;
	private HSSFCellStyle cellStyleString = null;
	private HSSFCellStyle cellStyleStringCentered = null;
	private HSSFCellStyle cellStyleBold = null;

	@Override
	protected void createInitialStyles(CHSSFFile file) {

		int alignLeft = 1;
		int alignCenter = 2;
		int alignRight = 3;

		this.cellStyleDate = file.getWorkbook().createCellStyle();
		this.cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		this.cellStyleDate.setAlignment((short) alignCenter);
		this.cellStyleDate.setBorderBottom((short) 1);
		this.cellStyleDate.setBorderLeft((short) 1);
		this.cellStyleDate.setBorderRight((short) 1);

		this.cellStyleTime = file.getWorkbook().createCellStyle();
		this.cellStyleTime.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm"));
		this.cellStyleTime.setAlignment((short) alignRight);
		this.cellStyleTime.setBorderBottom((short) 1);
		this.cellStyleTime.setBorderLeft((short) 1);
		this.cellStyleTime.setBorderRight((short) 1);

		this.cellStyleSumTime = file.getWorkbook().createCellStyle();
		this.cellStyleSumTime.setDataFormat(HSSFDataFormat.getBuiltinFormat("[hh]:mm"));
		this.cellStyleSumTime.setAlignment((short) alignRight);
		this.cellStyleSumTime.setBorderBottom((short) 1);
		this.cellStyleSumTime.setBorderLeft((short) 1);
		this.cellStyleSumTime.setBorderRight((short) 1);

		this.cellStyleFloat = file.getWorkbook().createCellStyle();
		this.cellStyleFloat.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));
		this.cellStyleFloat.setAlignment((short) alignCenter);
		this.cellStyleFloat.setBorderBottom((short) 1);
		this.cellStyleFloat.setBorderLeft((short) 1);
		this.cellStyleFloat.setBorderRight((short) 1);

		this.cellStyleInt = file.getWorkbook().createCellStyle();
		this.cellStyleInt.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		this.cellStyleInt.setAlignment((short) alignCenter);
		this.cellStyleInt.setBorderBottom((short) 1);
		this.cellStyleInt.setBorderLeft((short) 1);
		this.cellStyleInt.setBorderRight((short) 1);

		this.cellStyleString = file.getWorkbook().createCellStyle();
		this.cellStyleString.setAlignment((short) alignLeft);
		this.cellStyleString.setBorderLeft((short) 1);
		this.cellStyleString.setBorderRight((short) 1);
		this.cellStyleString.setBorderBottom((short) 1);

		this.cellStyleStringCentered = file.getWorkbook().createCellStyle();
		this.cellStyleStringCentered.setAlignment((short) alignCenter);
		this.cellStyleStringCentered.setBorderLeft((short) 1);
		this.cellStyleStringCentered.setBorderRight((short) 1);
		this.cellStyleStringCentered.setBorderBottom((short) 1);

		this.cellStyleBold = file.getWorkbook().createCellStyle();
		HSSFFont font = file.getWorkbook().createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		this.cellStyleBold.setFont(font);
		this.cellStyleBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	}

	@Override
	protected CReportOutputModel doExport(CHSSFFile file, final CComplexInputReportModel complexModel) throws IOException {
		int row = 0;

		final int headerColumnIdx = 1;
		// date
		HSSFRow writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(row);
		HSSFRichTextString nameCellValue = new HSSFRichTextString(complexModel.getDateFrom() + " - " + complexModel.getDateTo());
		writingRow.getCell(headerColumnIdx).setCellValue(nameCellValue);

		HSSFCell workDaysInMonthCell = writingRow.createCell((short) 5);
		workDaysInMonthCell.setCellStyle(this.cellStyleFloat);
		workDaysInMonthCell.setCellValue(complexModel.getWorkingDays());

		// jump to header target rows
		row = 3;

		// user rows rows
		Map<Long, CSummaryValues> usersSummaryValues = complexModel.getUserSummaryParameters();

		// convert to list
		List<CSummaryValues> summaryValues = new ArrayList<>(usersSummaryValues.values());

		// sort by name
		Collections.sort(summaryValues, new ComparatorByEmpTypeAndAlphabetical());

		String thisTypeOfEmploymentDescription = "";
		String previousTypeOfEmploymentDescription = "";
		// ak je false nepridá prázdny riadok, ak true pridá
		Boolean nextRow = false;

		for (CSummaryValues userSummaryValues : summaryValues) {
			thisTypeOfEmploymentDescription = userSummaryValues.getEmploymentTypeDescription();

			if (!thisTypeOfEmploymentDescription.equals(previousTypeOfEmploymentDescription)) {
				if (nextRow) {
					// pridať prázdny riadok
					HSSFRow emptyRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).createRow(row++);
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(emptyRow.getRowNum(), (short) 0, emptyRow.getRowNum(), (short) 14));

					// nastavím štýl všetkým bunkám v riadku aby bol riadok orámovaný
					for (int i = 1; i < 15; i++) {
						HSSFCell cellWithBorder = emptyRow.createCell((short) i);
						cellWithBorder.setCellStyle(this.cellStyleBold);
					}
				}

				final HSSFRow actualRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).createRow(row++);

				if (thisTypeOfEmploymentDescription.equals("Zamestnanec")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(actualRow.getRowNum(), (short) 0, actualRow.getRowNum(), (short) 14));
					addTypeOfEmpCell(actualRow, thisTypeOfEmploymentDescription);
				} else if (thisTypeOfEmploymentDescription.equals("Pracovník")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(actualRow.getRowNum(), (short) 0, actualRow.getRowNum(), (short) 14));
					addTypeOfEmpCell(actualRow, thisTypeOfEmploymentDescription);
				} else if (thisTypeOfEmploymentDescription.equals("Brigádnik")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(actualRow.getRowNum(), (short) 0, actualRow.getRowNum(), (short) 14));
					addTypeOfEmpCell(actualRow, thisTypeOfEmploymentDescription);
				} else if (thisTypeOfEmploymentDescription.equals("Neuvedený")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(actualRow.getRowNum(), (short) 0, actualRow.getRowNum(), (short) 14));
					addTypeOfEmpCell(actualRow, thisTypeOfEmploymentDescription);
				}
			}

			nextRow = true;
			BigDecimal doubleValue;
			Integer integerValue;

			int columnIdx = 0;
			HSSFRow newRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).createRow(row++);

			// user name
			HSSFCell nameCell = newRow.createCell((short) columnIdx++);
			nameCell.setCellStyle(this.cellStyleString);
			HSSFRichTextString nameValue = new HSSFRichTextString(userSummaryValues.getEmployeeName());
			nameCell.setCellValue(nameValue);

			// number work days - work days
			HSSFCell numUserWorkDaysOnlyCell = newRow.createCell((short) columnIdx++);
			numUserWorkDaysOnlyCell.setCellStyle(this.cellStyleFloat);

			doubleValue = new BigDecimal(userSummaryValues.getNumberEmployeeWorkDays_WorkDaysOnly());
			numUserWorkDaysOnlyCell.setCellValue(doubleValue.doubleValue());

			// number work hours - work days
			HSSFCell numUserWorkHourWorkDaysOnlyCell = newRow.createCell((short) columnIdx++);
			numUserWorkHourWorkDaysOnlyCell.setCellStyle(this.cellStyleSumTime);
			HSSFRichTextString numWorkHourWorkDaysOnlyValue = new HSSFRichTextString(userSummaryValues.getSumOfWorkHours_WorkDays());
			numUserWorkHourWorkDaysOnlyCell.setCellValue(numWorkHourWorkDaysOnlyValue);

			// number free days - in work
			HSSFCell numUserHolidayDaysOnlyCell = newRow.createCell((short) columnIdx++);
			numUserHolidayDaysOnlyCell.setCellStyle(this.cellStyleFloat);
			doubleValue = new BigDecimal(userSummaryValues.getNumberEmployeeWorkDays_HolidaysOnly());
			numUserHolidayDaysOnlyCell.setCellValue(doubleValue.doubleValue());

			// number hours from free days - in work
			HSSFCell numUserWorkHourHolidaysOnlyCell = newRow.createCell((short) columnIdx++);
			numUserWorkHourHolidaysOnlyCell.setCellStyle(this.cellStyleSumTime);
			HSSFRichTextString numWorkHourHolidaysOnlyValue = new HSSFRichTextString(userSummaryValues.getSumOfWorkHours_Holidays());
			numUserWorkHourHolidaysOnlyCell.setCellValue(numWorkHourHolidaysOnlyValue);

			// month: number holidays
			HSSFCell numHolidayDaysCell = newRow.createCell((short) columnIdx++);
			numHolidayDaysCell.setCellStyle(this.cellStyleStringCentered);
			HSSFRichTextString numHolidayDaysValue = new HSSFRichTextString(userSummaryValues.getHolidays());
			numHolidayDaysCell.setCellValue(numHolidayDaysValue);

			// user: number holiday
			HSSFCell numUserHolidayDaysCell = newRow.createCell((short) columnIdx++);
			numUserHolidayDaysCell.setCellStyle(this.cellStyleFloat);
			doubleValue = new BigDecimal(userSummaryValues.getNumberEmployeeDDays());
			numUserHolidayDaysCell.setCellValue(doubleValue.doubleValue());

			// user: PN
			HSSFCell numUserPNDaysCell = newRow.createCell((short) columnIdx++);
			numUserPNDaysCell.setCellStyle(this.cellStyleInt);
			integerValue = new Integer(userSummaryValues.getNumberEmployeePNDays());
			numUserPNDaysCell.setCellValue(integerValue.longValue());

			// user: NV
			HSSFCell numUserNVDaysCell = newRow.createCell((short) columnIdx++);
			numUserNVDaysCell.setCellStyle(this.cellStyleInt);
			integerValue = new Integer(userSummaryValues.getNumberEmployeeNVDays());
			numUserNVDaysCell.setCellValue(integerValue.longValue());

			// user: PvP Physician visit/days
			HSSFCell numUserPhysicianVisitDaysCell = newRow.createCell((short) columnIdx++);
			numUserPhysicianVisitDaysCell.setCellStyle(this.cellStyleInt);
			integerValue = new Integer(userSummaryValues.getNumberEmployeePvP_PhysicianVist_Days());
			numUserPhysicianVisitDaysCell.setCellValue(integerValue.longValue());

			// user: PvP Physician visit/hours
			HSSFCell numUserPhysicianVisitHoursCell = newRow.createCell((short) columnIdx++);
			numUserPhysicianVisitHoursCell.setCellStyle(this.cellStyleSumTime);
			HSSFRichTextString numPargraphPhysicianHoursValue = new HSSFRichTextString(userSummaryValues.getWorkbreakPhysicianVisitDuration());
			numUserPhysicianVisitHoursCell.setCellValue(numPargraphPhysicianHoursValue);

			// user: PvP - 60%
			HSSFCell numUserPvPDaysCell = newRow.createCell((short) columnIdx++);
			numUserPvPDaysCell.setCellStyle(this.cellStyleInt);
			integerValue = new Integer(userSummaryValues.getNumberEmployeePvP_60Percet_Days());
			numUserPvPDaysCell.setCellValue(integerValue.longValue());

			// user: PvP other (PvP, but not physician visit reason or 60
			// %)/days
			HSSFCell numUserPvPOtherDaysCell = newRow.createCell((short) columnIdx++);
			numUserPvPOtherDaysCell.setCellStyle(this.cellStyleInt);
			integerValue = new Integer(userSummaryValues.getNumberEmployeePvP_Other_Days());
			numUserPvPOtherDaysCell.setCellValue(integerValue.longValue());

			// user: PvP other (PvP, but not physician visit reason or 60 %)/
			// hours
			HSSFCell numParagraphOtherHoursCell = newRow.createCell((short) columnIdx++);
			numParagraphOtherHoursCell.setCellStyle(this.cellStyleSumTime);
			HSSFRichTextString numPargraphOtherHoursValue = new HSSFRichTextString(userSummaryValues.getWorkbreakOtherDuration());
			numParagraphOtherHoursCell.setCellValue(numPargraphOtherHoursValue);

			HSSFCell overtimeHoursCell = newRow.createCell((short) columnIdx++);
			overtimeHoursCell.setCellStyle(this.cellStyleSumTime);
			String strFormulaOvertimeHours = "CONCATENATE(IF((LEFT(C" + row + ",FIND(\":\",C" + row + ")-1)*60+RIGHT(C" + row + ",2))>=(($F$1-G" + row + "-H" + row + "-I" + row + "-L" + row
					+ ")*8*60-(LEFT(K" + row + ",FIND(\":\",K" + row + ")-1)*60+RIGHT(K" + row + ",2))-(LEFT(N" + row + ",FIND(\":\",N" + row + ")-1)*60+RIGHT(N" + row
					+ ",2))),\"\",\"-\"),TRUNC(ABS(((LEFT(C" + row + ",FIND(\":\",C" + row + ")-1)*60+RIGHT(C" + row + ",2))-(($F$1-G" + row + "-H" + row + "-I" + row + "-L" + row + ")*8*60-(LEFT(K"
					+ row + ",FIND(\":\",K" + row + ")-1)*60+RIGHT(K" + row + ",2))-(LEFT(N" + row + ",FIND(\":\",N" + row + ")-1)*60+RIGHT(N" + row + ",2)))))/60,0),\":\",TEXT(MOD(ABS(((LEFT(C" + row
					+ ",FIND(\":\",C" + row + ")-1)*60+RIGHT(C" + row + ",2))-(($F$1-G" + row + "-H" + row + "-I" + row + "-L" + row + ")*8*60-(LEFT(K" + row + ",FIND(\":\",K" + row
					+ ")-1)*60+RIGHT(K" + row + ",2))-(LEFT(N" + row + ",FIND(\":\",N" + row + ")-1)*60+RIGHT(N" + row + ",2))))),60),\"00\"))";
			overtimeHoursCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
			overtimeHoursCell.setCellFormula(strFormulaOvertimeHours);

			previousTypeOfEmploymentDescription = thisTypeOfEmploymentDescription;
		}

		super.finalizeExportProcess(false);

		return new CReportOutputModel();
	}

	private class ComparatorByEmpTypeAndAlphabetical implements Comparator<CSummaryValues> {

		private final Locale locale = new Locale("sk");
		private Collator collator = Collator.getInstance(locale);

		@Override
		public int compare(CSummaryValues o1, CSummaryValues o2) {

			// najskôr zoradím podľa id typov PP
			int empTypeId = collator.compare(String.valueOf(o1.getEmploymentTypeId()), String.valueOf(o2.getEmploymentTypeId()));

			if (empTypeId != 0) {
				return empTypeId;
			}

			int surname = collator.compare(o1.getEmployeeSurname(), o2.getEmployeeSurname());

			if (surname != 0) {
				return surname;
			}

			return collator.compare(o1.getEmployeeFirstName(), o2.getEmployeeFirstName());
		}
	}

	private void addTypeOfEmpCell(HSSFRow writingRow, String thisTypeOfEmploymentDescription) {
		final HSSFCell cell = writingRow.createCell((short) 0);

		cell.setCellStyle(this.cellStyleBold);
		cell.setCellValue(new HSSFRichTextString(thisTypeOfEmploymentDescription));

		// nastavím štýl všetkým bunkám v riadku aby bol riadok orámovaný
		for (int i = 1; i < 15; i++) {
			HSSFCell cellWithBorder = writingRow.createCell((short) i);
			cellWithBorder.setCellStyle(this.cellStyleBold);
		}
	}
}
