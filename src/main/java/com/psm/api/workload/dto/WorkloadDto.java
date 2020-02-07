package com.psm.api.workload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WorkloadDto {
	@JsonProperty("Uri")
	String uri;
	@JsonProperty("AvailableTransitions")
	String availableTransitions;
	@JsonProperty("ContainerUri")
	String containerUri;
	@JsonProperty("CurrentState")
	String currentState;
	@JsonProperty("FailbackConfigurationUri")
	String failbackConfigurationUri;
	@JsonProperty("FailoverConfigurationUri")
	String failoverConfigurationUri;
	@JsonProperty("FailoverVMAccessUri")
	String failoverVMAccessUri;
	@JsonProperty("LastOperationUri")
	String lastOperationUri;
	@JsonProperty("LinuxDaemonsUri")
	String linuxDaemonsUri;
	@JsonProperty("MachineName")
	String machineName;
	@JsonProperty("Name")
	String name;
	@JsonProperty("Online")
	String online;
	@JsonProperty("OperatingSystem")
	String operatingSystem;
	@JsonProperty("OperatingSystemVersion")
	String operatingSystemVersion;
	@JsonProperty("Parameters")
	String parameters;
	@JsonProperty("PrepareForFailoverConfigurationUri")
	String prepareForFailoverConfigurationUri;
	@JsonProperty("RecoveryPoints")
	String recoveryPoints;
	@JsonProperty("ScheduleActive")
	String scheduleActive;
	@JsonProperty("SchedulesUri")
	String schedulesUri;
	@JsonProperty("Status")
	String status;
	@JsonProperty("Tag")
	String tag;
	@JsonProperty("TestCutoverMarkedSuccessful")
	String testCutoverMarkedSuccessful;
	@JsonProperty("TestFailoverConfigurationUri")
	String testFailoverConfigurationUri;
	@JsonProperty("TmData")
	String tmData;
	@JsonProperty("WindowsServicesUri")
	String windowsServicesUri;
	@JsonProperty("WorkloadConfigurationUri")
	String workloadConfigurationUri;
}
