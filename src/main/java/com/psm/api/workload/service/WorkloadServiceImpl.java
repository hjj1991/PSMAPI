package com.psm.api.workload.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.apiserver.repository.ApiServerListRepository;
import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.common.service.NtlmGetPostService;
import com.psm.api.common.service.WriteLogService;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.UserRepository;
import com.psm.api.workload.dto.FindWorkloadDto;
import com.psm.api.workload.dto.RequestScheduleDTO;
import com.psm.api.workload.dto.WorkloadDto;
import com.psm.api.workload.dto.WorkloadOperationDTO;
import com.psm.api.workload.dto.WorkloadsDto;
import com.psm.api.workload.entity.AvailableActionEntity;
import com.psm.api.workload.entity.ScheduleEntity;
import com.psm.api.workload.entity.WorkloadEntity;
import com.psm.api.workload.repository.AvailableActionRepository;
import com.psm.api.workload.repository.ScheduleRepository;
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
	@Autowired
	NtlmGetPostService ntlmGetPostService;
	@Autowired
	ScheduleRepository scheduleRepository;
	@Autowired
	WriteLogService writeLogService;

	public HashMap<String, Object> postWorkloadAction(String serverHost, String actionUrl, String workloadId)
			throws Exception {

		ApiServerListEntity apiserverInfo = apiServerListRepository.findByServerHostAndDeletedYn(serverHost, "N");
		String userNameToAccessProtectServer = apiserverInfo.getUserNameToAccessProtectServer();
		String passwordToAccessProtectServer = apiserverInfo.getPasswordToAccessProtectServer();
		String domainNameToAccessProtectServer = apiserverInfo.getDomainNameToAccessProtectServer();
		ObjectMapper mapper = new ObjectMapper();

		HashMap<String, String> list = new HashMap<String, String>();
		HashMap<String, Object> result = new HashMap<String, Object>();

		CloseableHttpResponse response = null;

		try {
			response = ntlmGetPostService.postRequest(userNameToAccessProtectServer, passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, actionUrl);
			System.out.println(response.getStatusLine());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
					|| response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {

				list = mapper.readValue(EntityUtils.toString(response.getEntity()),
						new TypeReference<HashMap<String, String>>() {
						});
//				CloseableHttpResponse response2 = httpClient.execute(target, httpget, context);
				CloseableHttpResponse response2 = ntlmGetPostService.getRequest(userNameToAccessProtectServer, passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, "/protectionservices/Workloads/" + workloadId);
				if (response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					WorkloadDto tmpWorkload = mapper.readValue(EntityUtils.toString(response2.getEntity(), "UTF-8"),
							WorkloadDto.class);

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
					workloadEntity
							.setPrepareForFailoverConfigurationUri(tmpWorkload.getPrepareForFailoverConfigurationUri());
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
					// 해당 워크로드ID로 워크로드 액션 삭제
					availableActionRepository.deleteInBatch(availableActionRepository.findByWorkloadId(workloadId));
					// 사용가능한 워크로드 액션들을 insert한다.
					for (int availableCount = 0; availableCount < tmpWorkload.getAvailableTransitions()
							.size(); availableCount++) {
						AvailableActionEntity tempAvailableActionEntity = new AvailableActionEntity();
						tempAvailableActionEntity
								.setName(tmpWorkload.getAvailableTransitions().get(availableCount).get("Name"));
						tempAvailableActionEntity
								.setUri(tmpWorkload.getAvailableTransitions().get(availableCount).get("Uri"));
						tempAvailableActionEntity.setWorkloadId(workloadId);
						availableActionRepository.save(tempAvailableActionEntity);
					}
				}

				result.put("success", true);

				result.put("status", "200");
				result.put("data", list);
				result.put("resultMsg", "성공하였습니다.");
			} else {
				list = mapper.readValue(EntityUtils.toString(response.getEntity()),
						new TypeReference<HashMap<String, String>>() {
						});
				result.put("success", false);
				result.put("status", "200");
				result.put("data", list);
				result.put("resultMsg", "실패하였습니다.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	// 전체 워크로드를 조회한다.
	@Override
	public HashMap<String, Object> getWorkloadList(FindWorkloadDto findWorkloadDto, String authToken) throws Exception {

		int companyIdx; // 회사 idx
		HashMap<String, Object> result = new HashMap<String, Object>();
		Page<WorkloadEntity> data = null;
		String userRole;

		// 토큰에서 사용자 아이디를 가져와서 repository에서 해당 사용자 검색
		String userId = Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(authToken).getBody()
				.getSubject();
		UserEntity userInfo = userRepository.findByUserId(String.valueOf(userId))
				.orElseThrow(CUserNotFoundException::new);

		// 유저의 권한 불러온다.
		userRole = userInfo.getUserRoles().get(0);

		// 소속회사 idx번호를 구한다.
		companyIdx = userInfo.getCompanyIdx().getCompanyIdx();
		Pageable pageRequest = PageRequest.of(findWorkloadDto.getPage() - 1, findWorkloadDto.getPageSize());
		if (userRole.equals("ROLE_MASTER")) { // 사용자 권한에 따른 검색분기 나눔
			if (findWorkloadDto.getFindTarget() != null && findWorkloadDto.getFindKeyword() != null) {
				String target = findWorkloadDto.getFindTarget();
				String keyword = findWorkloadDto.getFindKeyword();
				if (target.equals("companyName")) {
					data = workloadRepository.findByCompanyIdx_CompanyNameLike("%" + keyword + "%", pageRequest);
					// 워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
					for (WorkloadEntity workload : data) {
						workload.setAvailableActionList(
								availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
					}
				} else {
					data = workloadRepository.findAll(pageRequest);
					// 워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
					for (WorkloadEntity workload : data) {
						workload.setAvailableActionList(
								availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
					}
				}
			} else {
				data = workloadRepository.findAll(pageRequest);
				// 워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
				for (WorkloadEntity workload : data) {
					workload.setAvailableActionList(
							availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
				}
			}
		} else {
			data = workloadRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
			// 워크로드ID로 사용가능한 액션목록을 조회한뒤 세팅한다.
			for (WorkloadEntity workload : data) {
				workload.setAvailableActionList(availableActionRepository.findByWorkloadId(workload.getWorkloadId()));
			}
		}

		result.put("status", "200");
		result.put("data", data);
		result.put("resultMsg", "정상조회되었습니다.");
		return result;
	}

	@Async
	public void asyncWorkload(ApiServerListEntity apiserverInfo) {
		/* 해당 API서버가 삭제되었거나 소속 회사가 삭제된 경우 시행하지 않음 */
		/* Parameters to access PlateSpin Protect Server */
		String userNameToAccessProtectServer = apiserverInfo.getUserNameToAccessProtectServer();
		String passwordToAccessProtectServer = apiserverInfo.getPasswordToAccessProtectServer();
		String domainNameToAccessProtectServer = apiserverInfo.getDomainNameToAccessProtectServer();
		String serverHost = apiserverInfo.getServerHost();
		ObjectMapper mapper = new ObjectMapper();

		WorkloadsDto tmpWorkloadList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd a KK:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("M/d/yyyy KK:mm:ss a");
		URI startingUri = null;
		try {
			startingUri = new URI("/protectionservices/Workloads/");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
			if (response1.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				tmpWorkloadList = mapper.readValue(EntityUtils.toString(response1.getEntity()), WorkloadsDto.class);
				for (int i = 0; i < tmpWorkloadList.getWorkloads().size(); i++) {
					HttpGet httpget2 = new HttpGet(tmpWorkloadList.getWorkloads().get(i).getUri());
					httpget2.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
					CloseableHttpResponse response2 = httpClient.execute(target, httpget2, context);
					if (response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						tmpWorkloadList.getWorkloads().set(i, mapper
								.readValue(EntityUtils.toString(response2.getEntity(), "UTF-8"), WorkloadDto.class));
						tmpWorkloadList.getWorkloads().get(i)
								.setCompanyName(apiserverInfo.getCompanyIdx().getCompanyName());
						tmpWorkloadList.getWorkloads().get(i).setWorkloadServerHost(serverHost);
						String workloadId = tmpWorkloadList.getWorkloads().get(i).getUri()
								.substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/") + 1);
						// 기존 워크로드ID가 존재하면 업데이트한다.
						if (workloadRepository.findByWorkloadId(workloadId) != null) {
							// 기존 워크로드 ID로 조회하여 가능한 액션 전부삭제
//							availableActionRepository.deleteInBatch(availableActionRepository.findByWorkloadId(workloadId));
							WorkloadEntity workloadEntity = new WorkloadEntity();
							workloadEntity = workloadRepository.findByWorkloadId(workloadId);
							workloadEntity.setServerHost(apiserverInfo.getServerHost());
//							workloadEntity.setWorkloadId(tmpWorkloadList.getWorkloads().get(i).getUri().substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/")+1));
							workloadEntity.setTargetId(tmpWorkloadList.getWorkloads().get(i).getContainerUri()
									.substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/") + 1));
							workloadEntity.setCurrentState(tmpWorkloadList.getWorkloads().get(i).getCurrentState());
							workloadEntity.setMachineName(tmpWorkloadList.getWorkloads().get(i).getMachineName());
							workloadEntity.setName(tmpWorkloadList.getWorkloads().get(i).getName());
							workloadEntity.setOnline(tmpWorkloadList.getWorkloads().get(i).getOnline());
							workloadEntity
									.setOperatingSystem(tmpWorkloadList.getWorkloads().get(i).getOperatingSystem());
							workloadEntity.setOperatingSystemVersion(
									tmpWorkloadList.getWorkloads().get(i).getOperatingSystemVersion());
							workloadEntity.setServicePack(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
							workloadEntity.setSourceMachinId(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
							workloadEntity.setUserName(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(0).get("Value"));
							workloadEntity.setDiscoveryAddress(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(1).get("Value"));
							workloadEntity.setAreBBTollsInstalled(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(2).get("Value"));
							workloadEntity.setReadyToCopySnapshotName(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(3).get("Value"));
							workloadEntity.setCanDeleteVm(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(4).get("Value"));
							workloadEntity.setCanRemoveSource(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(5).get("Value"));
							workloadEntity.setCanRemoveBBT(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(6).get("Value"));
							workloadEntity.setRunFailoverOnReplicationSuccess(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(7).get("Value"));
							workloadEntity.setIsRemoteWorkload(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(8).get("Value"));
							workloadEntity.setIsWindowsCluster(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(9).get("Value"));
							workloadEntity.setLastFullOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(10).get("Value"));
							workloadEntity.setLastIncrementalOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(11).get("Value"));
							workloadEntity.setLastTestedFailoverOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(12).get("Value"));
							workloadEntity.setLastUpdated(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(13).get("Value"));
							workloadEntity.setFailoverMachineId(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(14).get("Value"));
							workloadEntity.setNextFullOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(15).get("Value"));
							workloadEntity.setNextIncrementalOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(16).get("Value"));
							workloadEntity.setOnlineStatus(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(17).get("Value"));
							workloadEntity.setProtectionLevel(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(22).get("Value"));
							workloadEntity.setProtectionState(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(23).get("Value"));
							workloadEntity.setTargetPRO(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(24).get("Value"));
							workloadEntity.setWorkflowStep(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(25).get("Value"));
							workloadEntity.setWorkloadLifecycle(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(26).get("Value"));
							workloadEntity.setWorkloadGroupId(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(27).get("Value"));
							workloadEntity.setReplicationScheduleStatus(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(28).get("Value"));
							workloadEntity.setSourceMachineControllerAlias(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(29).get("Value"));
							workloadEntity.setPrepareForFailoverConfigurationUri(
									tmpWorkloadList.getWorkloads().get(i).getPrepareForFailoverConfigurationUri());
							workloadEntity.setScheduleActive(tmpWorkloadList.getWorkloads().get(i).getScheduleActive());
							workloadEntity.setSchedulesUri(tmpWorkloadList.getWorkloads().get(i).getSchedulesUri());
							workloadEntity.setTag(tmpWorkloadList.getWorkloads().get(i).getTag());
							workloadEntity.setTestCutoverMarkedSuccessful(
									tmpWorkloadList.getWorkloads().get(i).getTestCutoverMarkedSuccessful());
							workloadEntity.setTestFailoverConfigurationUri(
									tmpWorkloadList.getWorkloads().get(i).getTestFailoverConfigurationUri());
							workloadEntity.setTmData(tmpWorkloadList.getWorkloads().get(i).getTmData());
							workloadEntity.setWindowsServiceUri(
									tmpWorkloadList.getWorkloads().get(i).getWindowsServicesUri());
							workloadEntity.setWorkloadConfigurationUri(
									tmpWorkloadList.getWorkloads().get(i).getWorkloadConfigurationUri());
							workloadEntity.setCompanyIdx(apiserverInfo.getCompanyIdx());
							workloadEntity.setSyncDate(new Date());
							workloadRepository.save(workloadEntity);
							// 사용가능한 워크로드 액션들을 insert한다.
							for (int availableCount = 0; availableCount < tmpWorkloadList.getWorkloads().get(i)
									.getAvailableTransitions().size(); availableCount++) {
								AvailableActionEntity tempAvailableActionEntity = new AvailableActionEntity();
								tempAvailableActionEntity.setName(tmpWorkloadList.getWorkloads().get(i)
										.getAvailableTransitions().get(availableCount).get("Name"));
								tempAvailableActionEntity.setUri(tmpWorkloadList.getWorkloads().get(i)
										.getAvailableTransitions().get(availableCount).get("Uri"));
								tempAvailableActionEntity.setWorkloadId(workloadId);
								availableActionRepository.save(tempAvailableActionEntity);
							}
						} else { // 기존 워크로드ID가 존재하지 않으면 새로 insert
							// 사용가능한 워크로드 액션들을 insert한다.
							for (int availableCount = 0; availableCount < tmpWorkloadList.getWorkloads().get(i)
									.getAvailableTransitions().size(); availableCount++) {
								AvailableActionEntity tempAvailableActionEntity = new AvailableActionEntity();
								tempAvailableActionEntity.setName(tmpWorkloadList.getWorkloads().get(i)
										.getAvailableTransitions().get(availableCount).get("Name"));
								tempAvailableActionEntity.setUri(tmpWorkloadList.getWorkloads().get(i)
										.getAvailableTransitions().get(availableCount).get("Uri"));
								tempAvailableActionEntity.setWorkloadId(workloadId);
								availableActionRepository.save(tempAvailableActionEntity);
							}

							WorkloadEntity workloadEntity = new WorkloadEntity();
							workloadEntity.setServerHost(apiserverInfo.getServerHost());
							workloadEntity.setWorkloadId(tmpWorkloadList.getWorkloads().get(i).getUri()
									.substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/") + 1));
							workloadEntity.setTargetId(tmpWorkloadList.getWorkloads().get(i).getContainerUri()
									.substring(tmpWorkloadList.getWorkloads().get(i).getUri().lastIndexOf("/") + 1));
							workloadEntity.setCurrentState(tmpWorkloadList.getWorkloads().get(i).getCurrentState());
							workloadEntity.setMachineName(tmpWorkloadList.getWorkloads().get(i).getMachineName());
							workloadEntity.setName(tmpWorkloadList.getWorkloads().get(i).getName());
							workloadEntity.setOnline(tmpWorkloadList.getWorkloads().get(i).getOnline());
							workloadEntity
									.setOperatingSystem(tmpWorkloadList.getWorkloads().get(i).getOperatingSystem());
							workloadEntity.setOperatingSystemVersion(
									tmpWorkloadList.getWorkloads().get(i).getOperatingSystemVersion());
							workloadEntity.setServicePack(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(20).get("Value"));
							workloadEntity.setSourceMachinId(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(21).get("Value"));
							workloadEntity.setUserName(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(0).get("Value"));
							workloadEntity.setDiscoveryAddress(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(1).get("Value"));
							workloadEntity.setAreBBTollsInstalled(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(2).get("Value"));
							workloadEntity.setReadyToCopySnapshotName(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(3).get("Value"));
							workloadEntity.setCanDeleteVm(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(4).get("Value"));
							workloadEntity.setCanRemoveSource(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(5).get("Value"));
							workloadEntity.setCanRemoveBBT(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(6).get("Value"));
							workloadEntity.setRunFailoverOnReplicationSuccess(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(7).get("Value"));
							workloadEntity.setIsRemoteWorkload(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(8).get("Value"));
							workloadEntity.setIsWindowsCluster(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(9).get("Value"));
							workloadEntity.setLastFullOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(10).get("Value"));
							workloadEntity.setLastIncrementalOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(11).get("Value"));
							workloadEntity.setLastTestedFailoverOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(12).get("Value"));
							workloadEntity.setLastUpdated(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(13).get("Value"));
							workloadEntity.setFailoverMachineId(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(14).get("Value"));
							workloadEntity.setNextFullOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(15).get("Value"));
							workloadEntity.setNextIncrementalOn(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(16).get("Value"));
							workloadEntity.setOnlineStatus(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(17).get("Value"));
							workloadEntity.setProtectionLevel(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(22).get("Value"));
							workloadEntity.setProtectionState(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(23).get("Value"));
							workloadEntity.setTargetPRO(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(24).get("Value"));
							workloadEntity.setWorkflowStep(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(25).get("Value"));
							workloadEntity.setWorkloadLifecycle(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(26).get("Value"));
							workloadEntity.setWorkloadGroupId(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(27).get("Value"));
							workloadEntity.setReplicationScheduleStatus(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(28).get("Value"));
							workloadEntity.setSourceMachineControllerAlias(
									tmpWorkloadList.getWorkloads().get(i).getParameters().get(29).get("Value"));
							workloadEntity.setPrepareForFailoverConfigurationUri(
									tmpWorkloadList.getWorkloads().get(i).getPrepareForFailoverConfigurationUri());
							workloadEntity.setScheduleActive(tmpWorkloadList.getWorkloads().get(i).getScheduleActive());
							workloadEntity.setSchedulesUri(tmpWorkloadList.getWorkloads().get(i).getSchedulesUri());
							workloadEntity.setTag(tmpWorkloadList.getWorkloads().get(i).getTag());
							workloadEntity.setTestCutoverMarkedSuccessful(
									tmpWorkloadList.getWorkloads().get(i).getTestCutoverMarkedSuccessful());
							workloadEntity.setTestFailoverConfigurationUri(
									tmpWorkloadList.getWorkloads().get(i).getTestFailoverConfigurationUri());
							workloadEntity.setTmData(tmpWorkloadList.getWorkloads().get(i).getTmData());
							workloadEntity.setWindowsServiceUri(
									tmpWorkloadList.getWorkloads().get(i).getWindowsServicesUri());
							workloadEntity.setWorkloadConfigurationUri(
									tmpWorkloadList.getWorkloads().get(i).getWorkloadConfigurationUri());
							workloadEntity.setCompanyIdx(apiserverInfo.getCompanyIdx());
							workloadEntity.setSyncDate(new Date());
							workloadRepository.save(workloadEntity);

						}
					} else { // 워크로드 정보불러오기 실패시

					}
//					workloadList.add(tmpWorkloadList.getWorkloads().get(i));
				}

				// 워크로드 동기화 성공시
//				result.put("status", "200");
//				result.put("data", tmpWorkloadList);
//				return result;
			} else { // 워크로드 리스트 정보 불러오기 실패시
//				result.put("status", "-2");
//				result.put("data", null);
//				result.put("resultMsg", "접속실패 responseCode:" + response1.getStatusLine().getStatusCode());
//				return result;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("끝났어!!!" + Thread.currentThread().getName());
	}

	@Override
	@Async
	public void scheduleWorkloadAction(ScheduleEntity scheduleEntity) throws Exception {
		String workloadName = scheduleEntity.getWorkloadId().getName();
		writeLogService.createLogFile(workloadName);
		// TODO Auto-generated method stub
		String serverHost = scheduleEntity.getWorkloadId().getServerHost();
		String workloadId = scheduleEntity.getWorkloadId().getWorkloadId();
		

		ApiServerListEntity apiserverInfo = apiServerListRepository.findByServerHostAndDeletedYn(serverHost, "N");

		String userNameToAccessProtectServer = apiserverInfo.getUserNameToAccessProtectServer();
		String passwordToAccessProtectServer = apiserverInfo.getPasswordToAccessProtectServer();
		String domainNameToAccessProtectServer = apiserverInfo.getDomainNameToAccessProtectServer();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 없는 필드로 인한 오류 무시
		ModelMapper modelMapper = new ModelMapper();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentTime = sdf.parse(sdf.format(new Date()));
		Calendar currentCalendarDate = Calendar.getInstance();
		currentCalendarDate.setTime(currentTime);
		Date nextFullReplicationStartDate = null;
		Date nextIncrementalReplicationStartDate = null;

		if(scheduleEntity.getNextFullReplicationDate() != null) {
			nextFullReplicationStartDate = sdf.parse(scheduleEntity.getNextFullReplicationDate().toString());
		}
		
		if(scheduleEntity.getNextIncrementalReplicationDate() != null) {
			nextIncrementalReplicationStartDate = sdf.parse(scheduleEntity.getNextIncrementalReplicationDate().toString());
		}
		
		if(scheduleEntity.getFullReplicationDeletedYn().equals("N") && currentTime.compareTo(nextFullReplicationStartDate) >= 0) {
			Calendar nextFullReplicationDate = Calendar.getInstance();	//다음 풀복제 시간을 Calendar 형식으로 선언하기 위해 필요함.
			nextFullReplicationDate.setTime(nextFullReplicationStartDate);
			while(currentCalendarDate.compareTo(nextFullReplicationDate) >= 0) {
				nextFullReplicationDate.add(Calendar.MINUTE, scheduleEntity.getFullReplicationInterval());	//스케줄 주기를 분단위로 치환하여 더하여 다음 리플리케이션 시간을 설정한다.
			}				
			scheduleEntity.setNextFullReplicationDate(nextFullReplicationDate.getTime());
			scheduleEntity.setFullWorkingStatus("Y");
			scheduleRepository.save(scheduleEntity);
		}

		//풀복제
		if (scheduleEntity.getFullReplicationDeletedYn().equals("N") && scheduleEntity.getFullWorkingStatus().equals("Y")) {
		
			List<AvailableActionEntity> availableActionEntityList = availableActionRepository.findByWorkloadId(workloadId);
			
			if (scheduleEntity.getWorkloadId().getCurrentState().equals("Idle")) {
				AvailableActionEntity fullReplicationActionEntity = new AvailableActionEntity();
				AvailableActionEntity testFailOverActionEntity = new AvailableActionEntity();
				
				for(AvailableActionEntity tempEntity : availableActionEntityList) {
					if(tempEntity.getName().equals("RunReplication")) {
						fullReplicationActionEntity = tempEntity;
					}else if(tempEntity.getName().equals("TestFailover")) {
						testFailOverActionEntity = tempEntity;
					}
				}
				
				if (scheduleEntity.getScheduleStatus() != 1 && fullReplicationActionEntity != null) { //현재 Idle 상태이며 아무 문제 없을때 Replication 진행
					CloseableHttpResponse actionResponse = ntlmGetPostService.postRequest(userNameToAccessProtectServer,
							passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, fullReplicationActionEntity.getUri());
					
					if(actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK && actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
						writeLogService.writeLogFile(workloadName, "요청 실패, 요청 URL:" + serverHost + fullReplicationActionEntity.getUri());
						return;
					}
					
					HashMap<String, Object> responseResult = new HashMap<String, Object>();
					responseResult = mapper.readValue(EntityUtils.toString(actionResponse.getEntity()), new TypeReference<HashMap<String, Object>>() {});
					scheduleEntity.setScheduleStatus(1);
					scheduleEntity.setOperationUri(responseResult.get("OperationUri").toString());
					scheduleEntity.setFullReplicationStartDate(currentTime);
					scheduleRepository.save(scheduleEntity);
					writeLogService.writeLogFile(workloadName, "Run Replication 시작!");
					
				}else if(scheduleEntity.getScheduleStatus() == 1) { // 스케줄로 진행한 Replication이 종료되고 Idle 상태일때 TestFailOver 진행한다.				
					CloseableHttpResponse operationResponse = ntlmGetPostService.getRequest(userNameToAccessProtectServer,
							passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, scheduleEntity.getOperationUri());
					
					//정상 요청 되지 않으면 종료
					if(operationResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
						return;
					
					WorkloadOperationDTO workloadOperationValue = mapper.readValue(EntityUtils.toString(operationResponse.getEntity()), WorkloadOperationDTO.class);
					
					//작업이 아직 종료되지 않았다면 종료
					if(!workloadOperationValue.getIsFinished().equals("true"))
						return;
					//풀 리플리케이션이 정상 종료되었으면 Test FailOver 진행
					if(workloadOperationValue.getIsSucceeded().equals("true")) {	
						writeLogService.writeLogFile(workloadName, "Run Replication이 정상 종료되었습니다.");
						writeLogService.writeLogFile(workloadName, workloadOperationValue.getOperationResults().toString());
						CloseableHttpResponse actionResponse = ntlmGetPostService.postRequest(userNameToAccessProtectServer,
								passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, testFailOverActionEntity.getUri());
						
						if(actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK && actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
							writeLogService.writeLogFile(workloadName, "요청 실패, 요청 URL:" + serverHost + testFailOverActionEntity.getUri());
							return;
						}
						
						HashMap<String, Object> responseResult = new HashMap<String, Object>();
						responseResult = mapper.readValue(EntityUtils.toString(actionResponse.getEntity()), new TypeReference<HashMap<String, Object>>() {});
						scheduleEntity.setScheduleStatus(2);
						scheduleEntity.setOperationUri(responseResult.get("OperationUri").toString());
						scheduleRepository.save(scheduleEntity);
						writeLogService.writeLogFile(workloadName, "Test FailOver 시작!");	
						
					}else if(workloadOperationValue.getIsSucceeded().equals("false")) {
						scheduleEntity.setScheduleStatus(0);
						scheduleEntity.setFullWorkingStatus("N");
//						scheduleEntity.setNextFullReplicationDate(nextFullReplicationDate.getTime());
						scheduleRepository.save(scheduleEntity);
						writeLogService.writeLogFile(workloadName, "Run Replication이 실패하였습니다.");
						writeLogService.writeLogFile(workloadName, workloadOperationValue.getOperationResults().toString());
						writeLogService.writeLogFile(workloadName, "다음 스케줄로 세팅됩니다.");
					}
					
				}
				
				if(syncWorkload(serverHost, workloadId) == false) {
					writeLogService.writeLogFile(workloadName, "워크로드 새로고침 실패!");
				}
				
			}else if(scheduleEntity.getScheduleStatus() == 2) { //TestFailover 진행중 상태체크
				//현재상태 체크 호출
				CloseableHttpResponse operationResponse = ntlmGetPostService.getRequest(userNameToAccessProtectServer,
						passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, scheduleEntity.getOperationUri());
				if(operationResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
					return;
				
				WorkloadOperationDTO workloadOperationValue = mapper.readValue(EntityUtils.toString(operationResponse.getEntity()), WorkloadOperationDTO.class); 
				//TestFailover 정상 완료됐을 경우
				if(!workloadOperationValue.getIsFinished().equals("true"))
					return;
				
				Date finishedDate = new Date(Long.parseLong(workloadOperationValue.getFinishedAt().substring(6, 19)) + 9*60*60*1000); //외국시간이라 9시간 더해줘야 한국시간임.
				if(workloadOperationValue.getIsSucceeded().equals("true")) {					
					scheduleEntity.setScheduleStatus(3);
//					scheduleEntity.setNextFullReplicationDate(nextFullReplicationDate.getTime());
					scheduleEntity.setFullWorkingStatus("N");
					scheduleEntity.setFullReplicationFinishedDate(finishedDate);
					scheduleRepository.save(scheduleEntity);
					writeLogService.writeLogFile(workloadName, "Test FailOver 상태입니다.!");
					writeLogService.writeLogFile(workloadName, "다음 스케줄로 세팅됩니다.");
						
				}else if(workloadOperationValue.getIsSucceeded().equals("false")) {
					scheduleEntity.setScheduleStatus(0);
//					scheduleEntity.setNextFullReplicationDate(nextFullReplicationDate.getTime());
					scheduleEntity.setFullWorkingStatus("N");
					scheduleEntity.setFullReplicationFinishedDate(finishedDate);
					scheduleRepository.save(scheduleEntity);
					writeLogService.writeLogFile(workloadName, "Test FailOver가 실패하였습니다.");
					writeLogService.writeLogFile(workloadName, workloadOperationValue.getOperationResults().toString());
					writeLogService.writeLogFile(workloadName, "다음 스케줄로 세팅됩니다.");
				}
				
			}else if(scheduleEntity.getWorkloadId().getCurrentState().equals("WaitingForCancelTestFailover")) {
				AvailableActionEntity cancelFailoverActionEntity = availableActionRepository.findTopByWorkloadIdAndName(workloadId, "CancelFailover");
				CloseableHttpResponse actionResponse = ntlmGetPostService.postRequest(userNameToAccessProtectServer,
						passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, cancelFailoverActionEntity.getUri());
				if(actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK && actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
					writeLogService.writeLogFile(workloadName, "요청 실패, 요청 URL:" + serverHost + cancelFailoverActionEntity.getUri());
					return;
				}
					
				HashMap<String, Object> responseResult = new HashMap<String, Object>();
				responseResult = mapper.readValue(EntityUtils.toString(actionResponse.getEntity()), new TypeReference<HashMap<String, Object>>() {});
				scheduleEntity.setScheduleStatus(0);
				scheduleEntity.setOperationUri(responseResult.get("OperationUri").toString());
				scheduleRepository.save(scheduleEntity);
				writeLogService.writeLogFile(workloadName, "스케줄 시간에 도달하여 Test FailOver 취소요청하였습니다.");
				
				if(syncWorkload(serverHost, workloadId) == false) {
					writeLogService.writeLogFile(workloadName, "워크로드 새로고침 실패!");
				}
					
			}else if(scheduleEntity.getWorkloadId().getCurrentState().equals("RunningIncrementalAndTestFailover")) {
				AvailableActionEntity avortActionEntity = availableActionRepository.findTopByWorkloadIdAndName(workloadId, "Abort");
				CloseableHttpResponse actionResponse = ntlmGetPostService.postRequest(userNameToAccessProtectServer,
						passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, avortActionEntity.getUri());
				if(actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK && actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
					writeLogService.writeLogFile(workloadName, "요청 실패, 요청 URL:" + serverHost + avortActionEntity.getUri());
					return;
				}
				
				scheduleEntity.setScheduleStatus(0);
				scheduleEntity.setOperationUri(null);
				
				scheduleRepository.save(scheduleEntity);
				writeLogService.writeLogFile(workloadName, "스케줄 시간에 도달하였으나, RunningIncrementalAndTestFailover중이기 때문에 취소요청 하였습니다.");	
				if(syncWorkload(serverHost, workloadId) == false) {
					writeLogService.writeLogFile(workloadName, "워크로드 새로고침 실패!");
				}
				
			}
		//증분 복제
		}else if(scheduleEntity.getIncrementalReplicationDeletedYn().equals("N") &&currentTime.compareTo(nextIncrementalReplicationStartDate) >= 0 ) {
			Calendar nextIncrementalReplicationDate = Calendar.getInstance();
			nextIncrementalReplicationDate.setTime(nextIncrementalReplicationStartDate);
			while(currentCalendarDate.compareTo(nextIncrementalReplicationDate) >= 0) {
				nextIncrementalReplicationDate.add(Calendar.MINUTE, scheduleEntity.getIncrementalReplicationInterval());	//스케줄 주기를 분단위로 치환하여 더하여 다음 리플리케이션 시간을 설정한다.
			}	
			
			List<AvailableActionEntity> availableActionEntityList = availableActionRepository.findByWorkloadId(workloadId);
			
			if (scheduleEntity.getWorkloadId().getCurrentState().equals("Idle")) {
				AvailableActionEntity incrementalAndTestFailOverActionEntity = new AvailableActionEntity();
				
				for(AvailableActionEntity tempEntity : availableActionEntityList) {
					if(tempEntity.getName().equals("RunIncrementalAndTestFailover")) {
						incrementalAndTestFailOverActionEntity = tempEntity;
					}
				}
				
				CloseableHttpResponse actionResponse = ntlmGetPostService.postRequest(userNameToAccessProtectServer,
						passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, incrementalAndTestFailOverActionEntity.getUri());
					
				if(actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK && actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
					writeLogService.writeLogFile(workloadName, "요청 실패, 요청 URL:" + serverHost + incrementalAndTestFailOverActionEntity.getUri());
					return;
				}
					
				HashMap<String, Object> responseResult = new HashMap<String, Object>();
				responseResult = mapper.readValue(EntityUtils.toString(actionResponse.getEntity()), new TypeReference<HashMap<String, Object>>() {});
				scheduleEntity.setIncrementalReplicationStartDate(currentTime);
				scheduleEntity.setScheduleStatus(4);
				scheduleEntity.setOperationUri(responseResult.get("OperationUri").toString());
				scheduleRepository.save(scheduleEntity);
				writeLogService.writeLogFile(workloadName, "Run Incremental And TestFailOver 시작!");
				
				if(syncWorkload(serverHost, workloadId) == false) {
					writeLogService.writeLogFile(workloadName, "워크로드 새로고침 실패!");
				}
					
			}else if(scheduleEntity.getScheduleStatus() == 4) { //TestFailover 진행중 상태체크
				//현재상태 체크 호출
				CloseableHttpResponse operationResponse = ntlmGetPostService.getRequest(userNameToAccessProtectServer,
						passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, scheduleEntity.getOperationUri());
				if(operationResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
					return;
				
				WorkloadOperationDTO workloadOperationValue = mapper.readValue(EntityUtils.toString(operationResponse.getEntity()), WorkloadOperationDTO.class); 
				//TestFailover 정상 완료됐을 경우
				if(!workloadOperationValue.getIsFinished().equals("true"))
					return;
				
				Date finishedDate = new Date(Long.parseLong(workloadOperationValue.getFinishedAt().substring(6, 19)) + 9*60*60*1000); //외국시간이라 9시간 더해줘야 한국시간임.
				if(workloadOperationValue.getIsSucceeded().equals("true")) {					
					scheduleEntity.setScheduleStatus(3);
					scheduleEntity.setNextIncrementalReplicationDate(nextIncrementalReplicationDate.getTime());
					scheduleEntity.setIncrementalReplicationFinishedDate(finishedDate);
					scheduleRepository.save(scheduleEntity);
					writeLogService.writeLogFile(workloadName, "Test FailOver 상태입니다.!");
					writeLogService.writeLogFile(workloadName, "다음 스케줄로 세팅됩니다.");
						
				}else if(workloadOperationValue.getIsSucceeded().equals("false")) {
					scheduleEntity.setScheduleStatus(0);
					scheduleEntity.setNextIncrementalReplicationDate(nextIncrementalReplicationDate.getTime());
					scheduleEntity.setIncrementalReplicationFinishedDate(finishedDate);
					scheduleRepository.save(scheduleEntity);
					writeLogService.writeLogFile(workloadName, "RunIncremental And Test FailOver가 실패하였습니다.");
					writeLogService.writeLogFile(workloadName, workloadOperationValue.getOperationResults().toString());
					writeLogService.writeLogFile(workloadName, "다음 스케줄로 세팅됩니다.");
				}
				
			}else if(scheduleEntity.getWorkloadId().getCurrentState().equals("WaitingForCancelTestFailover")) {
				AvailableActionEntity cancelFailoverActionEntity = availableActionRepository.findTopByWorkloadIdAndName(workloadId, "CancelFailover");
				CloseableHttpResponse actionResponse = ntlmGetPostService.postRequest(userNameToAccessProtectServer,
						passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, cancelFailoverActionEntity.getUri());
				if(actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK && actionResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
					writeLogService.writeLogFile(workloadName, "요청 실패, 요청 URL:" + serverHost + cancelFailoverActionEntity.getUri());
					return;
				}
					
				HashMap<String, Object> responseResult = new HashMap<String, Object>();
				responseResult = mapper.readValue(EntityUtils.toString(actionResponse.getEntity()), new TypeReference<HashMap<String, Object>>() {});
				scheduleEntity.setScheduleStatus(0);
				scheduleEntity.setOperationUri(responseResult.get("OperationUri").toString());
				scheduleRepository.save(scheduleEntity);
				writeLogService.writeLogFile(workloadName, "스케줄 시간에 도달하여 Test FailOver 취소요청하였습니다.");
				
				if(syncWorkload(serverHost, workloadId) == false) {
					writeLogService.writeLogFile(workloadName, "워크로드 새로고침 실패!");
				}
				
			}
			
		}

	}

	@Override
	public HashMap<String, Object> postWorkloadSchedule(RequestScheduleDTO requestScheduleDTO) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = new HashMap<String, Object>();
		ScheduleEntity scheduleEntity = scheduleRepository.findByWorkloadId_WorkloadId(requestScheduleDTO.getWorkloadId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		result.put("status", 200);
		result.put("success", true);
		
		//기존 스케줄이 있다면 업데이트 해준다.
		if(scheduleEntity != null) {
			if(requestScheduleDTO.getFullReplicationDeletedYn().equals("N")) {
				scheduleEntity.setNextFullReplicationDate(sdf.parse(requestScheduleDTO.getNextFullReplicationDate()));
				scheduleEntity.setFullReplicationInterval(requestScheduleDTO.getFullReplicationInterval());
				scheduleEntity.setFullReplicationDeletedYn(requestScheduleDTO.getFullReplicationDeletedYn());
			}else if(requestScheduleDTO.getFullReplicationDeletedYn().equals("Y")) {
				scheduleEntity.setFullReplicationDeletedYn(requestScheduleDTO.getFullReplicationDeletedYn());
//				scheduleEntity.setFullReplicationInterval(0);
				scheduleEntity.setNextFullReplicationDate(null);
			}
			
			if(requestScheduleDTO.getIncrementalReplicationDeletedYn().equals("N")) {
				scheduleEntity.setNextIncrementalReplicationDate(sdf.parse(requestScheduleDTO.getNextIncrementalReplicationDate()));
				scheduleEntity.setIncrementalReplicationInterval(requestScheduleDTO.getIncrementalReplicationInterval());
				scheduleEntity.setIncrementalReplicationDeletedYn(requestScheduleDTO.getIncrementalReplicationDeletedYn());
			}else if(requestScheduleDTO.getIncrementalReplicationDeletedYn().equals("Y")) {
				scheduleEntity.setIncrementalReplicationDeletedYn(requestScheduleDTO.getIncrementalReplicationDeletedYn());
//				scheduleEntity.setIncrementalReplicationInterval(0);
				scheduleEntity.setNextIncrementalReplicationDate(null);
			}
			
			scheduleRepository.save(scheduleEntity);
			result.put("data", scheduleEntity);
		}else {
			WorkloadEntity workloadEntity = workloadRepository.findByWorkloadId(requestScheduleDTO.getWorkloadId());
			if(workloadEntity == null) {
				result.put("data", null);
				result.put("status", 200);
				result.put("success", false);
				result.put("resultMsg", "해당 워크로드ID가 없습니다.");
				return result;
			}	
			
			scheduleEntity = new ScheduleEntity();
			scheduleEntity.setWorkloadId(workloadEntity);
			if(requestScheduleDTO.getFullReplicationDeletedYn().equals("N")) {
				scheduleEntity.setNextFullReplicationDate(sdf.parse(requestScheduleDTO.getNextFullReplicationDate()));
				scheduleEntity.setFullReplicationInterval(requestScheduleDTO.getFullReplicationInterval());
				scheduleEntity.setFullReplicationDeletedYn(requestScheduleDTO.getFullReplicationDeletedYn());
			}else if(requestScheduleDTO.getFullReplicationDeletedYn().equals("Y")) {
				scheduleEntity.setFullReplicationDeletedYn(requestScheduleDTO.getFullReplicationDeletedYn());
//				scheduleEntity.setFullReplicationInterval(0);
				scheduleEntity.setNextFullReplicationDate(null);
			}
			
			if(requestScheduleDTO.getIncrementalReplicationDeletedYn().equals("N")) {
				scheduleEntity.setNextIncrementalReplicationDate(sdf.parse(requestScheduleDTO.getNextIncrementalReplicationDate()));
				scheduleEntity.setIncrementalReplicationInterval(requestScheduleDTO.getIncrementalReplicationInterval());
				scheduleEntity.setIncrementalReplicationDeletedYn(requestScheduleDTO.getIncrementalReplicationDeletedYn());
			}else if(requestScheduleDTO.getIncrementalReplicationDeletedYn().equals("Y")) {
				scheduleEntity.setIncrementalReplicationDeletedYn(requestScheduleDTO.getIncrementalReplicationDeletedYn());
//				scheduleEntity.setIncrementalReplicationInterval(0);
				scheduleEntity.setNextIncrementalReplicationDate(null);
			}
			
			scheduleRepository.save(scheduleEntity);
			result.put("data", scheduleEntity);
			
		}
		
		return result;
	}
	
	
	@Override
	public boolean syncWorkload(String serverHost, String workloadId) throws Exception {
		ApiServerListEntity apiserverInfo = apiServerListRepository.findByServerHostAndDeletedYn(serverHost, "N");
		String userNameToAccessProtectServer = apiserverInfo.getUserNameToAccessProtectServer();
		String passwordToAccessProtectServer = apiserverInfo.getPasswordToAccessProtectServer();
		String domainNameToAccessProtectServer = apiserverInfo.getDomainNameToAccessProtectServer();
		ObjectMapper mapper = new ObjectMapper();
	
		CloseableHttpResponse response2 = ntlmGetPostService.getRequest(userNameToAccessProtectServer, passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost, "/protectionservices/Workloads/" + workloadId);
		if (response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			WorkloadDto tmpWorkload = mapper.readValue(EntityUtils.toString(response2.getEntity(), "UTF-8"),
					WorkloadDto.class);

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
			workloadEntity
					.setPrepareForFailoverConfigurationUri(tmpWorkload.getPrepareForFailoverConfigurationUri());
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
			workloadRepository.save(workloadEntity);
			// 해당 워크로드ID로 워크로드 액션 삭제
			availableActionRepository.deleteInBatch(availableActionRepository.findByWorkloadId(workloadId));
			// 사용가능한 워크로드 액션들을 insert한다.
			for (int availableCount = 0; availableCount < tmpWorkload.getAvailableTransitions()
					.size(); availableCount++) {
				AvailableActionEntity tempAvailableActionEntity = new AvailableActionEntity();
				tempAvailableActionEntity
						.setName(tmpWorkload.getAvailableTransitions().get(availableCount).get("Name"));
				tempAvailableActionEntity
						.setUri(tmpWorkload.getAvailableTransitions().get(availableCount).get("Uri"));
				tempAvailableActionEntity.setWorkloadId(workloadId);
				availableActionRepository.save(tempAvailableActionEntity);
			}
			
			return true;
		}
		
		return false;
	}
}