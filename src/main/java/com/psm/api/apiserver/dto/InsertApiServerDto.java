package com.psm.api.apiserver.dto;

import lombok.Data;

@Data
public class InsertApiServerDto {
	String companyName;
	String domainNameToAccessProtectServer;
	String passwordToAccessProtectServer;
	String userNameToAccessProtectServer;
	String serverHost;
}
