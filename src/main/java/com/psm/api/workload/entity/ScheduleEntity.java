package com.psm.api.workload.entity;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="Tbl_schedule")
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
public class ScheduleEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "schedule_idx")
	private int scheduleIdx;
	
	@JsonIgnore
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="workload_id", nullable = false, referencedColumnName = "workloadId")
	private WorkloadEntity workloadId;
	
	@Column(nullable = true, columnDefinition = "datetime2")
	private Date fullReplicationStartDate;
	
	@Column(nullable = true, columnDefinition = "datetime2")
	private Date fullReplicationFinishedDate;
	
	@Column(nullable = true, columnDefinition = "datetime2")
	private Date nextFullReplicationDate;
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private int fullReplicationInterval;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String fullReplicationDeletedYn;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String fullWorkingStatus;
	
	@Column(nullable = true, columnDefinition = "datetime2")
	private Date incrementalReplicationStartDate;
	
	@Column(nullable = true, columnDefinition = "datetime2")
	private Date incrementalReplicationFinishedDate;
	
	@Column(nullable = true, columnDefinition = "datetime2")
	private Date nextIncrementalReplicationDate;
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private int incrementalReplicationInterval;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String incrementalReplicationDeletedYn;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String increWorkingStatus;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String workoutUsedYn;
	
	@Column(nullable=true)
	private String workoutType;
	
	@Column(nullable=true)
	private String workoutStartDate;
	
	@Column(nullable=true)
	private String workoutStartTime;
	
	@Column(nullable=true)
	private String workoutEndDate;
	
	@Column(nullable=true)
	private String workoutEndTime;
	
	@Column(nullable=false, columnDefinition = "int default 0")
	private int workoutContinuousTime;
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private int workoutInterval;
	
	@Column(nullable=true)
	private String workoutDetailOption;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String workoutStatus;
	
	@Column(nullable = false, columnDefinition = "int default 0")
	private int scheduleStatus;

	@Column(nullable = true, length = 200)
	private String operationUri;
	
}
