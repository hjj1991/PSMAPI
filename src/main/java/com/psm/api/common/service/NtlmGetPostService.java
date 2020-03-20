package com.psm.api.common.service;

import org.apache.http.HttpHost;
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
import org.springframework.stereotype.Service;

@Service
public class NtlmGetPostService {

	
	public CloseableHttpResponse getRequest(String userNameToAccessProtectServer, String passwordToAccessProtectServer, String domainNameToAccessProtectServer, String serverHost, String actionUrl) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(userNameToAccessProtectServer,
				passwordToAccessProtectServer, domainNameToAccessProtectServer, serverHost));
		HttpHost target = new HttpHost(serverHost, 80, "http");

		// Make sure the same context is used to execute logically related requests
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);

		// Execute a cheap method first. This will trigger NTLM authentication
		HttpGet httpGet = new HttpGet(actionUrl);
		httpGet.addHeader("Accept", "application/vnd.netiq.platespin.protect.ServerConfiguration+json");
		
		
		CloseableHttpResponse response = httpClient.execute(target, httpGet, context);
    	return response;
	}
	
	public CloseableHttpResponse postRequest(String userNameToAccessProtectServer, String passwordToAccessProtectServer, String domainNameToAccessProtectServer, String serverHost, String actionUrl) throws Exception {
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
		CloseableHttpResponse response = httpClient.execute(target, httpPost, context);
		
    	return response;
	}
}
