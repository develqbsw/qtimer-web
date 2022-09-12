package sk.qbsw.sed.server.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import sk.qbsw.sed.client.exception.CParseException;
import sk.qbsw.sed.common.utils.CDateUtils;
import sk.qbsw.sed.framework.report.export.stream.AXLSStreamReportExporter;
import sk.qbsw.sed.framework.report.model.CCellTypeEnum;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.model.hssf.CHSSFFile;
import sk.qbsw.sed.server.model.report.useful.CActivityData;
import sk.qbsw.sed.server.model.report.useful.CProjectData;
import sk.qbsw.sed.server.model.report.useful.CUserData;

public class CWeeklyXLSReportExporterImpl extends AXLSStreamReportExporter {

	private HSSFCellStyle cellStyleDate = null;
	private HSSFCellStyle cellStyleTime = null;

	@Override
	protected CReportOutputModel doExport(final CHSSFFile file, final CComplexInputReportModel complexModel) throws IOException, CParseException {
		this.fillCodebooks(file, complexModel);

		int numberOfProcessingRow = 0;
		final int cellCount = super.getCellCount();
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
					cell.setCellValue(this.getCalendarValueFromModel(row, index));
					break;
				case TYPE_TIME:
					cell.setCellStyle(this.cellStyleTime);
					cell.setCellValue(this.getCalendarValueFromModel(row, index).getTime());
					break;
				case TYPE_DURATION:
					cell.setCellValue(CDateUtils.convertDuration((String) this.getValueFromModel(row, index)));
					break;
				case TYPE_LONG:
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Long) this.getValueFromModel(row, index));
					break;
				case TYPE_INTEGER:
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Integer) this.getValueFromModel(row, index));
					break;
				default:
					throw new IllegalArgumentException("Unknown cell type: " + super.getCellType(index));
				}
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

	private void fillCodebooks(final CHSSFFile file, final CComplexInputReportModel complexModel) throws IOException {
		int codebookSheetIdx = 1;
		int startRow = 1;

		HSSFSheet sheet = file.getWorkbook().getSheetAt(codebookSheetIdx);
		HSSFRow firstDemoRow = sheet.getRow(startRow);

		List<HSSFCellStyle> colStyle = new ArrayList<>();
		List<Integer> colType = new ArrayList<>();

		// store cell parameters: style, type
		// for columns: 0,..,5
		// from first/demo row
		for (int col = 0; col < 6; col++) {
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

		// ciselnik aktivit (stlpec 3)
		rowIndex = startRow;
		List<CActivityData> activities = complexModel.getActivitiesData();
		for (CActivityData activity : activities) {
			int colIdx = 3;
			// use first demo row
			if (rowIndex == startRow) {
				HSSFRow writingRow = sheet.getRow(rowIndex++);

				HSSFRichTextString actNameCellValue = new HSSFRichTextString(activity.getActivityName());
				writingRow.getCell(colIdx).setCellValue(actNameCellValue);
			}
			// ... but create next rows
			else {
				HSSFRow writingRow = sheet.createRow(rowIndex++);

				HSSFRichTextString actNameCellValue = new HSSFRichTextString(activity.getActivityName());
				final HSSFCell cell0 = writingRow.createCell((short) colIdx);
				cell0.setCellStyle(colStyle.get(colIdx));
				cell0.setCellType(colType.get(colIdx).intValue());
				cell0.setCellValue(actNameCellValue);
			}
		}

		// ciselnik zamestnancov (stlpce 4,5)
		rowIndex = startRow;
		List<CUserData> users = complexModel.getUsersData();
		for (CUserData user : users) {
			// use first demo row
			if (rowIndex == startRow) {
				HSSFRow writingRow = sheet.getRow(rowIndex++);

				int colIdx = 4;
				HSSFRichTextString userNameCellValue = new HSSFRichTextString(user.getEmployeeName());
				writingRow.getCell(colIdx).setCellValue(userNameCellValue);

				if (user.getEmployeeId() == null) {

					Logger logger = Logger.getLogger(CWeeklyXLSReportExporterImpl.class.getName());
					logger.error("GENERATE WEEKLY REPORT: FAILED,  user " + user.getEmployeeName() + " has not employee id associated");
					throw new IOException("GENERATE WEEKLY REPORT: FAILED,  user " + user.getEmployeeName() + " has not employee id associated");
				}

				colIdx = 5;
				writingRow.getCell(colIdx).setCellValue(user.getEmployeeId());
			}
			// ... but create next rows
			else {
				HSSFRow writingRow = sheet.createRow(rowIndex++);

				int colIdx = 4;
				HSSFRichTextString userNameCellValue = new HSSFRichTextString(user.getEmployeeName());
				final HSSFCell cell0 = writingRow.createCell((short) colIdx);
				cell0.setCellStyle(colStyle.get(colIdx));
				cell0.setCellType(colType.get(colIdx).intValue());
				cell0.setCellValue(userNameCellValue);

				if (user.getEmployeeId() == null) {

					Logger logger = Logger.getLogger(CWeeklyXLSReportExporterImpl.class.getName());
					logger.error("GENERATE WEEKLY REPORT: FAILED,  user " + user.getEmployeeName() + " has not employee id associated");
					throw new IOException("GENERATE WEEKLY REPORT: FAILED,  user " + user.getEmployeeName() + " has not employee id associated");
				}

				colIdx = 5;
				final HSSFCell cell1 = writingRow.createCell((short) colIdx);
				cell1.setCellStyle(colStyle.get(colIdx));
				cell1.setCellType(HSSFCell.CELL_TYPE_STRING); // colType.get(colIdx).intValue())
				cell1.setCellValue(user.getEmployeeId());
			}
		}
	}
}
