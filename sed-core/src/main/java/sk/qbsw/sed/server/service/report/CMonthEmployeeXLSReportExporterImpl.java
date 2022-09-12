package sk.qbsw.sed.server.service.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;

import sk.qbsw.sed.client.exception.CParseException;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.report.export.stream.AXLSStreamReportExporter;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.model.CSummaryValues;
import sk.qbsw.sed.framework.report.model.hssf.CHSSFFile;

/**
 * 
 * @author rosenberg
 * 
 */
public class CMonthEmployeeXLSReportExporterImpl extends AXLSStreamReportExporter {

	private HSSFCellStyle cellStyleDate = null;
	private HSSFCellStyle cellStyleTime = null;
	private HSSFCellStyle cellStyleLikeTime = null;
	private HSSFCellStyle cellStyleSumTime = null;
	private HSSFCellStyle cellStyleFloat = null;
	private HSSFCellStyle cellStyleInt = null;

	@Override
	protected void createInitialStyles(CHSSFFile file) {
		this.cellStyleDate = file.getWorkbook().createCellStyle();
		this.cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		this.cellStyleDate.setBorderBottom((short) 1);

		this.cellStyleTime = file.getWorkbook().createCellStyle();
		this.cellStyleTime.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm"));
		this.cellStyleTime.setBorderBottom((short) 1);
		this.cellStyleTime.setBorderLeft((short) 2);
		this.cellStyleTime.setBorderRight((short) 2);

		this.cellStyleLikeTime = file.getWorkbook().createCellStyle();
		this.cellStyleLikeTime.setBorderBottom((short) 1);
		this.cellStyleLikeTime.setBorderLeft((short) 2);
		this.cellStyleLikeTime.setBorderRight((short) 2);
		this.cellStyleLikeTime.setAlignment((short) 3);

		this.cellStyleSumTime = file.getWorkbook().createCellStyle();
		this.cellStyleSumTime.setDataFormat(HSSFDataFormat.getBuiltinFormat("[hh]:mm"));
		this.cellStyleSumTime.setBorderBottom((short) 2);
		this.cellStyleSumTime.setBorderLeft((short) 2);
		this.cellStyleSumTime.setBorderRight((short) 2);

		this.cellStyleFloat = file.getWorkbook().createCellStyle();
		this.cellStyleFloat.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		this.cellStyleFloat.setAlignment((short) 3);
		this.cellStyleFloat.setBorderBottom((short) 2);
		this.cellStyleFloat.setBorderLeft((short) 2);
		this.cellStyleFloat.setBorderRight((short) 2);

		this.cellStyleInt = file.getWorkbook().createCellStyle();
		this.cellStyleInt.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		this.cellStyleInt.setAlignment((short) 3);
		this.cellStyleInt.setBorderBottom((short) 2);
		this.cellStyleInt.setBorderLeft((short) 2);
		this.cellStyleInt.setBorderRight((short) 2);

	}

	@Override
	protected CReportOutputModel doExport(CHSSFFile file, final CComplexInputReportModel complexModel) throws IOException, CParseException {
		int numberOfProcessingRow = 0;

		final int headerColumnIdx = 2;
		// employee
		HSSFRow writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString nameCellValue = new HSSFRichTextString(complexModel.getReportName());
		writingRow.getCell(headerColumnIdx).setCellValue(nameCellValue);

		// month
		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString monthCellValue = new HSSFRichTextString(complexModel.getMonth());
		writingRow.getCell(headerColumnIdx).setCellValue(monthCellValue);

		// year
		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString yearCellValue = new HSSFRichTextString(complexModel.getYear());
		writingRow.getCell(headerColumnIdx).setCellValue(yearCellValue);

		// skip header for daily rows
		numberOfProcessingRow++;

		// daily rows
		List<CReportModel> reportRows = complexModel.getReportRows();
		for (CReportModel row : reportRows) {
			int columnIdx = 0;
			writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
			if (writingRow != null) {

				// [date:(DD.MM.YYYY)]
				Calendar dateCellValue = row.getCalendar(1);
				writingRow.getCell(columnIdx).setCellStyle(this.cellStyleDate);
				writingRow.getCell(columnIdx).setCellValue(dateCellValue);

				// [day short name]
				HSSFRichTextString dayCellValue = new HSSFRichTextString(row.getString(4));
				writingRow.getCell(columnIdx + 1).setCellValue(dayCellValue);

				// [notes]
				HSSFRichTextString noteCellValue = new HSSFRichTextString(row.getString(2));
				writingRow.getCell(columnIdx + 2).setCellValue(noteCellValue);

				// [duration(h:mm)|request code]
				HSSFRichTextString hourCellValue = new HSSFRichTextString(row.getString(3));
				if (hourCellValue.getString() != null && hourCellValue.getString().indexOf(':') > -1) {
					String[] parts = hourCellValue.getString().split(":");
					int value = Integer.parseInt(parts[0]);
					if (value >= 24) {
						HSSFRichTextString cellValue = new HSSFRichTextString(hourCellValue.getString());
						writingRow.getCell(columnIdx + 3).setCellStyle(this.cellStyleLikeTime);
						writingRow.getCell(columnIdx + 3).setCellValue(cellValue);
					} else {
						writingRow.getCell(columnIdx + 3).setCellStyle(this.cellStyleTime);
						writingRow.getCell(columnIdx + 3).setCellValue(CDateUtils.convertTime(hourCellValue.getString()));
					}
				} else {
					writingRow.getCell(columnIdx + 3).setCellValue(hourCellValue);
				}

				// [alertness work(h:mm)]
				HSSFRichTextString hourAlertnessWorkCellValue = new HSSFRichTextString(row.getString(5));
				if (hourAlertnessWorkCellValue.getString() != null && hourAlertnessWorkCellValue.getString().indexOf(':') > -1) {
					String[] parts = hourAlertnessWorkCellValue.getString().split(":");
					int value = Integer.parseInt(parts[0]);
					if (value >= 24) {
						HSSFRichTextString cellValue = new HSSFRichTextString(hourAlertnessWorkCellValue.getString());
						writingRow.getCell(columnIdx + 4).setCellStyle(this.cellStyleLikeTime);
						writingRow.getCell(columnIdx + 4).setCellValue(cellValue);
					} else {
						writingRow.getCell(columnIdx + 4).setCellStyle(this.cellStyleTime);
						writingRow.getCell(columnIdx + 4).setCellValue(CDateUtils.convertTime(hourAlertnessWorkCellValue.getString()));
					}
				}

				// [interactive work(h:mm)]
				HSSFRichTextString hourInteractiveWorkCellValue = new HSSFRichTextString(row.getString(6));
				if (hourInteractiveWorkCellValue.getString() != null && hourInteractiveWorkCellValue.getString().indexOf(':') > -1) {
					String[] parts = hourInteractiveWorkCellValue.getString().split(":");
					int value = Integer.parseInt(parts[0]);
					if (value >= 24) {
						HSSFRichTextString cellValue = new HSSFRichTextString(hourInteractiveWorkCellValue.getString());
						writingRow.getCell(columnIdx + 5).setCellStyle(this.cellStyleLikeTime);
						writingRow.getCell(columnIdx + 5).setCellValue(cellValue);
					} else {
						writingRow.getCell(columnIdx + 5).setCellStyle(this.cellStyleTime);
						writingRow.getCell(columnIdx + 5).setCellValue(CDateUtils.convertTime(hourInteractiveWorkCellValue.getString()));
					}
				}
			}
		}

		Set<Long> keys = complexModel.getUserSummaryParameters().keySet();
		Object[] lKeys = keys.toArray();
		// take first
		CSummaryValues userSummaryValues = complexModel.getUserSummaryParameters().get((Long) lKeys[0]);

		// summary part - jump to target row
		numberOfProcessingRow = 35;

		final int summaryWorkColumn = 3;
		final int summaryAlertnessWorkColumn = summaryWorkColumn + 1;
		final int summaryInteractiveWorkColumn = summaryAlertnessWorkColumn + 1;

		// sum of alertness work
		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow);
		HSSFRichTextString sumAlertnessWorkCellValue = new HSSFRichTextString(userSummaryValues.getSumOfAlertnessWorkHours());
		writingRow.getCell(summaryAlertnessWorkColumn).setCellValue(sumAlertnessWorkCellValue);

		// sum of interactive work
		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow);
		HSSFRichTextString sumIneractiveWorkCellValue = new HSSFRichTextString(userSummaryValues.getSumOfInteractiveWorkHours());
		writingRow.getCell(summaryInteractiveWorkColumn).setCellValue(sumIneractiveWorkCellValue);

		// sum of work hours
		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString sumHoursCellValue = new HSSFRichTextString(userSummaryValues.getSumOfWorkHours());
		writingRow.getCell(summaryWorkColumn).setCellValue(sumHoursCellValue);

		// daily average
		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString averageCellValue = new HSSFRichTextString(userSummaryValues.getAverageHoursForDay());
		writingRow.getCell(summaryWorkColumn).setCellValue(averageCellValue);

		// number of employee work days
		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString numDaysCellValue = new HSSFRichTextString(userSummaryValues.getNumberEmployeeWorkDays());
		writingRow.getCell(summaryWorkColumn).setCellStyle(this.cellStyleFloat);
		if (!"".equals(numDaysCellValue.getString())) {
			BigDecimal wd = new BigDecimal(numDaysCellValue.getString());
			writingRow.getCell(summaryWorkColumn).setCellValue(wd.doubleValue());
		}

		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString DdaysCellValue = new HSSFRichTextString(userSummaryValues.getNumberEmployeeDDays());
		writingRow.getCell(summaryWorkColumn).setCellStyle(this.cellStyleFloat);
		if (!"".equals(DdaysCellValue.getString())) {
			BigDecimal d = new BigDecimal(DdaysCellValue.getString());
			writingRow.getCell(summaryWorkColumn).setCellValue(d.doubleValue());
		}

		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString NVdaysCellValue = new HSSFRichTextString(userSummaryValues.getNumberEmployeeNVDays());
		writingRow.getCell(summaryWorkColumn).setCellStyle(this.cellStyleInt);
		if (!"".equals(NVdaysCellValue.getString())) {
			int nv = Integer.parseInt(NVdaysCellValue.getString());
			writingRow.getCell(summaryWorkColumn).setCellValue(nv);
		}

		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString PNdaysCellValue = new HSSFRichTextString(userSummaryValues.getNumberEmployeePNDays());
		writingRow.getCell(summaryWorkColumn).setCellStyle(this.cellStyleInt);
		if (!"".equals(PNdaysCellValue.getString())) {
			int pn = Integer.parseInt(PNdaysCellValue.getString());
			writingRow.getCell(summaryWorkColumn).setCellValue(pn);
		}

		writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).getRow(numberOfProcessingRow++);
		HSSFRichTextString PvPdaysCellValue = new HSSFRichTextString(userSummaryValues.getNumberEmployeePvPDays());
		writingRow.getCell(summaryWorkColumn).setCellStyle(this.cellStyleInt);
		if (!"".equals(PvPdaysCellValue.getString())) {
			int pvp = Integer.parseInt(PvPdaysCellValue.getString());
			writingRow.getCell(summaryWorkColumn).setCellValue(pvp);
		}

		super.finalizeExportProcess(false);

		return new CReportOutputModel();
	}
}
