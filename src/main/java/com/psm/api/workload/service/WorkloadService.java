package com.psm.api.workload.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.workload.dto.FindWorkloadDto;
import com.psm.api.workload.dto.RequestScheduleDTO;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.entity.ScheduleEntity;

public interface WorkloadService {
	HashMap<String, Object> getWorkloadList(FindWorkloadDto findWorkloadDto, String authToken) throws Exception;

	HashMap<String, Object> postWorkloadAction(String serverHost, String actionUrl, String workloadId) throws Exception;

	void asyncWorkload(ApiServerListEntity apiserverInfo);
	
	void scheduleWorkloadAction(ScheduleEntity scheduleEntity) throws Exception;

	HashMap<String, Object> postWorkloadSchedule(RequestScheduleDTO requestScheduleDTO) throws Exception;

	boolean syncWorkload(String serverHost, String workloadId) throws Exception;
}
