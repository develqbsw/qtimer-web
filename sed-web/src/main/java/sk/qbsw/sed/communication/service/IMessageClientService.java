package sk.qbsw.sed.communication.service;

import java.util.List;
import java.util.Locale;

import sk.qbsw.sed.client.model.message.CMessageRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IMessageClientService {

	/**
	 * return warning messages about unconfirmed timestamps for previous week and
	 * month. also can return empty list.
	 * 
	 * @param locale
	 * @return
	 * @throws CBussinessDataException
	 */
	public List<CMessageRecord> getMessages(Locale locale) throws CBussinessDataException;

	/**
	 * return today namesday for current locale, supported locales are SK and EN
	 * 
	 * @param locale
	 * @return
	 * @throws CBussinessDataException
	 */
	public String getNamesday(Locale locale) throws CBussinessDataException;
}
