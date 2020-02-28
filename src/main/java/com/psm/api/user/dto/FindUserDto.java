package com.psm.api.user.dto;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class FindUserDto {
	@Value(value="0")
	private int page = 1;						//현재 페이지
	@Value(value="100")
	private int pageSize = 100;
	private String findTarget;
	private String findKeyword;
}
