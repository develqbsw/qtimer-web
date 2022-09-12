package sk.qbsw.sed.client.response;

import java.util.Calendar;
import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;

public class CGetDataForGraphOfAttendanceResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CAttendanceDuration> dataForGraph;

	private Calendar actualDate;

	public List<CAttendanceDuration> getDataForGraph() {
		return dataForGraph;
	}

	public void setDataForGraph(List<CAttendanceDuration> dataForGraph) {
		this.dataForGraph = dataForGraph;
	}

	public Calendar getActualDate() {
		return actualDate;
	}

	public void setActualDate(Calendar actualDate) {
		this.actualDate = actualDate;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
