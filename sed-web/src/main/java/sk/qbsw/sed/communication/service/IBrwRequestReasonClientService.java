package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwRequestReasonClientService {

	/**
	 * @param first
	 * @param count
	 * @param sortProperty
	 * @param sortAsc
	 * @return
	 * @throws CBussinessDataException
	 */
	public List<CRequestReasonRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc) throws CBussinessDataException;

	/**
	 * @return
	 * @throws CBussinessDataException
	 */
	public Long count() throws CBussinessDataException;
}
