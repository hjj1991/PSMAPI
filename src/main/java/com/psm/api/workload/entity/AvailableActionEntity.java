package com.psm.api.workload.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tbl_availableAction")
@DynamicUpdate
@Getter
@Setter
public class AvailableActionEntity {
	
	@Id
	@JsonIgnore
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "idx")
	private int idx;
	
	@Column(nullable = false, name="workloadId")
	private String workloadId;
	
	@Column(nullable = false, length = 200)
	private String uri;
	
	@Column(nullable = false, length = 200)
	private String name;
	

}
