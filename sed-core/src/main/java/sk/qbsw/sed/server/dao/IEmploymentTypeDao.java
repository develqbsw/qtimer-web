package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.domain.CEmploymentType;

public interface IEmploymentTypeDao extends IDao<CEmploymentType> {

	public List<CEmploymentType> findAllEmploymentTypes();
}
