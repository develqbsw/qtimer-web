package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwTimeStampGenerateService {

	public List<CTmpTimeSheet> fetch(int startRow, int endRow, String sortProperty, Boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException;

	public CTmpTimeSheet add(final CTmpTimeSheet timeStampRecord) throws CBussinessDataException;

	public CTmpTimeSheet update(CTmpTimeSheet record) throws CBussinessDataException;

	public CTmpTimeSheet delete(CTmpTimeSheet record) throws CBussinessDataException;

}
