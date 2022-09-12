package sk.qbsw.sed.web.ui.components;

import java.util.Arrays;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptUrlReferenceHeaderItem;

/**
 * this class holds objects witch can be inserted into header of wicket
 * component. usage :
 *
 * @Override public void renderHead (IHeaderResponse response) {
 *           super.renderHead(response); response.render(CJSResources.JQUERY);
 *           response.render(CJSResources.getCustomScriptItem("assets/scripts/pnp-common.js"));
 *           }
 *
 * @author martinkovic
 * @since 2.1.0
 * @version 2.1.0
 */
public class CJSResources {
	
	private static final String UTF_8 = "UTF-8";

	private CJSResources() {
		// Auto-generated constructor stub
	}

	public static final JavaScriptHeaderItem DT_BOOTSTRAP = new JavaScriptUrlReferenceHeaderItem("assets/plugins/DataTables/media/js/jquery.dataTables.min.js", null, true, UTF_8, null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(CCSSResources.DT_BOOTSTRAP, SELECT_2);
		}
	};

	public static final JavaScriptHeaderItem SELECT_2 = new JavaScriptUrlReferenceHeaderItem("assets/plugins/select2/select2.min.js", null, true, UTF_8, null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(CCSSResources.SELECT_2);
		}
	};

	public static final JavaScriptHeaderItem DATATABLES = new JavaScriptUrlReferenceHeaderItem("assets/js/table-data.js", null, true, UTF_8, null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(CJSResources.DT_BOOTSTRAP, SELECT_2);
		}
	};

	public static final JavaScriptHeaderItem JQUERY_DATERANGEPICKER_DATE = new JavaScriptUrlReferenceHeaderItem("assets/plugins/bootstrap-daterangepicker/daterangepicker.js", null, true, UTF_8,
			null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(/* JQUERY, */ CCSSResources.DATERANGEPICKER, CCSSResources.DATEPICKER, CCSSResources.FONT_AWESOME);
		}
	};

	public static final JavaScriptHeaderItem JQUERY_DATERANGEPICKER_DATE_SK = new JavaScriptUrlReferenceHeaderItem("assets/plugins/bootstrap-daterangepicker/daterangepicker.sk.js", null, true,
			UTF_8, null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(/* JQUERY, */ CCSSResources.DATERANGEPICKER, CCSSResources.DATEPICKER, CCSSResources.FONT_AWESOME);
		}
	};

	public static final JavaScriptHeaderItem D3V3 = new JavaScriptUrlReferenceHeaderItem("assets/plugins/nvd3/lib/d3.v3.js", null, true, UTF_8, null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(NVD3);
		}
	};

	public static final JavaScriptHeaderItem NVD3 = new JavaScriptUrlReferenceHeaderItem("assets/plugins/nvd3/nv.d3.min.js", null, true, UTF_8, null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Iterable<? extends HeaderItem> getDependencies() {
			return concatenate(CCSSResources.NVD3);
		}
	};

	public static final JavaScriptHeaderItem JQUERY_MOMENT = new JavaScriptUrlReferenceHeaderItem("assets/plugins/bootstrap-daterangepicker/moment.js", null, true, UTF_8, null) {
		
	};

	public static JavaScriptHeaderItem getCustomScriptItem(String url, final HeaderItem... items) {
		return new JavaScriptUrlReferenceHeaderItem(url, null, true, UTF_8, null) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Iterable<? extends HeaderItem> getDependencies() {
				return concatenate(items);
			}
		};
	}

	public static JavaScriptHeaderItem getInlineScriptItem(String javaScript, final HeaderItem... items) {
		return new JavaScriptContentHeaderItem(javaScript, null, null) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
