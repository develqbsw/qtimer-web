package sk.qbsw.sed.server.dao;

import java.util.List;

import sk.qbsw.core.dao.IDao;
import sk.qbsw.sed.server.model.domain.CZone;

public interface IZoneDao extends IDao<CZone> {

	public List<CZone> getZones(Long clientId);
}
