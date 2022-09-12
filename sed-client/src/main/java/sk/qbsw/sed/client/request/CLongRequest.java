package sk.qbsw.sed.client.request;

/**
 * 
 * @author lobb
 *
 */
public class CLongRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * constructor
	 * 
	 * @param id
	 */
	public CLongRequest(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
