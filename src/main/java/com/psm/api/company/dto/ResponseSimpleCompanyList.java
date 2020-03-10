package com.psm.api.company.dto;

import lombok.Data;

/*
 * 회사 Idx, Name만 추출하여 List로 내보내는 DTO입니다.
 */
@Data
public class ResponseSimpleCompanyList {
	int companyIdx;
	String companyName;
}
