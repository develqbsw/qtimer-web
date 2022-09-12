package sk.qbsw.sed.framework.report.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class CHSSFUtils {
	
	private CHSSFUtils() {
		// Auto-generated constructor stub
	}

	public static void setFillBackgroundColor(final HSSFCellStyle style, final HSSFColor color) {
		style.setFillForegroundColor(color.getIndex());
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	}

	public static void setFillBackgroundColor(final HSSFWorkbook workbook, final HSSFCell cell, final HSSFColor color) {
		final HSSFCellStyle style = workbook.createCellStyle();
		setFillBackgroundColor(style, color);
		cell.setCellStyle(style);
	}
}
