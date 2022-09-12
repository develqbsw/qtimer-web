package sk.qbsw.sed.client.model.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Record concerning logged user
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
public class CLoggedUserRecord implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1371278848371031100L;

	/**
	 * 
	 */

	private CClientInfo clientInfo;

	private String login;

	private String name;

	private String position;

	private String roleName;

	private String roleCode;

	private List<Long> roles = new ArrayList<>();

	private String surname;

	private Long userId;

	private String autoLoginToken;

	private boolean allowedAlertnessWork;

	private Long homeOfficePermission;

	private String jiraAccessToken;

	private String securityToken;

	private Long userPhotoId;

	private Integer tableRows;
	/**
	 * Flag if process of edit time is allowed for the user
	 */
	private Boolean editTime;

	private String language;

	private List<Long> userFavourites = new ArrayList<>();

	private List<Long> directSubordinates = new ArrayList<>();

	private Double vacation;
	private Double vacationNextYear;

	private Boolean jiraTokenGeneration;
	
	public Boolean getEditTime() {
		return editTime;
	}

	public void setEditTime(Boolean editTime) {
		this.editTime = editTime;
	}

	public boolean isAllowedAlertnessWork() {
		return allowedAlertnessWork;
	}

	public void setAllowedAlertnessWork(boolean allowedAlertnessWork) {
		this.allowedAlertnessWork = allowedAlertnessWork;
	}

	public Long getHomeOfficePermission() {
		return homeOfficePermission;
	}

	public void setHomeOfficePermission(Long homeOfficePermission) {
		this.homeOfficePermission = homeOfficePermission;
	}

	public CClientInfo getClientInfo() {
		return this.clientInfo;
	}

	public void setClientInfo(final CClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	public void addRole(final Long role) {
		this.roles.add(role);
	}

	public boolean containsRole(final Long roleToCheck) {
		return this.roles.contains(roleToCheck);
	}

	public String getLogin() {
		return this.login;
	}

	public String getName() {
		return this.name;
	}

	public String getPosition() {
		return this.position;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public List<Long> getRoles() {
		return this.roles;
	}

	public String getSurname() {
		return this.surname;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPosition(final String position) {
		this.position = position;
	}

	public void setRoleName(final String roleName) {
		this.roleName = roleName;
	}

	public void setRoles(final List<Long> role) {
		this.roles = role;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(final String roleCode) {
		this.roleCode = roleCode;
	}

	public String getAutoLoginToken() {
		return this.autoLoginToken;
	}

	public void setAutoLoginToken(final String autoLoginToken) {
		this.autoLoginToken = autoLoginToken;
	}

	public String getJiraAccessToken() {
		return jiraAccessToken;
	}

	public void setJiraAccessToken(String jiraAccessToken) {
		this.jiraAccessToken = jiraAccessToken;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public Long getUserPhotoId() {
		return userPhotoId;
	}

	public void setUserPhotoId(Long userPhotoId) {
		this.userPhotoId = userPhotoId;
	}

	public Integer getTableRows() {
		return tableRows;
	}

	public void setTableRows(Integer tableRows) {
		this.tableRows = tableRows;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Long> getUserFavourites() {
		return userFavourites;
	}

	public void setUserFavourites(List<Long> userFavourites) {
		this.userFavourites = userFavourites;
	}

	public List<Long> getDirectSubordinates() {
		return directSubordinates;
	}

	public void setDirectSubordinates(List<Long> directSubordinates) {
		this.directSubordinates = directSubordinates;
	}

	public Double getVacation() {
		return vacation;
	}

	public void setVacation(Double vacation) {
		this.vacation = vacation;
	}

	public Double getVacationNextYear() {
		return vacationNextYear;
	}

	public void setVacationNextYear(Double vacationNextYear) {
		this.vacationNextYear = vacationNextYear;
	}
	

	public Boolean getJiraTokenGeneration() {
		return jiraTokenGeneration;
	}

	public void setJiraTokenGeneration(Boolean jiraTokenGeneration) {
		this.jiraTokenGeneration = jiraTokenGeneration;
	}
	
}
