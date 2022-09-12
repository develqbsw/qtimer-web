package sk.qbsw.sed.server.service.upload;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.service.codelist.IProjectService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.client.ui.screen.codelist.upload.IUploadConstant;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * Class for projects data file content upload process
 * 
 * @author rosenberg
 *
 */
@Service
public class CProjectUploadProcess extends AUploadProcess {
	
	@Autowired
	private IProjectService projectService;

	/**
	 * values[0] - EVIPRO id values[1] - the program name values[2] - allowed for
	 * use? áno/nie values[3] - the program group values[4] - the program code
	 */
	@Override
	protected String writeItemData(Object[] values, List<Long> objectIds) {
		CProjectRecord record = new CProjectRecord();

		try {
			if (values.length < 5 || values[4] == null || "".equals((String) values[4])) {
				return IUploadConstant.UPLOAD_RESULT_ERR2 + "  chybný záznam: " + ((String) values[0]).trim() + " " + ((String) values[1]).trim();
			}
			String prjEviproId = ((String) values[0]).trim();
			String prjName = ((String) values[1]).trim();
			String prjSAvaliable = ((String) values[2]).trim();
			Boolean prjBAvailable = !"nie".equalsIgnoreCase(prjSAvaliable);
			String prjGroup = ((String) values[3]).trim();
			String prjCode = ((String) values[4]).trim();

			// check input params
			if (prjName.length() > 250) {
				return IUploadConstant.UPLOAD_RESULT_ERR2 + "  chybný záznam (názov projektu môže byť dlhý maximálne 250 znakov): " + ((String) values[0]).trim() + " " + ((String) values[1]).trim();
			}
			if (prjGroup.length() > 50) {
				return IUploadConstant.UPLOAD_RESULT_ERR2 + "  chybný záznam (názov skupiny projektu môže byť dlhý maximálne 50 znakov): " + ((String) values[0]).trim() + " "
						+ ((String) values[1]).trim();
			}
			if (prjCode.length() > 50) {
				return IUploadConstant.UPLOAD_RESULT_ERR2 + "  chybný záznam (kód projektu môže byť dlhý maximálne 50 znakov): " + ((String) values[0]).trim() + " " + ((String) values[1]).trim();
			}

			record.setName(prjName);
			record.setActive(prjBAvailable);
			record.setGroup(prjGroup); // nepozadujeme, ale nutne pre export sumarneho vykazu
			record.setCode(prjCode); // nepozadujeme, ale nutne pre export sumarneho vykazu

			// not mandatory
			record.setNote("Pozn.:" + prjGroup + " " + prjName);
			record.setOrder(Integer.getInteger(prjEviproId)); // za interne poradie sa berie id z evipro

			this.projectService.addOrUpdate(record, objectIds);
		} catch (NumberFormatException e) {
			return IUploadConstant.UPLOAD_RESULT_ERR2 + "  chybný záznam: " + ((String) values[0]).trim() + " " + ((String) values[1]).trim();
		} catch (CBusinessException e) {

			// problem s duplicitnym projektom pre viacere organizacie je rieseny na urovni dao metody, hladajucej projekt podla mena a nalezitosti k organizacii
			if (e.getMessage().indexOf(CClientExceptionsMessages.PROJECT_ALREADY_EXISTS) > -1) {
				// nothing to do! - uz je tam a nic neriesime
			} else {
				return IUploadConstant.UPLOAD_RESULT_ERR4;
			}
		}

		return IUploadConstant.UPLOAD_RESULT_OK;
	}

	@Override
	protected void preProcessing(List<Long> objectIds) throws CSecurityException {
		setObjectIds(this.projectService.invalidateLoggedClientProjects());
	}

	@Override
	protected void postProcessingInErrorCase(List<Long> objectIds) throws CSecurityException {
		this.projectService.validateSelectedProjects(getObjectIds());
	}
}
