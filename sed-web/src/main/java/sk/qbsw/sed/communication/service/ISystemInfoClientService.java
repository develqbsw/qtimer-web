package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface ISystemInfoClientService {

	public String getVersion() throws CBussinessDataException;
}
