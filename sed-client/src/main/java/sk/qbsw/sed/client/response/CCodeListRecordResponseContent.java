package sk.qbsw.sed.client.response;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

public class CCodeListRecordResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	@Expose
	@NotNull
	private List<CCodeListRecord> codeListRecord;

	public List<CCodeListRecord> getCodeListRecord() {
		return codeListRecord;
	}

	public void setCodeListRecord(List<CCodeListRecord> codeListRecord) {
		this.codeListRecord = codeListRecord;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
