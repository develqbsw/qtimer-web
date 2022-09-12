package sk.qbsw.sed.server.model.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_user_photo", sequenceName = "s_user_photo", allocationSize = 1)
@Table(schema = "public", name = "t_user_photo")
public class CUserPhoto implements Serializable {

	@Id
	@GeneratedValue(generator = "s_user_photo", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_image", nullable = true)
	private Byte[] photo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(Byte[] photo) {
		this.photo = photo;
	}
}
