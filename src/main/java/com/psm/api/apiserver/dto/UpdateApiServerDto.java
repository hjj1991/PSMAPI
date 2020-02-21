package com.psm.api.apiserver.dto;

import lombok.Data;

@Data
public class UpdateApiServerDto {
	int apiserverIdx;
	private String userNameToAccessProtectServer;
	private String passwordToAccessProtectServer;
	private String domainNameToAccessProtectServer;
	private String serverHost;
	private String deletedYn;
	private String companyName;
	
}
