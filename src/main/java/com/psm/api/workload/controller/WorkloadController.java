package com.psm.api.workload.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psm.api.common.response.SingleResult;
import com.psm.api.common.response.service.ResponseService;
import com.psm.api.user.dto.FindUserDto;
import com.psm.api.workload.dto.FindWorkloadDto;
import com.psm.api.workload.dto.RequestScheduleDTO;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.service.WorkloadService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = { "2. workload" })
@RequiredArgsConstructor
@RestController // 결과값을 JSON으로 출력합니다.
@RequestMapping(value = "/v1")
public class WorkloadController {
	
	@Autowired
	private WorkloadService workloadService;
	
	@Autowired
	private ResponseService responseService;

	@ApiImplicitParams({
		@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header") })
	@ApiOperation(value = "워크로드 조회", notes = "모든 워크로드를 조회한다")
	@GetMapping(value = "/workload")
	public SingleResult<?> getWorkloadList(FindWorkloadDto findWorkloadDto, @RequestHeader("X_AUTH_TOKEN") String authToken) throws Exception {
		// 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
		System.out.println(findWorkloadDto);
		System.out.println("안녕");
		HashMap<String, Object> result = new HashMap<>();
		result = workloadService.getWorkloadList(findWorkloadDto, authToken);
		if(result.get("status") == "200") {
			return responseService.getSingleResult(result.get("data"));
		}else {
			return responseService.getSingleResult(null, Integer.parseInt(result.get("status").toString()), (String)result.get("resultMsg"), false);
		}
		
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header") })
	@ApiOperation(value = "워크로드 액션", notes = "워크로드 액션진행")
	@PostMapping(value = "/workload/action")
	public SingleResult<?> postWorkloadAction(@RequestBody Map<String, String> param) throws Exception {
		// 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
		HashMap<String, Object> result = new HashMap<>();
		result = workloadService.postWorkloadAction(param.get("serverHost"), param.get("actionUrl"), param.get("workloadId"));
//		if(result.get("status") == "200") {
//			return responseService.getSingleResult((WorkloadsDto)result.get("data"));
//		}else {
			return responseService.getSingleResult(result.get("data"), Integer.parseInt(result.get("status").toString()), (String)result.get("resultMsg"), Boolean.valueOf((boolean) result.get("success")).booleanValue());
//		}

	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header") })
	@ApiOperation(value = "스케줄 생성", notes = "스케줄 생성")
	@PostMapping(value = "/workload/schedule")
	public SingleResult<?> postWorkloadSchedule(@RequestBody RequestScheduleDTO requestScheduleDTO) throws Exception {
		// 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
		HashMap<String, Object> result = new HashMap<>();
		result = workloadService.postWorkloadSchedule(requestScheduleDTO);
		if(Boolean.valueOf((boolean) result.get("success")).booleanValue() == true) {
			return responseService.getSingleResult(result.get("data"));
		}else {
			return responseService.getSingleResult(result.get("data"), Integer.parseInt(result.get("status").toString()), (String)result.get("resultMsg"), Boolean.valueOf((boolean) result.get("success")).booleanValue());
		}

	}
}
