package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.CStatsRecord;

public class CGetDataForStatsResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CStatsRecord> dataForGraph;

	public List<CStatsRecord> getDataForGraph() {
		return dataForGraph;
	}

	public void setDataForGraph(List<CStatsRecord> dataForGraph) {
		this.dataForGraph = dataForGraph;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
