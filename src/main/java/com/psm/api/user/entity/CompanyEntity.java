package com.psm.api.user.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tbl_company")
@NoArgsConstructor
@Data
public class CompanyEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "company_idx")
	private int companyIdx;
	
	@Column(nullable = false, unique = true, length = 60)
	private String companyId;
	
	@Column(nullable = false, length = 60)
	private String companyName;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "companyIdx")
	private Collection<UserEntity> user;
	
	@Column(nullable = false, columnDefinition = "datetime default getdate()")
	private Date createdDatetime;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String deletedYn;
	
}
