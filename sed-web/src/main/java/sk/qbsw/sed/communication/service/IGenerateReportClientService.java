package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.client.request.CGenerateEmployeesReportRequest;
import sk.qbsw.sed.client.request.CGenerateReportRequest;
import sk.qbsw.sed.client.response.CGenerateReportResponseContent;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IGenerateReportClientService {

	/**
	 * 
	 * @param request
	 * @return
	 * @throws CBussinessDataException
	 */
	public CGenerateReportResponseContent generate(CGenerateReportRequest request) throws CBussinessDataException;

	/**
	 * 
	 * @param request
	 * @return
	 * @throws CBussinessDataException
	 */
	public CGenerateReportResponseContent generateEmployeesExport(CGenerateEmployeesReportRequest request) throws CBussinessDataException;

	public CGenerateReportResponseContent generateWorkplaceExport(CGenerateEmployeesReportRequest request) throws CBussinessDataException;
}
