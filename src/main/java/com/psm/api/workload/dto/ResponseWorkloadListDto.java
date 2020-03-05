package com.psm.api.workload.dto;

import java.util.Date;
import java.util.List;

import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.workload.entity.AvailableActionEntity;
import com.psm.api.workload.entity.WorkloadEntity;

import lombok.Data;

@Data
public class ResponseWorkloadListDto {

	private List<AvailableActionEntity> availableActionList;
}
