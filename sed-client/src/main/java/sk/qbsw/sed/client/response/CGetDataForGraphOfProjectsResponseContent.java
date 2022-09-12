package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CProjectDuration;

public class CGetDataForGraphOfProjectsResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CProjectDuration> dataForGraph;

	public List<CProjectDuration> getDataForGraph() {
		return dataForGraph;
	}

	public void setDataForGraph(List<CProjectDuration> dataForGraph) {
		this.dataForGraph = dataForGraph;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
