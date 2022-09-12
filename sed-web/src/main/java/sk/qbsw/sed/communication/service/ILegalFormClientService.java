package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface ILegalFormClientService {

	/**
	 * @return
	 * @throws CBussinessDataException
	 */
	public List<CCodeListRecord> getValidRecords() throws CBussinessDataException;
}
