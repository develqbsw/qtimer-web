package sk.qbsw.sed.communication.service;

import java.util.Date;
import java.util.List;

import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.web.ui.components.panel.ChooseDefaultTimestampFormModel;

public interface IBrwTimeStampService {

	public CTimeStampRecord update(CTimeStampRecord record) throws CBussinessDataException;

	/**
	 * Returns work time for the selected user in input date interval
	 * 
	 * @param userId   selected user identifier
	 * @param dateFrom start border of selected date interval (inclusive)
	 * @param dateTo   stop of selected date interval (inclusive)
	 * @return
	 */
	public Long getWorkTimeInInterval(Long userId, Date dateFrom, Date dateTo) throws CBussinessDataException;

	public List<CTimeStampRecord> loadData(Long first, Long count, String sortProperty, boolean sortAsc, IFilterCriteria criteria) throws CBussinessDataException;

	public Long count(IFilterCriteria criteria) throws CBussinessDataException;

	/**
	 * Reads detail of timestamp record
	 * 
	 * @param timeStampId
	 * @return
	 */
	public CTimeStampRecord getDetail(Long timeStampId) throws CBussinessDataException;

	public Boolean showEditButtonOnDetail(Long timeStampId) throws CBussinessDataException;

	public Long massChangeTimestamps(CSubrodinateTimeStampBrwFilterCriteria filter, ChooseDefaultTimestampFormModel formModel) throws CBussinessDataException;
}
