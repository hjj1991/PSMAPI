package com.psm.api.workload.service;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	
	public HashMap<String, Object> postWorkloadAction(String serverHost, String actionUrl, String workloadId) throws Exception {
		
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
	    		
	    		list = mapper.readValue(EntityUtils.toString(response.getEntity()),new TypeReference<HashMap<String, String>>() {});

				HttpGet httpget = new HttpGet("/protectionservices/Workloads/" + workloadId);
				httpget.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
				CloseableHttpResponse response2 = httpClient.execute(target, httpget, context);
				System.out.println("켁");
				System.out.println(workloadId);
				System.out.println(response2.getStatusLine());
				if(response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					System.out.println("켁2");
					WorkloadDto tmpWorkload = mapper.readValue(EntityUtils.toString(response2.getEntity(),"UTF-8"), WorkloadDto.class);
					
					WorkloadEntity workloadEntity = workloadRepository.findByWorkloadId(workloadId);
					workloadEntity = workloadRepository.findByWorkloadId(workloadId);
					workloadEntity.setServerHost(apiserverInfo.getServerHost());
					workloadEntity.setCurrentState(tmpWorkload.getCurrentState());
					workloadEntity.setMachineName(tmpWorkload.getMachineName());
					workloadEntity.setName(tmpWorkload.getName());
					workloadEntity.setOnline(tmpWorkload.getOnline());
					workloadEntity.setOperatingSystem(tmpWorkload.getOperatingSystem());
					workloadEntity.setOperatingSystemVersion(tmpWorkload.getOperatingSystemVersion());
					workloadEntity.setServicePack(tmpWorkload.getParameters().get(20).get("Value"));
					workloadEntity.setSourceMachinId(tmpWorkload.getParameters().get(21).get("Value"));
					workloadEntity.setUserName(tmpWorkload.getParameters().get(0).get("Value"));
					workloadEntity.setDiscoveryAddress(tmpWorkload.getParameters().get(1).get("Value"));
					workloadEntity.setAreBBTollsInstalled(tmpWorkload.getParameters().get(2).get("Value"));
					workloadEntity.setReadyToCopySnapshotName(tmpWorkload.getParameters().get(3).get("Value"));
					workloadEntity.setCanDeleteVm(tmpWorkload.getParameters().get(4).get("Value"));
					workloadEntity.setCanRemoveSource(tmpWorkload.getParameters().get(5).get("Value"));
					workloadEntity.setCanRemoveBBT(tmpWorkload.getParameters().get(6).get("Value"));
					workloadEntity.setRunFailoverOnReplicationSuccess(tmpWorkload.getParameters().get(7).get("Value"));
					workloadEntity.setIsRemoteWorkload(tmpWorkload.getParameters().get(8).get("Value"));
					workloadEntity.setIsWindowsCluster(tmpWorkload.getParameters().get(9).get("Value"));
					workloadEntity.setLastFullOn(tmpWorkload.getParameters().get(10).get("Value"));
					workloadEntity.setLastIncrementalOn(tmpWorkload.getParameters().get(11).get("Value"));
					workloadEntity.setLastTestedFailoverOn(tmpWorkload.getParameters().get(12).get("Value"));
					workloadEntity.setLastUpdated(tmpWorkload.getParameters().get(13).get("Value"));
					workloadEntity.setFailoverMachineId(tmpWorkload.getParameters().get(14).get("Value"));
					workloadEntity.setNextFullOn(tmpWorkload.getParameters().get(15).get("Value"));
					workloadEntity.setNextIncrementalOn(tmpWorkload.getParameters().get(16).get("Value"));
					workloadEntity.setOnlineStatus(tmpWorkload.getParameters().get(17).get("Value"));
					workloadEntity.setProtectionLevel(tmpWorkload.getParameters().get(22).get("Value"));
					workloadEntity.setProtectionState(tmpWorkload.getParameters().get(23).get("Value"));
					workloadEntity.setTargetPRO(tmpWorkload.getParameters().get(24).get("Value"));
					workloadEntity.setWorkflowStep(tmpWorkload.getParameters().get(25).get("Value"));
					workloadEntity.setWorkloadLifecycle(tmpWorkload.getParameters().get(26).get("Value"));
					workloadEntity.setWorkloadGroupId(tmpWorkload.getParameters().get(27).get("Value"));
					workloadEntity.setReplicationScheduleStatus(tmpWorkload.getParameters().get(28).get("Value"));
					workloadEntity.setSourceMachineControllerAlias(tmpWorkload.getParameters().get(29).get("Value"));
					workloadEntity.setPrepareForFailoverConfigurationUri(tmpWorkload.getPrepareForFailoverConfigurationUri());
					workloadEntity.setScheduleActive(tmpWorkload.getScheduleActive());
					workloadEntity.setSchedulesUri(tmpWorkload.getSchedulesUri());
					workloadEntity.setTag(tmpWorkload.getTag());
					workloadEntity.setTestCutoverMarkedSuccessful(tmpWorkload.getTestCutoverMarkedSuccessful());
					workloadEntity.setTestFailoverConfigurationUri(tmpWorkload.getTestFailoverConfigurationUri());
					workloadEntity.setTmData(tmpWorkload.getTmData());
					workloadEntity.setWindowsServiceUri(tmpWorkload.getWindowsServicesUri());
					workloadEntity.setWorkloadConfigurationUri(tmpWorkload.getWorkloadConfigurationUri());
					workloadEntity.setCompanyIdx(apiserverInfo.getCompanyIdx());
					workloadEntity.setSyncDate(new Date());
					workloadEntity.setOperationUri(list.get("OperationUri"));
					workloadRepository.save(workloadEntity);
					//해당 워크로드ID로 워크로드 액션 삭제
					availableActionRepository.deleteInBatch(availableActionRepository.findByWorkloadId(workloadId));
					//사용가능한 워크로드 액션들을 insert한다.
					for(int availableCount = 0; availableCount < tmpWorkload.getAvailableTransitions().size(); availableCount++) {
						AvailableActionEntity tempAvailableActionEntity = new AvailableActionEntity();
						tempAvailableActionEntity.setName(tmpWorkload.getAvailableTransitions().get(availableCount).get("Name"));
						tempAvailableActionEntity.setUri(tmpWorkload.getAvailableTransitions().get(availableCount).get("Uri"));
						tempAvailableActionEntity.setWorkloadId(workloadId);
						availableActionRepository.save(tempAvailableActionEntity);
					}
				}
	    		
	    		result.put("success", true);
	    		
	    		result.put("status", "200");
	    		result.put("data", list);
	    		result.put("resultMsg", "성공하였습니다.");	
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