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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.psm.api.company.entity.CompanyEntity;

import lombok.Data;

@Entity
@Table(name="Tbl_workload")
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Data
public class WorkloadEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "workload_idx")
	private int workloadIdx;
	
	@Column(nullable = false, unique = true)
	private String workloadId;	
	
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="company_idx", nullable = true)
	private CompanyEntity companyIdx;
	
	@Column(nullable = false)
	private String targetId;
	
	@Column(nullable = false)
	private String currentState;
	
	@Column(nullable = false)
	private String machineName;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String online;
	
	@Column(nullable = false)
	private String operatingSystem;

	@Column(nullable = false)
	private String operatingSystemVersion;
	
	@Column(nullable = false)
	private String servicePack;
	
	@Column(nullable = false)
	private String sourceMachinId;
	
	@Column(nullable = false)
	private String userName;
	
	@Column(nullable = false)
	private String discoveryAddress;
	
	@Column(nullable = false)
	private String areBBTollsInstalled;
	
	@Column(nullable = true)
	private String readyToCopySnapshotName;
	
	@Column(nullable = false)
	private String canDeleteVm;
	
	@Column(nullable = false)
	private String canRemoveSource;
	
	@Column(nullable = false)
	private String canRemoveBBT;
	
	@Column(nullable = false)
	private String runFailoverOnReplicationSuccess;
	
	@Column(nullable = false)
	private String isRemoteWorkload;
	
	@Column(nullable = false)
	private String isWindowsCluster;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date lastFullOn;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date lastIncrementalOn;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date lastTestedFailoverOn;
	
	@Column(nullable = false, columnDefinition = "datetime2")
	private Date lastUpdated;
	
	@Column(nullable = false)
	private String failoverMachineId;
	
	@Column(nullable = false, columnDefinition = "varchar(255) default ''")
	private String nextFullOn;
	
	@Column(nullable = false, columnDefinition = "varchar(255) default ''")
	private String nextIncrementalOn;
	
	@Column(nullable = false)
	private String onlineStatus;
	
	@Column(nullable = false)
	private String protectionLevel;
	
	@Column(nullable = false)
	private String protectionState;
	
	@Column(nullable = false)
	private String targetPRO;
	
	@Column(nullable = true)
	private String workflowStep;
	
	@Column(nullable = false)
	private String workloadLifecycle;
	
	@Column(nullable = false)
	private String workloadGroupId;
	
	@Column(nullable = false)
	private String replicationScheduleStatus;
	
	@Column(nullable = false)
	private String sourceMachineControllerAlias;
	
	@Column(nullable = false)
	private String prepareForFailoverConfigurationUri;
	
	@Column(nullable = false)
	private String scheduleActive;
	
	@Column(nullable = false)
	private String schedulesUri;
	
	@Column(nullable = false, columnDefinition = "varchar(100) default ''")
	private String tag;
	
	@Column(nullable = false)
	private String testCutoverMarkedSuccessful;
	
	@Column(nullable = false)
	private String testFailoverConfigurationUri;
	
	@Column(nullable = true)
	private String tmData;
	
	@Column(nullable = true)
	private String windowsServiceUri;
	
	@Column(nullable = false)
	private String workloadConfigurationUri;
	
}
