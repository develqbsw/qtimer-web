package sk.qbsw.sed.client.request;

public class CGetInfoForMobileTimerRequest extends ARequest {

	private static final long serialVersionUID = 1L;

	private Boolean countToday;

	public Boolean getCountToday() {
		return countToday;
	}

	public void setCountToday(Boolean countToday) {
		this.countToday = countToday;
	}

	@Override
	public Boolean validate() {
		return null;
	}
}
