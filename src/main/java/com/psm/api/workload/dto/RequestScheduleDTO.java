package com.psm.api.workload.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
@Data
public class RequestScheduleDTO {
	@NotNull
	private String workloadId;
	private int scheduleStatus;
	private String schedulePriority;
	private String nextFullReplicationDate;
	private String nextIncrementalReplicationDate;
	private int incrementalReplicationInterval;
	private int fullReplicationInterval;
	private String fullReplicationDeletedYn;
	private String incrementalReplicationDeletedYn;

}
