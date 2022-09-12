package sk.qbsw.sed.client.service.business;

import java.util.List;
import java.util.Locale;

import sk.qbsw.sed.client.model.message.CMessageRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IMessageService {

	/**
	 * return warning messages about unconfirmed timestamps for previous week and
	 * month. also can return empty list.
	 * 
	 * @param locale
	 * @return
	 * @throws CBusinessException
	 */
	public List<CMessageRecord> getMessages(Locale locale) throws CBusinessException;

	/**
	 * return today namesday for current locale, supported locales are SK and EN
	 * 
	 * @param locale
	 * @return
	 * @throws CBusinessException
	 */
	public String getNamesDayMessage(Locale locale) throws CBusinessException;
}
