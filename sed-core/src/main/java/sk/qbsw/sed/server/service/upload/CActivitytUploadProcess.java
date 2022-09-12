package sk.qbsw.sed.server.service.upload;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.service.codelist.IActivityService;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.client.ui.screen.codelist.upload.IUploadConstant;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * 
 * @author rosenberg
 *
 */
@Service
public class CActivitytUploadProcess extends AUploadProcess {

	@Autowired
	private IActivityService activityService;

	/**
	 * values[0] - the activity code values[1] - the activity name values[2] - the
	 * activity working flag: áno/nie
	 */
	@Override
	protected String writeItemData(Object[] values, List<Long> objectIds) {
		CActivityRecord record = new CActivityRecord();

		try {
			if (values.length < 2 || values[1] == null || "".equals((String) values[1])) {
				return IUploadConstant.UPLOAD_RESULT_ERR2 + " chybný záznam: " + ((String) values[1]).trim();
			}

			Integer id = Integer.valueOf(((String) values[0]).trim());
			String name = ((String) values[1]).trim();
			Boolean working = Boolean.TRUE;
			if (values.length > 2) {
				working = !"nie".equals(((String) values[2]).trim());
			}

			record.setOrder(id);
			record.setName(name);
			record.setWorking(working);

			record.setNote("Pozn.: " + name);
			record.setActive(Boolean.TRUE); // default value

			this.activityService.addOrUpdate(record);
		} catch (NumberFormatException e) {
			return IUploadConstant.UPLOAD_RESULT_ERR2;
		} catch (ArrayIndexOutOfBoundsException e) {
			Logger.getLogger(this.getClass()).info(e);
			return IUploadConstant.UPLOAD_RESULT_ERR2;
		} catch (CBusinessException e) {
			// problem s duplicitnou aktivitou pre viacere organizacie
			// je rieseny na urovni dao metody,
			// hladajucej aktivitu podla mena a nalezitosti k organizacii
			if (e.getMessage().indexOf(CClientExceptionsMessages.ACTIVITY_ALREADY_EXISTS) > -1) {
				// nothing to do! - je tam a nic neriesime
			} else {
				return IUploadConstant.UPLOAD_RESULT_ERR4;
			}
		}

		return IUploadConstant.UPLOAD_RESULT_OK;
	}

	@Override
	protected void postProcessingInErrorCase(List<Long> objectIds) throws CSecurityException {
		// nothing to do
	}

	@Override
	protected void preProcessing(List<Long> objectIds) throws CSecurityException {
		// nothing to do
	}
}
