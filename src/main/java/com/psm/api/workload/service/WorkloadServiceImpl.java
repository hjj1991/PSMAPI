package com.psm.api.workload.service;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	
	
	@SuppressWarnings("null")
	@Override
	public WorkloadsDto getWorkloadList() throws Exception {
		/* Parameters to access PlateSpin Protect Server */
		String userNameToAccessProtectServer = "administrator";
		String passwordToAccessProtectServer = "vortmasp12#$";
		String domainNameToAccessProtectServer = "platespin";
		String serverHost = "migrate.eonit.co.kr";
		ObjectMapper mapper = new ObjectMapper();
		WorkloadsDto workloadList = null;
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
		CloseableHttpResponse response1 = httpClient.execute(target, httpget, context);
		if(response1.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			workloadList = mapper.readValue(EntityUtils.toString(response1.getEntity()), WorkloadsDto.class);	
			for(int i = 0; i < workloadList.getWorkloads().size(); i++) {
				HttpGet httpget2 = new HttpGet(workloadList.getWorkloads().get(i).getUri());
				httpget2.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
				CloseableHttpResponse response2 = httpClient.execute(target, httpget2, context);
				if(response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					workloadList.getWorkloads().set(i, mapper.readValue(EntityUtils.toString(response2.getEntity(),"UTF-8"), WorkloadDto.class));
					//기존 워크로드ID가 존재하면 업데이트한다.
					if(workloadRepository.findByWorkloadId(workloadList.getWorkloads().get(i).getUri().substring(workloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1)) != null){
						WorkloadEntity workloadEntity = new WorkloadEntity();
						workloadEntity = workloadRepository.findByWorkloadId(workloadList.getWorkloads().get(i).getUri().substring(workloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
//						workloadEntity.setWorkloadId(workloadList.getWorkloads().get(i).getUri().substring(workloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
						workloadEntity.setTargetId(workloadList.getWorkloads().get(i).getContainerUri().substring(workloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
						workloadEntity.setCurrentState(workloadList.getWorkloads().get(i).getCurrentState());
						workloadEntity.setMachineName(workloadList.getWorkloads().get(i).getMachineName());
						workloadEntity.setName(workloadList.getWorkloads().get(i).getName());
						workloadEntity.setOnline(workloadList.getWorkloads().get(i).getOnline());
						workloadEntity.setOperatingSystem(workloadList.getWorkloads().get(i).getOperatingSystem());
						workloadEntity.setOperatingSystemVersion(workloadList.getWorkloads().get(i).getOperatingSystemVersion());
						workloadEntity.setServicePack(workloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
						workloadEntity.setSourceMachinId(workloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
						workloadEntity.setUserName(workloadList.getWorkloads().get(i).getParameters().get(0).get("Value"));
						workloadEntity.setDiscoveryAddress(workloadList.getWorkloads().get(i).getParameters().get(1).get("Value"));
						workloadEntity.setAreBBTollsInstalled(workloadList.getWorkloads().get(i).getParameters().get(2).get("Value"));
						workloadEntity.setReadyToCopySnapshotName(workloadList.getWorkloads().get(i).getParameters().get(3).get("Value"));
						workloadEntity.setCanDeleteVm(workloadList.getWorkloads().get(i).getParameters().get(4).get("Value"));
						workloadEntity.setCanRemoveSource(workloadList.getWorkloads().get(i).getParameters().get(5).get("Value"));
						workloadEntity.setCanRemoveBBT(workloadList.getWorkloads().get(i).getParameters().get(6).get("Value"));
						workloadEntity.setRunFailoverOnReplicationSuccess(workloadList.getWorkloads().get(i).getParameters().get(7).get("Value"));
						workloadEntity.setIsRemoteWorkload(workloadList.getWorkloads().get(i).getParameters().get(8).get("Value"));
						workloadEntity.setIsWindowsCluster(workloadList.getWorkloads().get(i).getParameters().get(9).get("Value"));
						workloadEntity.setLastFullOn(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(10).get("Value")));
						workloadEntity.setLastIncrementalOn(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(11).get("Value")));
						workloadEntity.setLastTestedFailoverOn(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(12).get("Value")));
						workloadEntity.setLastUpdated(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(13).get("Value")));
						workloadEntity.setFailoverMachineId(workloadList.getWorkloads().get(i).getParameters().get(14).get("Value"));
						workloadEntity.setNextFullOn(workloadList.getWorkloads().get(i).getParameters().get(15).get("Value"));
						workloadEntity.setNextIncrementalOn(workloadList.getWorkloads().get(i).getParameters().get(16).get("Value"));
						workloadEntity.setOnlineStatus(workloadList.getWorkloads().get(i).getParameters().get(17).get("Value"));
						workloadEntity.setProtectionLevel(workloadList.getWorkloads().get(i).getParameters().get(18).get("Value"));
						workloadEntity.setProtectionState(workloadList.getWorkloads().get(i).getParameters().get(19).get("Value"));
						workloadEntity.setTargetPRO(workloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
						workloadEntity.setWorkflowStep(workloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
						workloadEntity.setWorkloadLifecycle(workloadList.getWorkloads().get(i).getParameters().get(22).get("Value"));
						workloadEntity.setWorkloadGroupId(workloadList.getWorkloads().get(i).getParameters().get(23).get("Value"));
						workloadEntity.setReplicationScheduleStatus(workloadList.getWorkloads().get(i).getParameters().get(24).get("Value"));
						workloadEntity.setSourceMachineControllerAlias(workloadList.getWorkloads().get(i).getParameters().get(25).get("Value"));
						workloadEntity.setPrepareForFailoverConfigurationUri(workloadList.getWorkloads().get(i).getPrepareForFailoverConfigurationUri());
						workloadEntity.setScheduleActive(workloadList.getWorkloads().get(i).getScheduleActive());
						workloadEntity.setSchedulesUri(workloadList.getWorkloads().get(i).getSchedulesUri());
						workloadEntity.setTag(workloadList.getWorkloads().get(i).getTag());
						workloadEntity.setTestCutoverMarkedSuccessful(workloadList.getWorkloads().get(i).getTestCutoverMarkedSuccessful());
						workloadEntity.setTestFailoverConfigurationUri(workloadList.getWorkloads().get(i).getTestFailoverConfigurationUri());
						workloadEntity.setTmData(workloadList.getWorkloads().get(i).getTmData());
						workloadEntity.setWindowsServiceUri(workloadList.getWorkloads().get(i).getWindowsServicesUri());
						workloadEntity.setWorkloadConfigurationUri(workloadList.getWorkloads().get(i).getWorkloadConfigurationUri());
						workloadRepository.save(workloadEntity);
					}else { //기존 워크로드ID가 존재하지 않으면 새로 insert
						WorkloadEntity workloadEntity = new WorkloadEntity();
						workloadEntity.setWorkloadId(workloadList.getWorkloads().get(i).getUri().substring(workloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
						workloadEntity.setTargetId(workloadList.getWorkloads().get(i).getContainerUri().substring(workloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
						workloadEntity.setCurrentState(workloadList.getWorkloads().get(i).getCurrentState());
						workloadEntity.setMachineName(workloadList.getWorkloads().get(i).getMachineName());
						workloadEntity.setName(workloadList.getWorkloads().get(i).getName());
						workloadEntity.setOnline(workloadList.getWorkloads().get(i).getOnline());
						workloadEntity.setOperatingSystem(workloadList.getWorkloads().get(i).getOperatingSystem());
						workloadEntity.setOperatingSystemVersion(workloadList.getWorkloads().get(i).getOperatingSystemVersion());
						workloadEntity.setServicePack(workloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
						workloadEntity.setSourceMachinId(workloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
						workloadEntity.setUserName(workloadList.getWorkloads().get(i).getParameters().get(0).get("Value"));
						workloadEntity.setDiscoveryAddress(workloadList.getWorkloads().get(i).getParameters().get(1).get("Value"));
						workloadEntity.setAreBBTollsInstalled(workloadList.getWorkloads().get(i).getParameters().get(2).get("Value"));
						workloadEntity.setReadyToCopySnapshotName(workloadList.getWorkloads().get(i).getParameters().get(3).get("Value"));
						workloadEntity.setCanDeleteVm(workloadList.getWorkloads().get(i).getParameters().get(4).get("Value"));
						workloadEntity.setCanRemoveSource(workloadList.getWorkloads().get(i).getParameters().get(5).get("Value"));
						workloadEntity.setCanRemoveBBT(workloadList.getWorkloads().get(i).getParameters().get(6).get("Value"));
						workloadEntity.setRunFailoverOnReplicationSuccess(workloadList.getWorkloads().get(i).getParameters().get(7).get("Value"));
						workloadEntity.setIsRemoteWorkload(workloadList.getWorkloads().get(i).getParameters().get(8).get("Value"));
						workloadEntity.setIsWindowsCluster(workloadList.getWorkloads().get(i).getParameters().get(9).get("Value"));
						workloadEntity.setLastFullOn(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(10).get("Value")));
						workloadEntity.setLastIncrementalOn(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(11).get("Value")));
						workloadEntity.setLastTestedFailoverOn(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(12).get("Value")));
						workloadEntity.setLastUpdated(sdf.parse(workloadList.getWorkloads().get(i).getParameters().get(13).get("Value")));
						workloadEntity.setFailoverMachineId(workloadList.getWorkloads().get(i).getParameters().get(14).get("Value"));
						workloadEntity.setNextFullOn(workloadList.getWorkloads().get(i).getParameters().get(15).get("Value"));
						workloadEntity.setNextIncrementalOn(workloadList.getWorkloads().get(i).getParameters().get(16).get("Value"));
						workloadEntity.setOnlineStatus(workloadList.getWorkloads().get(i).getParameters().get(17).get("Value"));
						workloadEntity.setProtectionLevel(workloadList.getWorkloads().get(i).getParameters().get(18).get("Value"));
						workloadEntity.setProtectionState(workloadList.getWorkloads().get(i).getParameters().get(19).get("Value"));
						workloadEntity.setTargetPRO(workloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
						workloadEntity.setWorkflowStep(workloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
						workloadEntity.setWorkloadLifecycle(workloadList.getWorkloads().get(i).getParameters().get(22).get("Value"));
						workloadEntity.setWorkloadGroupId(workloadList.getWorkloads().get(i).getParameters().get(23).get("Value"));
						workloadEntity.setReplicationScheduleStatus(workloadList.getWorkloads().get(i).getParameters().get(24).get("Value"));
						workloadEntity.setSourceMachineControllerAlias(workloadList.getWorkloads().get(i).getParameters().get(25).get("Value"));
						workloadEntity.setPrepareForFailoverConfigurationUri(workloadList.getWorkloads().get(i).getPrepareForFailoverConfigurationUri());
						workloadEntity.setScheduleActive(workloadList.getWorkloads().get(i).getScheduleActive());
						workloadEntity.setSchedulesUri(workloadList.getWorkloads().get(i).getSchedulesUri());
						workloadEntity.setTag(workloadList.getWorkloads().get(i).getTag());
						workloadEntity.setTestCutoverMarkedSuccessful(workloadList.getWorkloads().get(i).getTestCutoverMarkedSuccessful());
						workloadEntity.setTestFailoverConfigurationUri(workloadList.getWorkloads().get(i).getTestFailoverConfigurationUri());
						workloadEntity.setTmData(workloadList.getWorkloads().get(i).getTmData());
						workloadEntity.setWindowsServiceUri(workloadList.getWorkloads().get(i).getWindowsServicesUri());
						workloadEntity.setWorkloadConfigurationUri(workloadList.getWorkloads().get(i).getWorkloadConfigurationUri());
						workloadRepository.save(workloadEntity);
					}
				}
			}	
		}
		return workloadList;
	}

}
