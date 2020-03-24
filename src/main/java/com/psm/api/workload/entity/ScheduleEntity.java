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

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="Tbl_schedule")
@NoArgsConstructor
@DynamicUpdate
@Getter
@Setter
public class ScheduleEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "schedule_idx")
	private int scheduleIdx;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="workload_id", nullable = false, referencedColumnName = "workloadId")
	private WorkloadEntity workloadId;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date fullReplicationStartDate;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date fullReplicationFinishedDate;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date incrementalReplicationStartDate;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date incrementalReplicationFinishedDate;
	
	@Column(nullable = false)
	private int scheduleStatus;
	
	@Column(nullable = false)	//작업의 우선순위 설정
	private String schedulePriority;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date nextFullReplicationDate;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date nextIncrementalReplicationDate;
	
	@Column(nullable = true)
	private int incrementalReplicationInterval;
	
	@Column(nullable = true)
	private int fullReplicationInterval;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String replicationDeletedYn;
	
	@Column(nullable=false, columnDefinition = "char(1) default 'N'")
	private String incrementalDeletedYn;
	
	@Column(nullable = true, length = 200)
	private String operationUri;
	
}
