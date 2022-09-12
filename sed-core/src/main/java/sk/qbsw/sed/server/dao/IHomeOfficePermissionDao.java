package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.codelist.CHomeOfficePermission;

public interface IHomeOfficePermissionDao extends IDao<CHomeOfficePermission> {

	public List<CHomeOfficePermission> findAllPermissionTypes();
}