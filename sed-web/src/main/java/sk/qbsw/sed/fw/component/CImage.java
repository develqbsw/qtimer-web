package sk.qbsw.sed.fw.component;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.model.CSystemSettings;

public class CImage extends Image {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean
	private CSystemSettings settings;

	private String url;

	private static final String DEFAULT_PHOTO_ID = "-1";

	public CImage(String id, Long userPhotoId) {
		super(id);
		this.url = getImageUrl(userPhotoId);
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);

		tag.put("src", url);
	}

	private String getImageUrl(Long userPhotoId) {

		String userPhotoIdString;
		Injector.get().inject(this);

		if (userPhotoId != null) {
			userPhotoIdString = Long.toString(userPhotoId);
		} else {
			userPhotoIdString = DEFAULT_PHOTO_ID;
		}
		return settings.getApiUrl() + CApiUrl.USER_PHOTO + "/" + userPhotoIdString;
	}

	public void setUrlForUser(Long userPhotoId) {
		this.url = getImageUrl(userPhotoId);
	}
}
