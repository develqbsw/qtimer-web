package sk.qbsw.sed.framework.report.export.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;

import sk.qbsw.sed.client.exception.CParseException;
import sk.qbsw.sed.framework.report.model.CCellTypeEnum;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.model.hssf.CHSSFFile;
import sk.qbsw.sed.framework.report.transform.stream.IXLSStreamReportTransformer;

public abstract class AXLSStreamReportExporter extends AStreamReportExporter {

	// cielovy workseet pre zapis udajov
	private int targetSheetIndex = 0; // default value

	/**
	 * Returns target worksheet index for data writing
	 * 
	 * @return
	 */
	public int getTargetSheetIndex() {
		return targetSheetIndex;
	}

	/**
	 * Set index target worksheet (order from 0) for data writing
	 * 
	 * @param targetSheetIndex
	 */
	public void setTargetSheetIndex(int targetSheetIndex) {
		this.targetSheetIndex = targetSheetIndex;
	}

	private CHSSFFile file;
	private OutputStream outputStream;
	private CComplexInputReportModel complexModel;
	private IXLSStreamReportTransformer transformer;

	private void prepareXLSOutputFile(final InputStream templateStream) throws IOException {
		final CHSSFFile file = new CHSSFFile(templateStream);
		this.file = file;
		this.createInitialStyles(this.file);
	}

	@Override
	public final CReportOutputModel exportToStream(final OutputStream outputStream, final InputStream templateStream, final CComplexInputReportModel complexModel) throws IOException, CParseException {
		this.complexModel = complexModel;
		this.outputStream = outputStream;
		this.prepareXLSOutputFile(templateStream);
		return this.doExport(this.file, complexModel);
	}

	@Override
	public void setXLSTransformer(final IXLSStreamReportTransformer transformer) {
		this.transformer = transformer;
	}

	protected CCellTypeEnum getCellType(final int cellIndex) {
		if (null != this.transformer) {
			return this.transformer.getCellDefinition().get(cellIndex).getCellType();
		}
		return CCellTypeEnum.TYPE_STRING;
	}

	protected int getCellCount() {
		if (null != this.transformer) {
			return this.transformer.getCellDefinition().size();
		} else {
			if (!this.complexModel.getReportRows().isEmpty()) {
				return BeanUtils.getPropertyDescriptors(this.complexModel.getReportRows().get(0).getClass()).length;
			}
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	protected void finalizeExportProcess(Boolean evaluate) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.file.getWorkbook().write(out);
		out.flush();
		out.close();
		final ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
		final CHSSFFile evaluationFile = new CHSSFFile(input);
		input.close();

		if (evaluate) {
			// start of evaluation process
			final HSSFWorkbook workbook = evaluationFile.getWorkbook();
			final HSSFSheet sheet = workbook.getSheetAt(getTargetSheetIndex());
			final HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(sheet, workbook);
			for (final Iterator<HSSFRow> rowIterator = sheet.rowIterator(); rowIterator.hasNext();) {
				final HSSFRow row = rowIterator.next();
				evaluator.setCurrentRow(row);
				for (final Iterator<HSSFCell> cellIterator = row.cellIterator(); cellIterator.hasNext();) {
					final HSSFCell cell = cellIterator.next();
					if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
						final String formula = cell.getCellFormula();
						cell.setCellFormula(formula);
						evaluator.evaluateFormulaCell(cell);
					}
				}
			}
			// end of evaluation process
		}

		// write to output stream
		evaluationFile.getWorkbook().write(this.outputStream);
		this.outputStream.flush();
		this.outputStream.close();
	}

	protected abstract CReportOutputModel doExport(CHSSFFile file, final CComplexInputReportModel complexModel) throws IOException, CParseException;

	protected abstract void createInitialStyles(CHSSFFile file);

}
