package com.psm.api.workload.service;

import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.psm.api.workload.dto.WorkloadsDto;

public interface WorkloadService {
	WorkloadsDto getBoardList() throws Exception;
}
