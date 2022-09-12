package sk.qbsw.sed.client.service.brw;

import java.util.List;

import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IBrwRequestService extends IBrwService<CRequestRecord> {
	
	/**
	 * Fetching of table data
	 * 
	 * @param startRow starting record
	 * @param endRow   final record to read
	 * @return list of red data
	 */
	public List<CRequestRecord> fetch(int startRow, int endRow, IFilterCriteria criteria) throws CBusinessException;

	/**
	 * 
	 * Nacita ziadosti pre graf podla zadaneho filtra. Pre vikend alebo sviatok
	 * vytvori nove ziadosti. Ak su dve ziadosti na ten isty rozsah da len tu s
	 * vyssou prioritou (schvalena > zadana > zamietnuta > zrusena).
	 * 
	 * @param criteria
	 * @return
	 * @throws CBusinessException
	 */
	public List<CRequestRecordForGraph> loadDataForGraph(IFilterCriteria criteria) throws CBusinessException;
}
