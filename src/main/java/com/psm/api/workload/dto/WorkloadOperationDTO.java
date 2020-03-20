package com.psm.api.workload.dto;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WorkloadOperationDTO {
	@JsonProperty("FinishedAt")
	String finishedAt;
	@JsonProperty("IsAborted")
	String isAborted;
	@JsonProperty("IsFinished")
	String isFinished;
	@JsonProperty("IsStuck")
	String isStuck;
	@JsonProperty("IsSucceeded")
	String isSucceeded;
	@JsonProperty("StartedAt")
	String startedAt;
	@JsonProperty("OperationResults")
	List<HashMap<String, Object>> operationResults;
	@JsonProperty("OperationType")
	String operationType;
	@JsonProperty("SubOperations")
	List<HashMap<String, Object>> subOperations;
}
