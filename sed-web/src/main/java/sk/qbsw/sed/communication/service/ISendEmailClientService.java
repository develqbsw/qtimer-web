package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface ISendEmailClientService {

	/**
	 * 
	 * @param data
	 * @return
	 * @throws CBussinessDataException
	 */
	Long sendMissingEmployeesEmail(CUserSystemEmailContainer data) throws CBussinessDataException;
}
