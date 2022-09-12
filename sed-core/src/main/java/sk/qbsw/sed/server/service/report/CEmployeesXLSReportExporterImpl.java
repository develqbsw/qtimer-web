package sk.qbsw.sed.server.service.report;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.util.Region;

import sk.qbsw.sed.client.exception.CParseException;
import sk.qbsw.sed.framework.report.export.stream.AXLSStreamReportExporter;
import sk.qbsw.sed.framework.report.model.CCellTypeEnum;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.model.hssf.CHSSFFile;

public class CEmployeesXLSReportExporterImpl extends AXLSStreamReportExporter {
	
	private HSSFCellStyle cellStyleBold = null;
	private HSSFCellStyle cellStyleBorder = null;

	@Override
	protected CReportOutputModel doExport(CHSSFFile file, CComplexInputReportModel complexModel) throws IOException, CParseException {
		int numberOfProcessingRow = 0;
		final int cellCount = super.getCellCount();
		String thisTypeOfEmploymentDescription = "";
		String previousTypeOfEmploymentDescription = "";
		// ak je false nepridá prázdny riadok, ak true pridá
		Boolean nextRow = false;

		for (final CReportModel row : complexModel.getReportRows()) {
			thisTypeOfEmploymentDescription = (String) this.getValueFromModel(row, 6);

			if (!thisTypeOfEmploymentDescription.equals(previousTypeOfEmploymentDescription)) {
				if (nextRow) {
					// pridať prázdny riadok
					final HSSFRow emptyRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).createRow(++numberOfProcessingRow);
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(numberOfProcessingRow, (short) 0, numberOfProcessingRow, (short) 22));

					// nastavím štýl všetkým bunkám v riadku aby bol riadok
					// orámovaný
					for (int i = 1; i < 23; i++) {
						HSSFCell cellWithBorder = emptyRow.createCell((short) i);
						cellWithBorder.setCellStyle(this.cellStyleBorder);
					}
				}

				final HSSFRow writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).createRow(++numberOfProcessingRow);

				if (thisTypeOfEmploymentDescription.equals("Zamestnanec")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(numberOfProcessingRow, (short) 0, numberOfProcessingRow, (short) 22));
					addTypeOfEmpCell(writingRow, thisTypeOfEmploymentDescription);
				} else if (thisTypeOfEmploymentDescription.equals("Pracovník")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(numberOfProcessingRow, (short) 0, numberOfProcessingRow, (short) 22));
					addTypeOfEmpCell(writingRow, thisTypeOfEmploymentDescription);
				} else if (thisTypeOfEmploymentDescription.equals("Brigádnik")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(numberOfProcessingRow, (short) 0, numberOfProcessingRow, (short) 22));
					addTypeOfEmpCell(writingRow, thisTypeOfEmploymentDescription);
				} else if (thisTypeOfEmploymentDescription.equals("Neuvedený")) {
					file.getWorkbook().getSheetAt(getTargetSheetIndex()).addMergedRegion(new Region(numberOfProcessingRow, (short) 0, numberOfProcessingRow, (short) 22));
					addTypeOfEmpCell(writingRow, thisTypeOfEmploymentDescription);
				}
				nextRow = true;
			}

			final HSSFRow writingRow = file.getWorkbook().getSheetAt(getTargetSheetIndex()).createRow(++numberOfProcessingRow);
			for (int index = 0; index < cellCount; index++) {
				final HSSFCell cell = writingRow.createCell((short) index);
				final CCellTypeEnum cellType = super.getCellType(index);
				if (CCellTypeEnum.TYPE_STRING.equals(cellType)) {
					cell.setCellStyle(this.cellStyleBorder);
					cell.setCellValue(new HSSFRichTextString((String) this.getValueFromModel(row, index)));
				} else if (CCellTypeEnum.TYPE_INTEGER.equals(cellType)) {
					cell.setCellStyle(this.cellStyleBorder);
					cell.setCellValue((Integer) this.getValueFromModel(row, index));
				} else {
					throw new IllegalArgumentException("Unknown cell type: " + super.getCellType(index));
				}
			}

			previousTypeOfEmploymentDescription = thisTypeOfEmploymentDescription;
		}

		super.finalizeExportProcess(false);

		return new CReportOutputModel();
	}

	@Override
	protected void createInitialStyles(final CHSSFFile file) {
		this.cellStyleBold = file.getWorkbook().createCellStyle();
		HSSFFont font = file.getWorkbook().createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		this.cellStyleBold.setFont(font);
		this.cellStyleBold.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBold.setBorderTop(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBold.setBorderRight(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBold.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		this.cellStyleBorder = file.getWorkbook().createCellStyle();
		this.cellStyleBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBorder.setBorderTop(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBorder.setBorderRight(HSSFCellStyle.BORDER_THIN);
		this.cellStyleBorder.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	}

	private Object getValueFromModel(final CReportModel model, final int index) {
		return model.getObject(index);
	}

	private void addTypeOfEmpCell(HSSFRow writingRow, String thisTypeOfEmploymentDescription) {
		final HSSFCell cell = writingRow.createCell((short) 0);
		cell.setCellStyle(this.cellStyleBold);
		cell.setCellValue(new HSSFRichTextString(thisTypeOfEmploymentDescription));

		// nastavím štýl všetkým bunkám v riadku aby bol riadok orámovaný
		for (int i = 1; i < 23; i++) {
			HSSFCell cellWithBorder = writingRow.createCell((short) i);
			cellWithBorder.setCellStyle(this.cellStyleBorder);
		}
	}
}
