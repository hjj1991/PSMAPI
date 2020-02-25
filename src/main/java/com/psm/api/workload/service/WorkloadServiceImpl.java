package com.psm.api.workload.service;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.apiserver.repository.ApiServerListRepository;
import com.psm.api.workload.dto.WorkloadDto;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.entity.WorkloadEntity;
import com.psm.api.workload.repository.WorkloadRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
//@Transactional
public class WorkloadServiceImpl implements WorkloadService {
	
	@Autowired
	WorkloadRepository workloadRepository;
	
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
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(userNameToAccessProtectServer,
				passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost));
		

		// Make sure the same context is used to execute logically related requests
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		
		// Execute a cheap method first. This will trigger NTLM authentication
		httpPost.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
		CloseableHttpResponse response = null;
		
		
	    try {
	    	response = httpClient.execute(target, httpPost, context);
	    	System.out.println(response.getStatusLine());
	    	if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    		result.put("success", true);
	    		list = mapper.readValue(EntityUtils.toString(response.getEntity()),new TypeReference<HashMap<String, String>>() {});
		    	System.out.println(list);
	    	}else {
	    		list = mapper.readValue(EntityUtils.toString(response.getEntity()),new TypeReference<HashMap<String, String>>() {});
	    		result.put("success", false);
	    	}
	    	
	        
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }

		
		result.put("status", "200");
		result.put("data", list);
		result.put("resultMsg", "api서버정보가 없습니다.");	

		
		return result;
		
	}
	
	
	//전체 워크로드를 조회한다.
	@SuppressWarnings("null")
	@Override
	public HashMap<String, Object> getWorkloadList() throws Exception {
		
		List<ApiServerListEntity> apiserverList = apiServerListRepository.findAll();
		HashMap<String, Object> result = new HashMap<String, Object>();
		WorkloadsDto lastWorkloadsList = new WorkloadsDto();
		List<WorkloadDto> workloadList = new ArrayList<WorkloadDto>();
		
		for(ApiServerListEntity apiserverInfo: apiserverList) {
			/* 해당 API서버가 삭제되었거나 소속 회사가 삭제된 경우 시행하지 않음 */
			if(apiserverInfo.getCompanyIdx() == null || apiserverInfo.getCompanyIdx().getDeletedYn().equals("Y") || apiserverInfo.getDeletedYn().equals("Y")) {
				continue;
			}
			/* Parameters to access PlateSpin Protect Server */
			String userNameToAccessProtectServer = apiserverInfo.getUserNameToAccessProtectServer();
			String passwordToAccessProtectServer = apiserverInfo.getPasswordToAccessProtectServer();
			String domainNameToAccessProtectServer = apiserverInfo.getDomainNameToAccessProtectServer();
			String serverHost = apiserverInfo.getServerHost();
			ObjectMapper mapper = new ObjectMapper();
			
			WorkloadsDto tmpWorkloadList = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd a KK:mm:ss");
			URI startingUri = new URI("/protectionservices/Workloads/");
			CloseableHttpClient httpClient = HttpClients.createDefault();
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(userNameToAccessProtectServer,
					passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost));
			HttpHost target = new HttpHost(serverHost, 80, "http");

			// Make sure the same context is used to execute logically related requests
			HttpClientContext context = HttpClientContext.create();
			context.setCredentialsProvider(credsProvider);

			// Execute a cheap method first. This will trigger NTLM authentication
			HttpGet httpget = new HttpGet(startingUri);
			httpget.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
			try {
				CloseableHttpResponse response1 = httpClient.execute(target, httpget, context);
				if(response1.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					tmpWorkloadList = mapper.readValue(EntityUtils.toString(response1.getEntity()), WorkloadsDto.class);	
					for(int i = 0; i < tmpWorkloadList.getWorkloads().size(); i++) {
						HttpGet httpget2 = new HttpGet(tmpWorkloadList.getWorkloads().get(i).getUri());
						httpget2.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
						CloseableHttpResponse response2 = httpClient.execute(target, httpget2, context);
						if(response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							tmpWorkloadList.getWorkloads().set(i, mapper.readValue(EntityUtils.toString(response2.getEntity(),"UTF-8"), WorkloadDto.class));
							tmpWorkloadList.getWorkloads().get(i).setCompanyName(apiserverInfo.getCompanyIdx().getCompanyName());
							tmpWorkloadList.getWorkloads().get(i).setWorkloadServerHost(serverHost);
							//기존 워크로드ID가 존재하면 업데이트한다.
							if(workloadRepository.findByWorkloadId(tmpWorkloadList.getWorkloads().get(i).getUri().substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1)) != null){
								WorkloadEntity workloadEntity = new WorkloadEntity();
								workloadEntity = workloadRepository.findByWorkloadId(tmpWorkloadList.getWorkloads().get(i).getUri().substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
//								workloadEntity.setWorkloadId(tmpWorkloadList.getWorkloads().get(i).getUri().substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
								workloadEntity.setTargetId(tmpWorkloadList.getWorkloads().get(i).getContainerUri().substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
								workloadEntity.setCurrentState(tmpWorkloadList.getWorkloads().get(i).getCurrentState());
								workloadEntity.setMachineName(tmpWorkloadList.getWorkloads().get(i).getMachineName());
								workloadEntity.setName(tmpWorkloadList.getWorkloads().get(i).getName());
								workloadEntity.setOnline(tmpWorkloadList.getWorkloads().get(i).getOnline());
								workloadEntity.setOperatingSystem(tmpWorkloadList.getWorkloads().get(i).getOperatingSystem());
								workloadEntity.setOperatingSystemVersion(tmpWorkloadList.getWorkloads().get(i).getOperatingSystemVersion());
								workloadEntity.setServicePack(tmpWorkloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
								workloadEntity.setSourceMachinId(tmpWorkloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
								workloadEntity.setUserName(tmpWorkloadList.getWorkloads().get(i).getParameters().get(0).get("Value"));
								workloadEntity.setDiscoveryAddress(tmpWorkloadList.getWorkloads().get(i).getParameters().get(1).get("Value"));
								workloadEntity.setAreBBTollsInstalled(tmpWorkloadList.getWorkloads().get(i).getParameters().get(2).get("Value"));
								workloadEntity.setReadyToCopySnapshotName(tmpWorkloadList.getWorkloads().get(i).getParameters().get(3).get("Value"));
								workloadEntity.setCanDeleteVm(tmpWorkloadList.getWorkloads().get(i).getParameters().get(4).get("Value"));
								workloadEntity.setCanRemoveSource(tmpWorkloadList.getWorkloads().get(i).getParameters().get(5).get("Value"));
								workloadEntity.setCanRemoveBBT(tmpWorkloadList.getWorkloads().get(i).getParameters().get(6).get("Value"));
								workloadEntity.setRunFailoverOnReplicationSuccess(tmpWorkloadList.getWorkloads().get(i).getParameters().get(7).get("Value"));
								workloadEntity.setIsRemoteWorkload(tmpWorkloadList.getWorkloads().get(i).getParameters().get(8).get("Value"));
								workloadEntity.setIsWindowsCluster(tmpWorkloadList.getWorkloads().get(i).getParameters().get(9).get("Value"));
								workloadEntity.setLastFullOn(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(10).get("Value")));
								workloadEntity.setLastIncrementalOn(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(11).get("Value")));
								workloadEntity.setLastTestedFailoverOn(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(12).get("Value")));
								workloadEntity.setLastUpdated(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(13).get("Value")));
								workloadEntity.setFailoverMachineId(tmpWorkloadList.getWorkloads().get(i).getParameters().get(14).get("Value"));
								workloadEntity.setNextFullOn(tmpWorkloadList.getWorkloads().get(i).getParameters().get(15).get("Value"));
								workloadEntity.setNextIncrementalOn(tmpWorkloadList.getWorkloads().get(i).getParameters().get(16).get("Value"));
								workloadEntity.setOnlineStatus(tmpWorkloadList.getWorkloads().get(i).getParameters().get(17).get("Value"));
								workloadEntity.setProtectionLevel(tmpWorkloadList.getWorkloads().get(i).getParameters().get(18).get("Value"));
								workloadEntity.setProtectionState(tmpWorkloadList.getWorkloads().get(i).getParameters().get(19).get("Value"));
								workloadEntity.setTargetPRO(tmpWorkloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
								workloadEntity.setWorkflowStep(tmpWorkloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
								workloadEntity.setWorkloadLifecycle(tmpWorkloadList.getWorkloads().get(i).getParameters().get(22).get("Value"));
								workloadEntity.setWorkloadGroupId(tmpWorkloadList.getWorkloads().get(i).getParameters().get(23).get("Value"));
								workloadEntity.setReplicationScheduleStatus(tmpWorkloadList.getWorkloads().get(i).getParameters().get(24).get("Value"));
								workloadEntity.setSourceMachineControllerAlias(tmpWorkloadList.getWorkloads().get(i).getParameters().get(25).get("Value"));
								workloadEntity.setPrepareForFailoverConfigurationUri(tmpWorkloadList.getWorkloads().get(i).getPrepareForFailoverConfigurationUri());
								workloadEntity.setScheduleActive(tmpWorkloadList.getWorkloads().get(i).getScheduleActive());
								workloadEntity.setSchedulesUri(tmpWorkloadList.getWorkloads().get(i).getSchedulesUri());
								workloadEntity.setTag(tmpWorkloadList.getWorkloads().get(i).getTag());
								workloadEntity.setTestCutoverMarkedSuccessful(tmpWorkloadList.getWorkloads().get(i).getTestCutoverMarkedSuccessful());
								workloadEntity.setTestFailoverConfigurationUri(tmpWorkloadList.getWorkloads().get(i).getTestFailoverConfigurationUri());
								workloadEntity.setTmData(tmpWorkloadList.getWorkloads().get(i).getTmData());
								workloadEntity.setWindowsServiceUri(tmpWorkloadList.getWorkloads().get(i).getWindowsServicesUri());
								workloadEntity.setWorkloadConfigurationUri(tmpWorkloadList.getWorkloads().get(i).getWorkloadConfigurationUri());
								workloadEntity.setCompanyIdx(apiserverInfo.getCompanyIdx());
								workloadRepository.save(workloadEntity);
							}else { //기존 워크로드ID가 존재하지 않으면 새로 insert
								WorkloadEntity workloadEntity = new WorkloadEntity();
								workloadEntity.setWorkloadId(tmpWorkloadList.getWorkloads().get(i).getUri().substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
								workloadEntity.setTargetId(tmpWorkloadList.getWorkloads().get(i).getContainerUri().substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
								workloadEntity.setCurrentState(tmpWorkloadList.getWorkloads().get(i).getCurrentState());
								workloadEntity.setMachineName(tmpWorkloadList.getWorkloads().get(i).getMachineName());
								workloadEntity.setName(tmpWorkloadList.getWorkloads().get(i).getName());
								workloadEntity.setOnline(tmpWorkloadList.getWorkloads().get(i).getOnline());
								workloadEntity.setOperatingSystem(tmpWorkloadList.getWorkloads().get(i).getOperatingSystem());
								workloadEntity.setOperatingSystemVersion(tmpWorkloadList.getWorkloads().get(i).getOperatingSystemVersion());
								workloadEntity.setServicePack(tmpWorkloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
								workloadEntity.setSourceMachinId(tmpWorkloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
								workloadEntity.setUserName(tmpWorkloadList.getWorkloads().get(i).getParameters().get(0).get("Value"));
								workloadEntity.setDiscoveryAddress(tmpWorkloadList.getWorkloads().get(i).getParameters().get(1).get("Value"));
								workloadEntity.setAreBBTollsInstalled(tmpWorkloadList.getWorkloads().get(i).getParameters().get(2).get("Value"));
								workloadEntity.setReadyToCopySnapshotName(tmpWorkloadList.getWorkloads().get(i).getParameters().get(3).get("Value"));
								workloadEntity.setCanDeleteVm(tmpWorkloadList.getWorkloads().get(i).getParameters().get(4).get("Value"));
								workloadEntity.setCanRemoveSource(tmpWorkloadList.getWorkloads().get(i).getParameters().get(5).get("Value"));
								workloadEntity.setCanRemoveBBT(tmpWorkloadList.getWorkloads().get(i).getParameters().get(6).get("Value"));
								workloadEntity.setRunFailoverOnReplicationSuccess(tmpWorkloadList.getWorkloads().get(i).getParameters().get(7).get("Value"));
								workloadEntity.setIsRemoteWorkload(tmpWorkloadList.getWorkloads().get(i).getParameters().get(8).get("Value"));
								workloadEntity.setIsWindowsCluster(tmpWorkloadList.getWorkloads().get(i).getParameters().get(9).get("Value"));
								workloadEntity.setLastFullOn(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(10).get("Value")));
								workloadEntity.setLastIncrementalOn(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(11).get("Value")));
								workloadEntity.setLastTestedFailoverOn(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(12).get("Value")));
								workloadEntity.setLastUpdated(sdf.parse(tmpWorkloadList.getWorkloads().get(i).getParameters().get(13).get("Value")));
								workloadEntity.setFailoverMachineId(tmpWorkloadList.getWorkloads().get(i).getParameters().get(14).get("Value"));
								workloadEntity.setNextFullOn(tmpWorkloadList.getWorkloads().get(i).getParameters().get(15).get("Value"));
								workloadEntity.setNextIncrementalOn(tmpWorkloadList.getWorkloads().get(i).getParameters().get(16).get("Value"));
								workloadEntity.setOnlineStatus(tmpWorkloadList.getWorkloads().get(i).getParameters().get(17).get("Value"));
								workloadEntity.setProtectionLevel(tmpWorkloadList.getWorkloads().get(i).getParameters().get(18).get("Value"));
								workloadEntity.setProtectionState(tmpWorkloadList.getWorkloads().get(i).getParameters().get(19).get("Value"));
								workloadEntity.setTargetPRO(tmpWorkloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
								workloadEntity.setWorkflowStep(tmpWorkloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
								workloadEntity.setWorkloadLifecycle(tmpWorkloadList.getWorkloads().get(i).getParameters().get(22).get("Value"));
								workloadEntity.setWorkloadGroupId(tmpWorkloadList.getWorkloads().get(i).getParameters().get(23).get("Value"));
								workloadEntity.setReplicationScheduleStatus(tmpWorkloadList.getWorkloads().get(i).getParameters().get(24).get("Value"));
								workloadEntity.setSourceMachineControllerAlias(tmpWorkloadList.getWorkloads().get(i).getParameters().get(25).get("Value"));
								workloadEntity.setPrepareForFailoverConfigurationUri(tmpWorkloadList.getWorkloads().get(i).getPrepareForFailoverConfigurationUri());
								workloadEntity.setScheduleActive(tmpWorkloadList.getWorkloads().get(i).getScheduleActive());
								workloadEntity.setSchedulesUri(tmpWorkloadList.getWorkloads().get(i).getSchedulesUri());
								workloadEntity.setTag(tmpWorkloadList.getWorkloads().get(i).getTag());
								workloadEntity.setTestCutoverMarkedSuccessful(tmpWorkloadList.getWorkloads().get(i).getTestCutoverMarkedSuccessful());
								workloadEntity.setTestFailoverConfigurationUri(tmpWorkloadList.getWorkloads().get(i).getTestFailoverConfigurationUri());
								workloadEntity.setTmData(tmpWorkloadList.getWorkloads().get(i).getTmData());
								workloadEntity.setWindowsServiceUri(tmpWorkloadList.getWorkloads().get(i).getWindowsServicesUri());
								workloadEntity.setWorkloadConfigurationUri(tmpWorkloadList.getWorkloads().get(i).getWorkloadConfigurationUri());
								workloadEntity.setCompanyIdx(apiserverInfo.getCompanyIdx());
								workloadRepository.save(workloadEntity);
							}
						}else {	//워크로드 정보불러오기 실패시
							
						}
						workloadList.add(tmpWorkloadList.getWorkloads().get(i));
					}	
					
					//워크로드 동기화 성공시
//					result.put("status", "200");
//					result.put("data", tmpWorkloadList);
//					return result;
				}else {	//워크로드 리스트 정보 불러오기 실패시
					result.put("status", "-2");
					result.put("data", null);
					result.put("resultMsg", "접속실패 responseCode:" + response1.getStatusLine().getStatusCode());
					return result;
				}

			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
//		System.out.println(lastWorkloadList.getWorkloads());
		lastWorkloadsList.setWorkloads(workloadList);
		result.put("status", "200");
		result.put("data", lastWorkloadsList);
		result.put("resultMsg", "api서버정보가 없습니다.");	
		return result;
	}
}