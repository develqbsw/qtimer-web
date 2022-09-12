package sk.qbsw.sed.panel.users;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;
import sk.qbsw.sed.client.response.CGenerateReportResponseContent;
import sk.qbsw.sed.communication.service.IGenerateReportClientService;
import sk.qbsw.sed.component.CJavascriptResources;
import sk.qbsw.sed.component.jstree.CJSTreePanel;
import sk.qbsw.sed.component.jstree.ITreeNodeSelected;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.exception.CBussinessDataExceptionProcessor;
import sk.qbsw.sed.fw.model.CPageResourceConfiguration;
import sk.qbsw.sed.fw.panel.CEmptyPanel;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.AJAXDownload;
import sk.qbsw.sed.fw.utils.CStringResourceReader;
import sk.qbsw.sed.panel.timesheet.editable.CGenerateReportUtils;
import sk.qbsw.sed.web.ui.CSedSession;

/**
 * SubPage: /org/structure SubPage title: Organizačná štruktúra
 * 
 * OrgStructureTreePanel - Prehľady
 */
public class COrgStructureTreePanel extends CPanel {
	@SpringBean
	private IGenerateReportClientService generateReportClientService;

	/** serial uid */
	private static final long serialVersionUID = 1L;
	private final ITreeNodeSelected nodeSelectedListener;
	private CJSTreePanel panel;
	private Label changeDraggableLabel;
	private Label showAlsoNotValidLabel;
	private Panel feedbackPanel = null;
	private CGenerateReportResponseContent resp;
	private WebMarkupContainer exportExcel;

	public COrgStructureTreePanel(String id, ITreeNodeSelected nodeSelectedListener, final Panel feedbackPanel) {
		super(id);
		this.nodeSelectedListener = nodeSelectedListener;
		this.feedbackPanel = feedbackPanel;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		try {
			panel = new CJSTreePanel<CViewOrganizationTreeNodeRecord>("tree", new CEmployeeJSTreeDataProvider());
			panel.setDraggable(null, false);
			panel.setNodeSelectedListener(nodeSelectedListener);
			add(panel);
		} catch (CBussinessDataException e) {
			CBussinessDataExceptionProcessor.process(e, getSession(), null, getPage());
			add(new CEmptyPanel("tree"));
		}

		AbstractLink changeLink = new AjaxLink("changeDraggable") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (panel != null) {
					panel.setDraggable(target, !panel.isDraggable());
					if (panel.isDraggable()) {
						changeDraggableLabel.setDefaultModel(Model.of(getString("page.org.structure.label.change.on")));
					} else {
						changeDraggableLabel.setDefaultModel(Model.of(getString("page.org.structure.label.change.off")));
					}
					target.add(this);
				}
			}
		};
		changeLink.setOutputMarkupId(true);
		changeLink.setEscapeModelStrings(false);
		add(changeLink);

		changeDraggableLabel = new Label("changeDraggableLabel", getString("page.org.structure.label.change.off"));
		changeLink.add(changeDraggableLabel);

		AbstractLink showAlsoNotValid = new AjaxLink("showAlsoNotValid") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (panel.getOnlyValid()) {
					panel.setOnlyValid(false);
					showAlsoNotValidLabel.setDefaultModel(Model.of(getString("page.org.structure.label.show.valid")));
				} else {
					panel.setOnlyValid(true);
					showAlsoNotValidLabel.setDefaultModel(Model.of(getString("page.org.structure.label.show.not.valid")));
				}

				target.add(this);
				target.add(panel);
			}
		};
		add(showAlsoNotValid);

		showAlsoNotValidLabel = new Label("showAlsoNotValidLabel", getString("page.org.structure.label.show.not.valid"));
		showAlsoNotValid.add(showAlsoNotValidLabel);

		final AJAXDownload download = new AJAXDownload() {
			private static final long serialVersionUID = 1L;

			@Override
			protected IResourceStream getResourceStream() {
				return createResourceStream(resp.getByteArray());
			}

			@Override
			protected String getFileName() {
				return resp.getFileName();
			}
		};
		add(download);

		exportExcel = new WebMarkupContainer("exportExcel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return true;
			}
		};
		exportExcel.setOutputMarkupId(true);
		exportExcel.add(new AttributeAppender("title", getString("common.button.export")));

		AjaxFallbackLink<Object> generateWorkplaceReport = new AjaxFallbackLink<Object>("generateWorkplaceReport") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				generateWorkplaceReport(CSedSession.get().getUser().getClientInfo().getClientId(), panel.getOnlyValid(), feedbackPanel);
				download.initiate(target);
				target.add(feedbackPanel);
				target.add(exportExcel);
			}
		};

		AjaxFallbackLink<Object> generateEmployeesReport = new AjaxFallbackLink<Object>("generateEmployeesReport") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				generateEmployeesReport(CSedSession.get().getUser().getClientInfo().getClientId(), panel.getOnlyValid(), feedbackPanel);
				download.initiate(target);
				target.add(feedbackPanel);
				target.add(exportExcel);
			}
		};

		exportExcel.add(generateWorkplaceReport);
		exportExcel.add(generateEmployeesReport);

		add(exportExcel);
	}

	@Override
	protected void initResources(CPageResourceConfiguration resourceConfiguration) {
		resourceConfiguration.addPluginScript(CJavascriptResources.JSTREE);
		resourceConfiguration.addThemeScript(CJavascriptResources.SED_TREE);
	}

	private void generateEmployeesReport(Long clientId, Boolean onlyValid, final Panel feedbackPanel) {
		try {
			resp = generateReportClientService.generateEmployeesExport(CGenerateReportUtils.getParams(clientId, onlyValid));
		} catch (CBussinessDataException e) {
			feedbackPanel.error(CStringResourceReader.read(e.getModel().getServerCode()));
			Logger.getLogger(COrgStructureTreePanel.class).error(e);
		}
	}

	private void generateWorkplaceReport(Long clientId, Boolean onlyValid, final Panel feedbackPanel) {
		try {
			resp = generateReportClientService.generateWorkplaceExport(CGenerateReportUtils.getParams(clientId, onlyValid));
		} catch (CBussinessDataException e) {
			feedbackPanel.error(CStringResourceReader.read(e.getModel().getServerCode()));
			Logger.getLogger(COrgStructureTreePanel.class).error(e);
		}
	}

	private IResourceStream createResourceStream(final byte[] byteArray) {

		IResourceStream resourceStream = new AbstractResourceStream() {
			private static final long serialVersionUID = 1L;

			InputStream inStream;

			@Override
			public String getContentType() {
				return "application/vnd.ms-excel";
			}

			@Override
			public InputStream getInputStream() throws ResourceStreamNotFoundException {
				inStream = new ByteArrayInputStream(byteArray);
				return inStream;
			}

			@Override
			public void close() throws IOException {
				inStream.close();
			}
		};

		return resourceStream;
	}
}
