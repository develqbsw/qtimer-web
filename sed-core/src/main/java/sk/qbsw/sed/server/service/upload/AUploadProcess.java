package sk.qbsw.sed.server.service.upload;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sk.qbsw.sed.client.response.CUploadResponseContent;
import sk.qbsw.sed.client.ui.screen.codelist.upload.IUploadConstant;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * 
 * @author rosenberg
 * 
 */
public abstract class AUploadProcess implements IUploadProcess {

	private List<Long> objectIds;

	public AUploadProcess() {
		this.objectIds = new ArrayList<>();
	}

	/**
	 * upload file content
	 * 
	 * @throws CSecurityException
	 */
	@Override
	public CUploadResponseContent upload(String[] fileRows) throws CSecurityException {
		CUploadResponseContent response = new CUploadResponseContent();

		String VALUES_SEPARATOR = new String("\u00A4"); // "¤"

		this.objectIds = new ArrayList<>();

		preProcessing(this.objectIds);

		if (fileRows != null && fileRows.length > 0) {
			try {
				String result = "";
				for (int i = 0; i < fileRows.length; i++) {
					String tmp = fileRows[i].trim();
					if ("".equals(tmp)) {
						// prazdne riadky ignorujeme
						continue;
					}

					String[] values = tmp.split(VALUES_SEPARATOR);

					// Start SED-357 - Doplnenie kontroly pri načítaní
					// číselníkov
					// Pri načítaní číselníkov zo súborov cez obrazovku OA.C.030
					// doplň kontrolu pre počet stĺpcov v jednotlivých
					// číselníkoch (projekty 5, aktivity 3, pracovníci 6)
					// Z dôvodu optimalizácie stačí skontrolovať prvý riadok.
					if (this instanceof CProjectUploadProcess) {
						if (values.length != 5) {
							Logger.getLogger(this.getClass()).info("Project upload error. --> " + tmp);
							response.setAdditionalInfo(tmp);
							response.setResult(IUploadConstant.UPLOAD_RESULT_ERR2);
							return response;
						} else {
							result = writeItemData(values, this.objectIds);
						}
					} else if (this instanceof CActivitytUploadProcess) {
						if (values.length != 3) {
							Logger.getLogger(this.getClass()).info("Activity upload error. --> " + tmp);
							response.setAdditionalInfo(tmp);
							response.setResult(IUploadConstant.UPLOAD_RESULT_ERR2);
							return response;
						} else {
							result = writeItemData(values, this.objectIds);
						}
					} else if (this instanceof CUserUploadProcess) {
						if (values.length != 6) {
							Logger.getLogger(this.getClass()).info("User upload error. --> " + tmp);
							response.setAdditionalInfo(tmp);
							response.setResult(IUploadConstant.UPLOAD_RESULT_ERR2);
							return response;
						} else {
							result = writeItemData(values, this.objectIds);
						}
					}
					// End SED-357 - Doplnenie kontroly pri načítaní číselníkov

					if (IUploadConstant.UPLOAD_RESULT_OK.equals(result)) {
						continue;
					} else {
						if (!this.objectIds.isEmpty()) {
							postProcessingInErrorCase(this.objectIds);
						}
						response.setResult(result);
						return response;
					}
				}
			} catch (CSecurityException e) {
				Logger.getLogger(this.getClass()).info(e);
				if (!this.objectIds.isEmpty()) {
					postProcessingInErrorCase(this.objectIds);
				}
				response.setResult(IUploadConstant.UPLOAD_RESULT_ERR1);
				return response;
			}

		}

		response.setResult(IUploadConstant.UPLOAD_RESULT_OK);
		return response;
	}

	public void setObjectIds(List<Long> objectIds) {
		this.objectIds = null;
		this.objectIds = objectIds;
	}

	public List<Long> getObjectIds() {
		return this.objectIds;
	}

	/**
	 * Writes item data
	 * 
	 * @param values    input values
	 * @param objectIds identifiers of the object, that should be modified, but in
	 *                  error case is necessary to set ones status to previous state
	 * @return return process code
	 * @throws Exception in error case
	 */
	protected abstract String writeItemData(Object[] values, List<Long> objectIds) throws CSecurityException;

	protected abstract void preProcessing(List<Long> objectIds) throws CSecurityException;

	protected abstract void postProcessingInErrorCase(List<Long> objectIds) throws CSecurityException;
}
