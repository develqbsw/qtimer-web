
package sk.qbsw.sed.server.service.report;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;

import sk.qbsw.sed.client.exception.CParseException;
import sk.qbsw.sed.framework.report.export.stream.AXLSStreamReportExporter;
import sk.qbsw.sed.framework.report.model.CCellTypeEnum;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.model.hssf.CHSSFFile;

public class CWorkplaceXLSReportExporterImpl extends AXLSStreamReportExporter {
	
	private HSSFCellStyle cellStyleBold = null;
	private HSSFCellStyle cellStyleBorder = null;

	@Override
	protected CReportOutputModel doExport(CHSSFFile file, CComplexInputReportModel complexModel) throws IOException, CParseException {
		int numberOfProcessingRow = 0;
		final int cellCount = super.getCellCount();

		for (final CReportModel row : complexModel.getReportRows()) {
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
}
