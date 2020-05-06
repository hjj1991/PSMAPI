package com.psm.api.workload.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.workload.dto.ResponseWorkloadListDto;
import com.psm.api.workload.entity.AvailableActionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="Tbl_workload")
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Getter
@Setter
public class WorkloadEntity extends ResponseWorkloadListDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "workload_idx")
	private int workloadIdx;
	
	@Column(nullable = false, unique = true)
	private String workloadId;	
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name="company_idx", nullable = true)
	private CompanyEntity companyIdx;
	
	@Column(nullable = false)
	private String serverHost;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "workloadId")
	private Collection<ScheduleEntity> scheduleList;
	
	
	@Column(nullable = true)
	private String targetId;
	
	@Column(nullable = true)
	private String currentState;
	
	@Column(nullable = true)
	private String machineName;
	
	@Column(nullable = true)
	private String name;
	
	@Column(nullable = true)
	private String online;
	
	@Column(nullable = true)
	private String operatingSystem;

	@Column(nullable = true)
	private String operatingSystemVersion;
	
	@Column(nullable = true)
	private String servicePack;
	
	@Column(nullable = true)
	private String sourceMachinId;
	
	@Column(nullable = true)
	private String userName;
	
	@Column(nullable = true)
	private String discoveryAddress;
	
	@Column(nullable = true)
	private String areBBTollsInstalled;
	
	@Column(nullable = true)
	private String readyToCopySnapshotName;
	
	@Column(nullable = true)
	private String canDeleteVm;
	
	@Column(nullable = true)
	private String canRemoveSource;
	
	@Column(nullable = true)
	private String canRemoveBBT;
	
	@Column(nullable = true)
	private String runFailoverOnReplicationSuccess;
	
	@Column(nullable = true)
	private String isRemoteWorkload;
	
	@Column(nullable = true)
	private String isWindowsCluster;
	
	@Column(nullable = true)
	private String lastFullOn;
	
	@Column(nullable = true)
	private String lastIncrementalOn;
	
	@Column(nullable = true)
	private String lastTestedFailoverOn;
	
	@Column(nullable = true)
	private String lastUpdated;
	
	@Column(nullable = true)
	private String failoverMachineId;
	
	@Column(nullable = true, columnDefinition = "varchar(255) default ''")
	private String nextFullOn;
	
	@Column(nullable = true, columnDefinition = "varchar(255) default ''")
	private String nextIncrementalOn;
	
	@Column(nullable = true)
	private String onlineStatus;
	
	@Column(nullable = true)
	private String protectionLevel;
	
	@Column(nullable = true)
	private String protectionState;
	
	@Column(nullable = true)
	private String targetPRO;
	
	@Column(nullable = true)
	private String workflowStep;
	
	@Column(nullable = true)
	private String workloadLifecycle;
	
	@Column(nullable = true)
	private String workloadGroupId;
	
	@Column(nullable = true)
	private String replicationScheduleStatus;
	
	@Column(nullable = true)
	private String sourceMachineControllerAlias;
	
	@Column(nullable = true)
	private String prepareForFailoverConfigurationUri;
	
	@Column(nullable = true)
	private String scheduleActive;
	
	@Column(nullable = true)
	private String schedulesUri;
	
	@Column(nullable = true, columnDefinition = "varchar(100) default ''")
	private String tag;
	
	@Column(nullable = true)
	private String testCutoverMarkedSuccessful;
	
	@Column(nullable = true)
	private String testFailoverConfigurationUri;
	
	@Column(nullable = true)
	private String tmData;
	
	@Column(nullable = true)
	private String windowsServiceUri;
	
	@Column(nullable = true)
	private String workloadConfigurationUri;
	
	@Column(nullable = true, length = 200)
	private String operationUri;
	
	@Column(nullable = false, columnDefinition = "datetime2 default getdate()")
	private Date syncDate;
	
}
