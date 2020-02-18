package com.psm.api.company.dto;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class FindCompanyDto {
	@Value(value="0")
	private int page = 1;						//현재 페이지
	@Value(value="10")
	private int pageSize = 10;
	private String findTarget;
	private String findKeyword;
}
