package sk.qbsw.sed.web.ui.components;

import java.util.Arrays;

import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssUrlReferenceHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;

/**
 * this class holds objects witch can be inserted into header of wicket
 * component. usage :
 *
 * @Override public void renderHead (IHeaderResponse response) {
 *           super.renderHead(response);
 *           response.render(CCSSResources.BOOTSTRAP);
 *           response.render(CCSSResources.getCustomCssItem("")); }
 *
 * @author martinkovic
 * @since 2.1.0
 * @version 2.1.0
 */
public class CCSSResources {

	private static final String SCREEN = "screen";

	private CCSSResources() {
		// Auto-generated constructor stub
	}

	public static final CssHeaderItem FONTS = new CssUrlReferenceHeaderItem("assets/fonts/font.css", SCREEN, null) {
	};

	public static final CssHeaderItem DT_BOOTSTRAP = new CssUrlReferenceHeaderItem("assets/plugins/DataTables/media/css/DT_bootstrap.css", SCREEN, null) {
		@Override
		public java.lang.Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(SELECT_2);
		}
	};

	public static final CssHeaderItem SELECT_2 = new CssUrlReferenceHeaderItem("assets/plugins/select2/select2.css", SCREEN, null) {
	};

	public static final CssHeaderItem DATERANGEPICKER = new CssUrlReferenceHeaderItem("assets/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css", SCREEN, null) {
	};

	public static final CssHeaderItem NVD3 = new CssUrlReferenceHeaderItem("assets/plugins/nvd3/nv.d3.css", SCREEN, null) {
	};

	public static final CssHeaderItem FONT_AWESOME = new CssUrlReferenceHeaderItem("assets/plugins/font-awesome/css/font-awesome.min.css", SCREEN, null) {
	};

	public static final CssHeaderItem DATEPICKER = new CssUrlReferenceHeaderItem("assets/plugins/bootstrap-datepicker/css/datepicker.css", SCREEN, null) {
	};

	public static CssHeaderItem getCustomCSSItem(String url, final HeaderItem... items) {
		return new CssUrlReferenceHeaderItem(url, SCREEN, null) {
			@Override
			public Iterable<? extends HeaderItem> getDependencies() {
				return concatenate(items);
			}
		};
	}

	public static CssHeaderItem getInlineScriptItem(String css, final HeaderItem... items) {
		return new CssContentHeaderItem(css, null, null) {
			@Override
			public Iterable<? extends HeaderItem> getDependencies() {
				return concatenate(items);
			}
		};
	}

	public static Iterable<HeaderItem> concatenate(HeaderItem... objs) {
		return Arrays.asList(objs);
	}
}
