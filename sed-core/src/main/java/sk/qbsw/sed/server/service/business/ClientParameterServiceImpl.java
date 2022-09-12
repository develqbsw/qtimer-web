package sk.qbsw.sed.server.service.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.params.CParameter;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.service.business.IClientParameterService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.dao.IParameterDao;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.params.CParameterEntity;

/**
 * Service for management client parameters
 * 
 * @author Ladislav Rosenberg
 * @version 1.0
 * @since 1.6.0
 */
@Service(value = "clientParameterService")
public class ClientParameterServiceImpl implements IClientParameterService {

	@Autowired
	public IUserDao userDao;

	@Autowired
	public IParameterDao parameterDao;

	/**
	 * @see IClientParameterService#addClientParameter(Long, String, String)
	 */
	@Transactional
	public void addClientParameter(Long clientId, String name, String stringValue) throws CBusinessException, CSecurityException {
		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();
		final CUser loggedUser = this.userDao.findById(loggedUserRecord.getUserId());

		final CParameterEntity parameterEntity = new CParameterEntity();

		CParameterEntity entity = new CParameterEntity();
		entity.setName(name);
		entity.setStringValue(stringValue);
		entity.setClient(loggedUser.getClient());

		this.parameterDao.saveOrUpdate(parameterEntity);
	}

	/**
	 * @see IClientParameterService#modifyClientParameter(Long, String, String)
	 */
	@Transactional
	public void modifyClientParameter(Long parameterId, String name, String stringValue) throws CBusinessException, CSecurityException {
		final CLoggedUserRecord loggedUserRecord = CServletSessionUtils.getLoggedUser();
		final CUser loggedUser = this.userDao.findById(loggedUserRecord.getUserId());

		CParameterEntity entity = this.parameterDao.findById(parameterId);
		entity.setName(name);
		entity.setStringValue(stringValue);
		entity.setClient(loggedUser.getClient());

		this.parameterDao.saveOrUpdate(entity);
	}

	/**
	 * @see IClientParameterService#getClientParameter(Long, String)
	 */
	@Transactional
	public CParameter getClientParameter(Long clientId, String name) throws CBusinessException {
		List<CParameterEntity> parameters = this.parameterDao.findByNameForClient(clientId, name);
		if (parameters != null && parameters.size() > 0) {
			return convertToScreenModel(parameters.get(0));
		}
		return null;
	}

	/**
	 * @see IClientParameterService#getClientParameters(Long)
	 */
	@Transactional
	public List<CParameter> getClientParameters(Long clientId) throws CBusinessException {
		List<CParameter> clientParameters = new ArrayList<>();
		List<CParameterEntity> serverParameters = this.parameterDao.findAll(clientId);

		if (serverParameters != null && serverParameters.size() > 0) {
			for (CParameterEntity serverParameter : serverParameters) {
				clientParameters.add(convertToScreenModel(serverParameter));
			}
		}

		return clientParameters;
	}

	/**
	 * Converts database entity to client model
	 * 
	 * @param entity input database entity
	 * @return client model
	 */
	private CParameter convertToScreenModel(final CParameterEntity entity) {
		final CParameter model = new CParameter();
		model.setId(entity.getId());
		model.setName(entity.getName());
		model.setClientId(entity.getClient().getId());
		model.setStringValue(entity.getStringValue());

		return model;
	}
}
