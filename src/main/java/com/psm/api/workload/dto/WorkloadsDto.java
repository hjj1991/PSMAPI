package com.psm.api.workload.dto;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WorkloadsDto {
	@JsonProperty("Uri")
	String uri;
	@JsonProperty("Workloads")
	List<WorkloadDto> workloads;
}
