package sk.qbsw.sed.panel.upload;

import java.io.IOException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.lang.Bytes;

import sk.qbsw.sed.client.exception.CSystemFailureException;
import sk.qbsw.sed.client.response.CUploadResponseContent;
import sk.qbsw.sed.client.ui.screen.codelist.upload.IUploadConstant;
import sk.qbsw.sed.communication.service.IUploadClientService;
import sk.qbsw.sed.fw.component.feedback.CFeedbackPanel;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.panel.CPanel;
import sk.qbsw.sed.fw.utils.CCacheUtils;
import sk.qbsw.sed.page.CUploadPage;

/**
 * SubPage: /upload SubPage title: Upload číselníkov
 * 
 * Panel UploadPanel - upload číselníkov Projektov, Aktivít, Zamestnancov
 */
public class CUploadPanel extends CPanel {

	private FileUploadField projectsFileUpload;
	private FileUploadField activitiesFileUpload;
	private FileUploadField employeesFileUpload;

	@SpringBean
	private IUploadClientService uploadClientService;

	@SpringBean
	private CCacheUtils cache;

	/** error panel */
	private CFeedbackPanel errorPanel;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CUploadPanel(String id) {
		super(id);

		errorPanel = new CFeedbackPanel("errorPanel");
		registerFeedbackPanel(errorPanel);
		add(errorPanel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		Form<Void> projectsForm = new Form<Void>("projectsForm") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				final FileUpload uploadedFile = projectsFileUpload.getFileUpload();

				if (uploadedFile != null) {

					String content;
					try {
						content = IOUtils.toString(uploadedFile.getInputStream(), "Cp1250");
					} catch (IOException e) {
						throw new CSystemFailureException(e);
					} finally {
						uploadedFile.closeStreams();
					}

					String[] fileRows = content.split("\n");
					CUploadResponseContent result = null;
					try {
						result = uploadClientService.uploadProjects(fileRows);
						cache.deleteCacheForOrg(CCacheUtils.CACHE_PROJECT);
					} catch (CBussinessDataException e) {
						throw new CSystemFailureException(e);
					}

					processUploadResult(result);

				} else {
					getSession().info(getString("upload.no_file_selected"));
				}
				setResponsePage(CUploadPage.class);
			}
		};

		// Enable multipart mode (need for uploads file)
		projectsForm.setMultiPart(true);
		// max upload size, 500k
		projectsForm.setMaxSize(Bytes.kilobytes(500));

		projectsFileUpload = new FileUploadField("projectsFileUpload", new Model());

		projectsForm.add(projectsFileUpload);

		add(projectsForm);

		Form<Void> activitiesForm = new Form<Void>("activitiesForm") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				final FileUpload uploadedFile = activitiesFileUpload.getFileUpload();

				if (uploadedFile != null) {

					String content;
					try {
						content = IOUtils.toString(uploadedFile.getInputStream(), "Cp1250");
					} catch (IOException e) {
						throw new CSystemFailureException(e);
					} finally {
						uploadedFile.closeStreams();
					}

					String[] fileRows = content.split("\n");
					CUploadResponseContent result = null;
					try {
						result = uploadClientService.uploadActivities(fileRows);
						cache.deleteCacheForOrg(CCacheUtils.CACHE_PROJECT);
					} catch (CBussinessDataException e) {
						throw new CSystemFailureException(e);
					}

					processUploadResult(result);
				} else {
					getSession().info(getString("upload.no_file_selected"));
				}
				setResponsePage(CUploadPage.class);
			}
		};

		// Enable multipart mode (need for uploads file)
		activitiesForm.setMultiPart(true);
		// max upload size, 500k
		activitiesForm.setMaxSize(Bytes.kilobytes(500));

		activitiesFileUpload = new FileUploadField("activitiesFileUpload", new Model());

		activitiesForm.add(activitiesFileUpload);

		add(activitiesForm);

		Form<Void> employeesForm = new Form<Void>("employeesForm") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {

				final FileUpload uploadedFile = employeesFileUpload.getFileUpload();

				if (uploadedFile != null) {

					String content;
					try {
						content = IOUtils.toString(uploadedFile.getInputStream(), "Cp1250");
					} catch (IOException e) {
						throw new CSystemFailureException(e);
					} finally {
						uploadedFile.closeStreams();
					}

					String[] fileRows = content.split("\n");
					CUploadResponseContent result = null;
					try {
						result = uploadClientService.uploadEmployees(fileRows);
					} catch (CBussinessDataException e) {
						throw new CSystemFailureException(e);
					}

					processUploadResult(result);
				} else {
					getSession().info(getString("upload.no_file_selected"));
				}
				setResponsePage(CUploadPage.class);
			}
		};

		// Enable multipart mode (need for uploads file)
		employeesForm.setMultiPart(true);
		// max upload size, 500k
		employeesForm.setMaxSize(Bytes.kilobytes(500));

		employeesFileUpload = new FileUploadField("employeesFileUpload", new Model());

		employeesForm.add(employeesFileUpload);

		add(employeesForm);
	}

	private void processUploadResult(CUploadResponseContent uploadStatus) {
		if (IUploadConstant.UPLOAD_RESULT_OK.equals(uploadStatus.getResult())) {
			getSession().info(getString("upload.successfully_ended"));
		} else {
			// error in upload process
			if (IUploadConstant.UPLOAD_RESULT_ERR1.equals(uploadStatus.getResult())) {
				// security upload error
				getSession().error(getString("upload.security_error"));
			} else if (IUploadConstant.UPLOAD_RESULT_ERR2.equals(uploadStatus.getResult())) {
				// file structure error
				getSession().error(getString("upload.file_upload_file_structure_error") + " --> " + uploadStatus.getAdditionalInfo());
			} else if (IUploadConstant.UPLOAD_RESULT_ERR3.equals(uploadStatus.getResult())) {
				getSession().error(getString("upload.file_upload_big_file_error"));
			} else {
				// other error
				getSession().error(getString("upload.file_upload_error"));
			}
		}
	}
}
