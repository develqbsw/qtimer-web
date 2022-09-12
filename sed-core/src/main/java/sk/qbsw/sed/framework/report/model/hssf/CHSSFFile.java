package sk.qbsw.sed.framework.report.model.hssf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class CHSSFFile {

	private HSSFWorkbook hssfWorkbook = null;

	public CHSSFFile(final String fileName) throws IOException {
		this(new FileInputStream(fileName));
	}

	public CHSSFFile(final InputStream stream) throws IOException {
		final POIFSFileSystem fs = new POIFSFileSystem(stream);
		this.hssfWorkbook = new HSSFWorkbook(fs);
	}

	public HSSFWorkbook getWorkbook() {
		return this.hssfWorkbook;
	}
}
