package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IRequestStatusClientService {

	/**
	 * Returns list of valid request status records.
	 * 
	 * @return list of records
	 * @throws CBussinessDataException
	 */
	List<CCodeListRecord> getValidRecords() throws CBussinessDataException;
}
