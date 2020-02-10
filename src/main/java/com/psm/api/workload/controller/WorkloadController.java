package com.psm.api.workload.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psm.api.common.response.SingleResult;
import com.psm.api.common.response.service.ResponseService;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.service.WorkloadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = { "1. workload" })
@RequiredArgsConstructor
@RestController // 결과값을 JSON으로 출력합니다.
@RequestMapping(value = "/v1")
public class WorkloadController {
	
	@Autowired
	private WorkloadService workloadService;
	
	@Autowired
	private ResponseService responseService;
	
	@ApiOperation(value = "워크로드 조회", notes = "모든 워크로드를 조회한다")
	@GetMapping(value = "/workload")
	public SingleResult<WorkloadsDto> getWorkloadList() throws Exception {
		// 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
		HashMap<String, Object> result = new HashMap<>();
		result = workloadService.getWorkloadList();
		if(result.get("status") == "200") {
			return responseService.getSingleResult((WorkloadsDto)result.get("data"));
		}else {
			return responseService.getSingleResult(null, Integer.parseInt(result.get("status").toString()), (String)result.get("resultMsg"), false);
		}
		
	}
}
