package com.psm.api.apiserver.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.EntityGraph;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.psm.api.company.entity.CompanyEntity;

import lombok.Data;

@Entity
@Table(name="Tbl_apiserver_list")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@DynamicUpdate
@Data
public class ApiServerListEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "apiserver_idx")
	private int apiserverIdx;
	
	@Column(nullable = false)
	private String userNameToAccessProtectServer;
	
	@Column(nullable = false)
	private String passwordToAccessProtectServer;
	
	@Column(nullable = false)
	private String domainNameToAccessProtectServer;
	
	@Column(nullable = false)
	private String serverHost;
	
	@Column(nullable = false, columnDefinition = "datetime2 default getdate()", insertable = false, updatable = false)
	private Date createdDate;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'", insertable = false)
	private String deletedYn;
	
//	@JsonIgnore
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name="company_idx", nullable = true)
	private CompanyEntity companyIdx;
	
}
