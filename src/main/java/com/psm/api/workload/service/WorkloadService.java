package com.psm.api.workload.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.psm.api.workload.dto.WorkloadsDto;

public interface WorkloadService {
	HashMap<String, Object> getWorkloadList() throws Exception;

	HashMap<String, Object> postWorkloadAction(String serverHost, String actionUrl) throws Exception;
}
