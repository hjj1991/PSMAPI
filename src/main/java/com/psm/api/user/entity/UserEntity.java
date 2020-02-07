
package com.psm.api.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tbl_user")
@NoArgsConstructor
@Data
public class UserEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userIdx;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="company_idx")
	private CompanyEntity companyIdx;
	
	@Column(nullable = false, unique = true, length = 60)
	private String useryId;
	
	@Column(nullable = false, length = 60)
	private String userName;
	
	@Column(nullable = false, length = 100)
	private String userPw;
	
	@Column(nullable = false, length = 20)
	private String userTel;
	
	@Column(nullable = false, length = 20)
	private String userPhone;
	
	@Column(nullable = false, columnDefinition = "datetime default getdate()")
	private Date createdDatetime;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String deletedYn;
	
}
