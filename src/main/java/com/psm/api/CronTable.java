package com.psm.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.apiserver.repository.ApiServerListRepository;
import com.psm.api.workload.dto.WorkloadDto;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.entity.AvailableActionEntity;
import com.psm.api.workload.entity.WorkloadEntity;
import com.psm.api.workload.repository.AvailableActionRepository;
import com.psm.api.workload.repository.WorkloadRepository;
import com.psm.api.workload.service.TestService;

@Component
public class CronTable {
	
	@Autowired
	WorkloadRepository workloadRepository;
	@Autowired
	AvailableActionRepository availableActionRepository;
	@Autowired
	TestService testService;
	
	@Autowired
	ApiServerListRepository apiServerListRepository;

    // 애플리케이션 시작 후 2분 후에 첫 실행, 그 후 매 2분마다 주기적으로 실행한다.
    @Scheduled(initialDelay = 600 * 2, fixedDelay = 60000 * 2)
    @Transactional
    public void workloadSync() {

    	System.out.println("Current Thread : {}" + Thread.currentThread().getName());
        // 실행될 로직
		List<ApiServerListEntity> apiserverList = apiServerListRepository.findAll();
//		HashMap<String, Object> result = new HashMap<String, Object>();
//		WorkloadsDto lastWorkloadsList = new WorkloadsDto();
//		List<WorkloadDto> workloadList = new ArrayList<WorkloadDto>();
		
		//액션테이블 비우기
//		availableActionRepository.deleteAllInBatch();
		availableActionRepository.resetIdAvailableActionTable();
		
		for(ApiServerListEntity apiserverInfo: apiserverList) {
			if(apiserverInfo.getCompanyIdx() == null || apiserverInfo.getCompanyIdx().getDeletedYn().equals("Y") || apiserverInfo.getDeletedYn().equals("Y")) {
				continue;
			}
			testService.asyncTest(apiserverInfo);
		}
	}
    
}
