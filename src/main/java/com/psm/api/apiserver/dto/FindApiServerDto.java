package com.psm.api.apiserver.dto;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class FindApiServerDto {
	@Value(value="0")
	private int page = 1;						//현재 페이지
	@Value(value="10")
	private int pageSize = 10;
	private String inCompanyName;
	private String userRole;
	private String findTarget;
	private String findKeyword;
}
