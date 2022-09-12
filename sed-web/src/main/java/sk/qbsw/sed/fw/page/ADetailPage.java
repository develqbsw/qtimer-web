package sk.qbsw.sed.fw.page;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

/**
 * abstract for all detail pages (CRU pages)
 * 
 * @author Peter Bozik
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public abstract class ADetailPage extends AAuthenticatedPage {

	private static final long serialVersionUID = 1L;
	public  static final String DETAIL_PARAMETER = "detailParameter";
	private StringValue entityID;

	/**
	 * 
	 * @param parameters
	 * @param entityParameter null if create page
	 */
	public ADetailPage(PageParameters parameters, String entityParameter) {
		super(parameters);
		if (StringUtils.isNotBlank(entityParameter)) {
			entityID = parameters.get(entityParameter);
		}
	}

	protected Long getEntityIDLong() {
		if (entityID == null) {
			return null;
		}
		if (!StringUtils.isNumeric(entityID.toString())) {
			return null;
		}
		return entityID.toOptionalLong();
	}
}
