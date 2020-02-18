package com.psm.api.company.entity;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.workload.entity.ApiServerListEntity;
import com.psm.api.workload.entity.WorkloadEntity;

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
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "companyIdx")
	private Collection<UserEntity> user;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "companyIdx")
	private Collection<WorkloadEntity> workload;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "companyIdx")
	private Collection<ApiServerListEntity> apiServerList;
	
	@Column(nullable = false, columnDefinition = "datetime2 default getdate()")
	private Date createdDate;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String deletedYn;
	
}
