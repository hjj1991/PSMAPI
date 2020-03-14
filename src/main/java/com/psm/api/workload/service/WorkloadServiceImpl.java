package com.psm.api.workload.service;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.apiserver.repository.ApiServerListRepository;
import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.UserRepository;
import com.psm.api.workload.dto.FindWorkloadDto;
import com.psm.api.workload.dto.ResponseWorkloadListDto;
import com.psm.api.workload.dto.WorkloadDto;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.entity.AvailableActionEntity;
import com.psm.api.workload.entity.WorkloadEntity;
import com.psm.api.workload.repository.AvailableActionRepository;
import com.psm.api.workload.repository.WorkloadRepository;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
//@Transactional
public class WorkloadServiceImpl implements WorkloadService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	WorkloadRepository workloadRepository;
	@Autowired
	AvailableActionRepository availableActionRepository;
	@Value("spring.jwt.secret")
	private String secretKey;
	@Autowired
	ApiServerListRepository apiServerListRepository;
	
	public HashMap<String, Object> postWorkloadAction(String serverHost, String actionUrl) throws Exception {
		
		ApiServerListEntity apiserverInfo = apiServerListRepository.findByServerHostAndDeletedYn(serverHost, "N");
		String userNameToAccessProtectServer = apiserverInfo.getUserNameToAccessProtectServer();
		String passwordToAccessProtectServer = apiserverInfo.getPasswordToAccessProtectServer();
		String domainNameToAccessProtectServer = apiserverInfo.getDomainNameToAccessProtectServer();
		ObjectMapper mapper = new ObjectMapper();
		
		HashMap<String, String> list = new HashMap<String,String>();
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(actionUrl);
		HttpHost target = new HttpHost(serverHost, 80, "http");
		StringEntity postParams = new StringEntity("{\n}");
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(userNameToAccessProtectServer,
				passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost));
		

		// Make sure the same context is used to execute logically related requests
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		
		// Execute a cheap method first. This will trigger NTLM authentication
		httpPost.setHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setEntity(postParams);
		CloseableHttpResponse response = null;
		
		
	    try {
	    	response = httpClient.execute(target, httpPost, context);
	    	System.out.println(response.getStatusLine());
	    	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK || response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
	    		result.put("success", true);
	    		list = mapper.readValue(EntityUtils.toString(response.getEntity()),new TypeReference<HashMap<String, String>>() {});
	    		result.put("status", "200");
	    		result.put("data", list);
	    		result.put("resultMsg", "성공하였습니다.");	
		    	System.out.println(list);
	    	}else {
	    		list = mapper.readValue(EntityUtils.toString(response.getEntity()),new TypeReference<HashMap<String, String>>() {});
	    		result.put("success", false);
	    		result.put("status", "200");
	    		result.put("data", list);
	    		result.put("resultMsg", "실패하였습니다.");	
	    	}
	    	
	        
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }

		

		

		
		return result;
		
	}
	
	
	//전체 워크로드를 조회한다.
	@Override
	public HashMap<String, Object> getWorkloadList(FindWorkloadDto findWorkloadDto, String authToken) throws Exception {
	
		int companyIdx; //회사 idx
		HashMap<String, Object> result = new HashMap<String, Object>();
		Page<WorkloadEntity> data = null;
		String userRole;
		
		//토큰에서 사용자 아이디를 가져와서 repository에서 해당 사용자 검색
		String userId = Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(authToken).getBody().getSubject();
		UserEntity userInfo = userRepository.findByUserId(String.valueOf(userId)).orElseThrow(CUserNotFoundException::new);
		
		//유저의 권한 불러온다.
		userRole = userInfo.getUserRoles().get(0);
		
		//소속회사 idx번호를 구한다.
		companyIdx = userInfo.getCompanyIdx().getCompanyIdx();
		Pageable pageRequest = PageRequest.of(findWorkloadDto.getPage() - 1,  findWorkloadDto.getPageSize());
		if(userRole.equals("ROLE_MASTER")) {				//사용자 권한에 따른 검색분기 나눔
			if(findWorkloadDto.getFindTarget() != null && findWorkloadDto.getFindKeyword() != null) {
				String target = findWorkloadDto.getFindTarget();
				String keyword = findWorkloadDto.getFindKeyword();
				if(target.equals("companyName")) {
					data = workloadRepository.findByCompanyIdx_CompanyNameLike("%" + keyword + "%", pageRequest);
					//워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
					for(WorkloadEntity workload : data) {
						workload.setAvailableActionList(availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
					}
				}else {
					data = workloadRepository.findAll(pageRequest);
					//워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
					for(WorkloadEntity workload : data) {
						workload.setAvailableActionList(availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
					}
				}
			}else {	
				data = workloadRepository.findAll(pageRequest);
				//워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
				for(WorkloadEntity workload : data) {
					workload.setAvailableActionList(availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
				}
			}
		}else {
				data = workloadRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
				//워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
				for(WorkloadEntity workload : data) {
					workload.setAvailableActionList(availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
				}
		}
		
		

		result.put("status", "200");
		result.put("data", data);
		result.put("resultMsg", "정상조회되었습니다.");	
		return result;
	}
}