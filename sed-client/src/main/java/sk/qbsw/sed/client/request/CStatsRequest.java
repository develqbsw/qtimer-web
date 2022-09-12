package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.CStatsFilter;

public class CStatsRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CStatsFilter filter;

	public CStatsRequest() {
		super();
	}

	public CStatsRequest(CStatsFilter filter) {
		super();
		this.filter = filter;
	}

	public CStatsFilter getFilter() {
		return filter;
	}

	public void setFilter(CStatsFilter filter) {
		this.filter = filter;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
