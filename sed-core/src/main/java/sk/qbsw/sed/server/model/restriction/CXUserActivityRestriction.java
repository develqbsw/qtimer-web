package sk.qbsw.sed.server.model.restriction;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Cross mapping table (public.t_user_activity_restriction) between
 * public.t_user and public.t_restriction_group
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_user_activity_restriction", sequenceName = "s_user_activity_restriction", allocationSize = 1)
@Table(schema = "public", name = "t_user_activity_restriction")
public class CXUserActivityRestriction implements Serializable {

	@Id
	@GeneratedValue(generator = "s_user_activity_restriction", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user", nullable = false)
	CUser user;

	@Where(clause = "c_flag_valid = 'true'")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_restriction_group", nullable = false)
	CActivityRestrictionGroup restrictionGroup;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CUser getUser() {
		return user;
	}

	public void setUser(CUser user) {
		this.user = user;
	}

	public CActivityRestrictionGroup getRestrictionGroup() {
		return restrictionGroup;
	}

	public void setRestrictionGroup(CActivityRestrictionGroup restrictionGroup) {
		this.restrictionGroup = restrictionGroup;
	}
}
