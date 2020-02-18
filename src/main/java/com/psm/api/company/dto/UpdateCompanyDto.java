package com.psm.api.company.dto;

import lombok.Data;

@Data
public class UpdateCompanyDto {
	int companyIdx;
	String companyId;
	String companyName;
	String deletedYn;
}
