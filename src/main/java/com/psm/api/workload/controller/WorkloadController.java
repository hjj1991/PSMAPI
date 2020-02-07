package com.psm.api.workload.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.service.WorkloadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = { "1. workload" })
@RequiredArgsConstructor
@RestController // 결과값을 JSON으로 출력합니다.
@RequestMapping(value = "/v1")
public class WorkloadController {
	
	@Autowired
	private WorkloadService workloadService;
	
	
	@ApiOperation(value = "워크로드 조회", notes = "모든 워크로드를 조회한다")
	@GetMapping(value = "/workload")
	public ResponseEntity<?> getWorkloadList() throws Exception {
		// 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
		
		WorkloadsDto value = workloadService.getBoardList();
		return new ResponseEntity<>(value, HttpStatus.OK);
	}
}
