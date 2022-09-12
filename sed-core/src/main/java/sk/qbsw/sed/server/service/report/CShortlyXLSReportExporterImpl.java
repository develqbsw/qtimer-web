package sk.qbsw.sed.server.service.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor;

import sk.qbsw.sed.framework.report.export.stream.AXLSStreamReportExporter;
import sk.qbsw.sed.framework.report.model.CCellTypeEnum;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.model.hssf.CHSSFFile;
import sk.qbsw.sed.framework.report.util.CHSSFUtils;
import sk.qbsw.sed.server.model.report.useful.CProjectData;
import sk.qbsw.sed.server.model.report.useful.CUserData;
import sk.qbsw.sed.server.service.CTimeUtils;

public class CShortlyXLSReportExporterImpl extends AXLSStreamReportExporter {

	private HSSFCellStyle cellStyleDate = null;
	private HSSFCellStyle cellStyleTime = null;

	@Override
	protected CReportOutputModel doExport(final CHSSFFile file, final CComplexInputReportModel complexModel) throws IOException {

		this.fillCodebooks(file, complexModel);

		int numberOfProcessingRow = 0;
		final int cellCount = super.getCellCount();

		BigDecimal sumTime = BigDecimal.ZERO;
		for (final CReportModel record : complexModel.getReportRows()) {
			sumTime = sumTime.add(new BigDecimal((Integer) record.getObject(6)));
		}

		for (final CReportModel row : complexModel.getReportRows()) {
			final HSSFRow writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).createRow(++numberOfProcessingRow);
			for (int index = 0; index < cellCount; index++) {
				final HSSFCell cell = writingRow.createCell((short) index);
				final CCellTypeEnum cellType = super.getCellType(index);
				switch (cellType) {
				case TYPE_STRING:
					cell.setCellValue(new HSSFRichTextString((String) this.getValueFromModel(row, index)));
					break;
				case TYPE_DATE:
					cell.setCellStyle(this.cellStyleDate);
					cell.setCellValue((Calendar) this.getCalendarValueFromModel(row, index));
					break;
				case TYPE_PERCENT:
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(((Double) this.getValueFromModel(row, index)).doubleValue());
					break;
				default:
					throw new IllegalArgumentException("Unknown cell type: " + super.getCellType(index));
				}
			}
			if (numberOfProcessingRow == 1) {
				HSSFCell cell = writingRow.createCell((short) 5);
				cell.setCellStyle(this.cellStyleDate);
				final Calendar cal = this.getCalendarValueFromModel(row, 5);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				cell.setCellValue(cal);
				CHSSFUtils.setFillBackgroundColor(cell.getCellStyle(), new HSSFColor.LIGHT_GREEN());

				// sumtime je v min
				final BigDecimal timeInHours = CTimeUtils.convertToHours(sumTime);
				cell = writingRow.createCell((short) 6);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(timeInHours.doubleValue() >= 1 ? timeInHours.doubleValue() : 1);
				CHSSFUtils.setFillBackgroundColor(file.getWorkbook(), cell, new HSSFColor.YELLOW());
			}
		}

		super.finalizeExportProcess(true);

		return new CReportOutputModel();
	}

	@Override
	protected void createInitialStyles(final CHSSFFile file) {
		this.cellStyleDate = file.getWorkbook().createCellStyle();
		this.cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		this.cellStyleTime = file.getWorkbook().createCellStyle();
		this.cellStyleTime.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm"));
	}

	private Object getValueFromModel(final CReportModel model, final int index) {
		return model.getObject(index);
	}

	private Calendar getCalendarValueFromModel(final CReportModel model, final int index) {
		return model.getCalendar(index);
	}

	private void fillCodebooks(final CHSSFFile file, final CComplexInputReportModel complexModel) {
		int codebookSheetIdx = 1;
		int startRow = 1;

		HSSFSheet sheet = file.getWorkbook().getSheetAt(codebookSheetIdx);
		HSSFRow firstDemoRow = sheet.getRow(startRow);

		List<HSSFCellStyle> colStyle = new ArrayList<>();
		List<Integer> colType = new ArrayList<>();

		// store cell parameters: style, type
		// for columns: 0,..,5
		// from first/demo row
		for (int col = 0; col < 5; col++) {
			colStyle.add(firstDemoRow.getCell(col).getCellStyle());
			colType.add(firstDemoRow.getCell(col).getCellType());
		}

		// ciselnik projektov (stlpce 0,1,2)
		int rowIndex = startRow;
		List<CProjectData> projects = complexModel.getProjectsData();
		for (CProjectData project : projects) {
			// use first demo row
			if (rowIndex == startRow) {
				// use first - demo row
				HSSFRow writingRow = sheet.getRow(rowIndex++);

				int colIdx = 0;
				HSSFRichTextString prjNameCellValue = new HSSFRichTextString(project.getPrjName());
				writingRow.getCell(colIdx).setCellValue(prjNameCellValue);

				colIdx = 1;
				HSSFRichTextString prjGroupCellValue = new HSSFRichTextString(project.getPrjGroup());
				writingRow.getCell(colIdx).setCellValue(prjGroupCellValue);

				colIdx = 2;
				HSSFRichTextString prjIdCellValue = new HSSFRichTextString(project.getPrjId());
				writingRow.getCell(colIdx).setCellValue(prjIdCellValue);
			}
			// ... but create next rows
			else {
				HSSFRow writingRow = sheet.createRow(rowIndex++);

				int colIdx = 0;
				HSSFRichTextString prjNameCellValue = new HSSFRichTextString(project.getPrjName());
				final HSSFCell cell0 = writingRow.createCell((short) colIdx);
				cell0.setCellStyle(colStyle.get(colIdx));
				cell0.setCellType(colType.get(colIdx).intValue());
				cell0.setCellValue(prjNameCellValue);

				colIdx = 1;
				HSSFRichTextString prjGroupCellValue = new HSSFRichTextString(project.getPrjGroup());
				final HSSFCell cell1 = writingRow.createCell((short) colIdx);
				cell1.setCellStyle(colStyle.get(colIdx));
				cell1.setCellType(colType.get(colIdx).intValue());
				cell1.setCellValue(prjGroupCellValue);

				colIdx = 2;
				HSSFRichTextString prjIdCellValue = new HSSFRichTextString(project.getPrjId());
				final HSSFCell cell2 = writingRow.createCell((short) colIdx);
				cell2.setCellStyle(colStyle.get(colIdx));
				cell2.setCellType(colType.get(colIdx).intValue());
				cell2.setCellValue(prjIdCellValue);
			}
		}

		// ciselnik zamestnancov (stlpce 3,4)
		rowIndex = startRow;
		List<CUserData> users = complexModel.getUsersData();
		for (CUserData user : users) {
			// use first demo row
			if (rowIndex == startRow) {
				HSSFRow writingRow = sheet.getRow(rowIndex++);

				int colIdx = 3;
				HSSFRichTextString userNameCellValue = new HSSFRichTextString(user.getEmployeeName());
				writingRow.getCell(colIdx).setCellValue(userNameCellValue);

				colIdx = 4;
				writingRow.getCell(colIdx).setCellValue(user.getEmployeeId());
			}
			// ... but create next rows
			else {
				HSSFRow writingRow = sheet.createRow(rowIndex++);

				int colIdx = 3;
				HSSFRichTextString userNameCellValue = new HSSFRichTextString(user.getEmployeeName());
				final HSSFCell cell0 = writingRow.createCell((short) colIdx);
				cell0.setCellStyle(colStyle.get(colIdx));
				cell0.setCellType(colType.get(colIdx).intValue());
				cell0.setCellValue(userNameCellValue);

				colIdx = 4;
				final HSSFCell cell1 = writingRow.createCell((short) colIdx);
				cell1.setCellStyle(colStyle.get(colIdx));
				cell1.setCellType(HSSFCell.CELL_TYPE_STRING); // colType.get(colIdx).intValue())
				cell1.setCellValue(user.getEmployeeId());
			}
		}
	}

}
