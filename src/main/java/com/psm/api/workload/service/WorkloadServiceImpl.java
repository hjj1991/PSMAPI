package com.psm.api.workload.service;

import java.net.URI;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psm.api.workload.dto.WorkloadDto;
import com.psm.api.workload.dto.WorkloadsDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
//@Transactional
public class WorkloadServiceImpl implements WorkloadService {
	@Override
	public WorkloadsDto getBoardList() throws Exception {
		/* Parameters to access PlateSpin Protect Server */
		String userNameToAccessProtectServer = "administrator";
		String passwordToAccessProtectServer = "vortmasp12#$";
		String domainNameToAccessProtectServer = "platespin";
		String serverHost = "migrate.eonit.co.kr";
		ObjectMapper mapper = new ObjectMapper();
		WorkloadsDto workloadList = null;
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
			for(WorkloadDto workload: workloadList.getWorkloads()) {
				HttpGet httpget2 = new HttpGet(workload.getUri());
				httpget2.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
				CloseableHttpResponse response2 = httpClient.execute(target, httpget2, context);
				if(response2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//					workload.set
					System.out.println(EntityUtils.toString(response2.getEntity()));
				}
				
			}
				
		}

		
 
		return workloadList;
	}

}
