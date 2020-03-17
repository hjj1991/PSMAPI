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
import lombok.NoArgsConstructor;

@Entity
@Table(name="Tbl_schedule")
@NoArgsConstructor
@DynamicUpdate
@Data
public class ScheduleEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "schedule_idx")
	private int scheduleIdx;
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="workload_id", nullable = false, referencedColumnName = "workloadId", unique = true)
	private WorkloadEntity workloadId;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date fullReplicationStartdate;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date incrementalReplicationStartdate;
	
	@Column(nullable = false)
	private String scheduleStatus;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date nextFullReplicationDate;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date nextIncrementalReplicationDate;
	
	@Column(nullable = true)
	private String incrementalReplicationInterval;
	
	@Column(nullable = true)
	private String fullReplicationInterval;
	
}
