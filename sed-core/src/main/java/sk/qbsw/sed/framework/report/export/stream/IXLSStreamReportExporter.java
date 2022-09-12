package sk.qbsw.sed.framework.report.export.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sk.qbsw.sed.client.exception.CParseException;
import sk.qbsw.sed.framework.report.export.IReportExporter;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;
import sk.qbsw.sed.framework.report.model.CReportOutputModel;
import sk.qbsw.sed.framework.report.transform.stream.IXLSStreamReportTransformer;

public interface IXLSStreamReportExporter extends IReportExporter {

	public CReportOutputModel exportToStream(OutputStream outputStream, InputStream templateStream, final CComplexInputReportModel complexModel) throws IOException, CParseException;

	public void setXLSTransformer(IXLSStreamReportTransformer transformer);
}
