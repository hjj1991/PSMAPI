package com.psm.api.workload.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;

@Entity
@Table(name="tbl_availableAction")
@DynamicUpdate
@Data
public class AvailableActionEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idx;
	
	@Column(nullable = false)
	private String workloadId;
	
	@Column(nullable = false, length = 200)
	private String uri;
	
	@Column(nullable = false, length = 200)
	private String name;
}
