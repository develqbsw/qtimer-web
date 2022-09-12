package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IRequestTypeClientService {

	public List<CCodeListRecord> getValidRecords() throws CBussinessDataException;

	public List<CCodeListRecord> getValidRecordsForRequestReason() throws CBussinessDataException;
}
