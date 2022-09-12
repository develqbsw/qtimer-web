package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwEmployeeClientService {

	public List<CEmployeeRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, String name) throws CBussinessDataException;

	public Long count(String name) throws CBussinessDataException;
}
