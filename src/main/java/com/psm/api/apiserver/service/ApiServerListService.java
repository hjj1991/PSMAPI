package com.psm.api.apiserver.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;

import com.psm.api.apiserver.dto.FindApiServerDto;
import com.psm.api.apiserver.dto.InsertApiServerDto;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.company.dto.UpdateCompanyDto;

public interface ApiServerListService {
	Page<ApiServerListEntity> findApiServer(FindApiServerDto findApiServerDto);

	HashMap<String, Object> insertApiServer(List<InsertApiServerDto> insertApiServerList);

	ApiServerListEntity updateApiServer(UpdateCompanyDto updateApiServerValue);

	HashMap<String, Object> deleteApiServer(List<String> deleteApiServerIdxList);
}
