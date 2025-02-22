package com.psm.api.company.service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;

import com.psm.api.company.dto.FindCompanyDto;
import com.psm.api.company.dto.InsertCompanyDto;
import com.psm.api.company.dto.ResponseSimpleCompanyList;
import com.psm.api.company.dto.UpdateCompanyDto;
import com.psm.api.company.entity.CompanyEntity;

public interface CompanyService {

	Page<CompanyEntity> findCompany(FindCompanyDto findCompanyDto);

	HashMap<String, Object> insertCompany(List<InsertCompanyDto> insertCompanyList);

	CompanyEntity updateCompany(UpdateCompanyDto updateCompanyValue);

	HashMap<String, Object> deleteCompany(List<String> deleteCompanyIdxList);

	List<ResponseSimpleCompanyList> simpleCompanyList();

}
