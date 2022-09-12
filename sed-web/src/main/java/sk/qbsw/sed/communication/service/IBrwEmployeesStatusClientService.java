package sk.qbsw.sed.communication.service;

import java.util.List;

import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IBrwEmployeesStatusClientService {

	List<CEmployeesStatusNew> fetch() throws CBussinessDataException;
}
