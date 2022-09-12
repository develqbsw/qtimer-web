package sk.qbsw.sed.server.ldap;

import org.springframework.stereotype.Service;

/**
 * The Class CLdapAuthenticationConfigurator.
 * 
 * @author Dalibor Rak
 * @author Tomas Lauro
 * 
 * @version 1.11.8
 * @since 1.6.0
 */
@Service("ldapAuthenticationConfigurator")
public class CLdapAuthenticationConfigurator implements ILdapAuthenticationConfigurator {
	/** The ldap server name. */
	private String serverName;

	/** The ldap server port. */
	private int serverPort;

	/** The flag indicates usage of ssl. */
	private boolean useSslFlag;

	/** The ldap dn of an user to authenticate with ldap server. */
	private String userDn;

	/** The ldap user password. */
	private String userPassword;

	/** The ldap user organization. */
	private Long userOrganizationId;

	/** The ldap user search base dns. */
	private String userSearchBaseDn;

	/** The user search filter. */
	private String userSearchFilter = "(&(cn=%s))";

	private String serverName2;

	private boolean useSslFlag2;

	private int serverPort2;

	/**
	 * Instantiates a new c ldap authentication configurator.
	 */
	public CLdapAuthenticationConfigurator() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.service.ILdapAuthenticationConfigurator#
	 * getServerName()
	 */

	public String getServerName() {
		return serverName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#
	 * setServerName(java.lang.String)
	 */

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.service.ILdapAuthenticationConfigurator#
	 * getServerPort()
	 */

	public int getServerPort() {
		return serverPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#
	 * setServerPort(int)
	 */

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.service.ILdapAuthenticationConfigurator#
	 * getUseSslFlag()
	 */

	public boolean getUseSslFlag() {
		return useSslFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#
	 * setUseSslFlag(boolean)
	 */

	public void setUseSslFlag(boolean useSslFlag) {
		this.useSslFlag = useSslFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sk.qbsw.core.security.service.ILdapAuthenticationConfigurator#getUserDn()
	 */

	public String getUserDn() {
		return userDn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#setUserDn
	 * (java.lang.String)
	 */

	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.service.ILdapAuthenticationConfigurator#
	 * getUserPassword()
	 */

	public String getUserPassword() {
		return userPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#
	 * setUserPassword(java.lang.String)
	 */

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.service.ILdapAuthenticationConfigurator#
	 * getUserOrganizationId()
	 */

	public Long getUserOrganizationId() {
		return userOrganizationId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#
	 * setUserOrganizationId(java.lang.Long)
	 */

	public void setUserOrganizationId(Long userOrganizationId) {
		this.userOrganizationId = userOrganizationId;
	}

	public String getUserSearchBaseDn() {
		return userSearchBaseDn;
	}

	public void setUserSearchBaseDn(String userSearchBaseDn) {
		this.userSearchBaseDn = userSearchBaseDn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#
	 * getUserSearchFilter()
	 */

	public String getUserSearchFilter() {
		return userSearchFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sk.qbsw.core.security.model.jmx.ILdapAuthenticationConfigurator#
	 * setUserSearchFilter(java.lang.String)
	 */

	public void setUserSearchFilter(String userSearchFilter) {
		this.userSearchFilter = userSearchFilter;
	}

	public String getServerName2() {
		return serverName2;
	}

	public void setServerName2(String serverName2) {
		this.serverName2 = serverName2;
	}

	public boolean getUseSslFlag2() {
		return useSslFlag2;
	}

	public void setUseSslFlag2(boolean useSslFlag2) {
		this.useSslFlag2 = useSslFlag2;
	}

	public int getServerPort2() {
		return serverPort2;
	}

	public void setServerPort2(int serverPort2) {
		this.serverPort2 = serverPort2;
	}
}
