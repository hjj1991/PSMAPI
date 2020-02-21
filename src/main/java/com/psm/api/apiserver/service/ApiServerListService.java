package com.psm.api.apiserver.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;

import com.psm.api.apiserver.dto.FindApiServerDto;
import com.psm.api.apiserver.dto.InsertApiServerDto;
import com.psm.api.apiserver.dto.UpdateApiServerDto;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.company.dto.UpdateCompanyDto;

public interface ApiServerListService {
	HashMap<String, Object> findApiServer(FindApiServerDto findApiServerDto);

	HashMap<String, Object> insertApiServer(List<InsertApiServerDto> insertApiServerList);

	ApiServerListEntity updateApiServer(UpdateApiServerDto updateApiServerValue);

	HashMap<String, Object> deleteApiServer(List<String> deleteApiServerIdxList);
}
