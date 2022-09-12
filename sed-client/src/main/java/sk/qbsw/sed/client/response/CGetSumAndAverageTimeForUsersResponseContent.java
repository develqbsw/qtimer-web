package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.timestamp.CGetSumAndAverageTimeForUser;

public class CGetSumAndAverageTimeForUsersResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CGetSumAndAverageTimeForUser> list;

	private Long totalSums;
	private Long totalAvg;

	public List<CGetSumAndAverageTimeForUser> getList() {
		return list;
	}

	public void setList(List<CGetSumAndAverageTimeForUser> list) {
		this.list = list;
	}

	public Long getTotalSums() {
		return totalSums;
	}

	public void setTotalSums(Long totalSums) {
		this.totalSums = totalSums;
	}

	public Long getTotalAvg() {
		return totalAvg;
	}

	public void setTotalAvg(Long totalAvg) {
		this.totalAvg = totalAvg;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
