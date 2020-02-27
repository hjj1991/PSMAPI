package com.psm.api.apiserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.psm.api.apiserver.dto.FindApiServerDto;
import com.psm.api.apiserver.dto.InsertApiServerDto;
import com.psm.api.apiserver.dto.UpdateApiServerDto;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.apiserver.repository.ApiServerListRepository;
import com.psm.api.apiserver.repository.PagingApiServerListRepository;
import com.psm.api.common.exception.CCompanyNotFoundException;
import com.psm.api.company.dto.InsertCompanyDto;
import com.psm.api.company.dto.UpdateCompanyDto;
import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.company.repository.CompanyRepository;

@Service
public class ApiServerListServiceImpl implements ApiServerListService {
	

	@Autowired
	CompanyRepository companyRepository;
	@Autowired
	ApiServerListRepository apiServerListRepository;
	@Autowired
	PagingApiServerListRepository pagingApiServerListRepository;
	@Override
	public HashMap<String, Object> findApiServer(FindApiServerDto findApiServerDto) {
		// TODO Auto-generated method stub
//		if(findCompanyDto.getPage())
		int companyIdx; //회사 idx
		HashMap<String, Object> result = new HashMap<String, Object>();
		Page<ApiServerListEntity> data = null;
		List<String> companyLsit = new ArrayList<String>();
		
		
		
		//소속회사 목록을 구한다.
		List<CompanyEntity> companyEntityList = companyRepository.findAll();
		for(CompanyEntity companyEntity : companyEntityList) {
			companyLsit.add(companyEntity.getCompanyName());
		}
		
		//소속회사이름으로 회사 idx번호를 구한다.
		companyIdx = companyRepository.findByCompanyName(findApiServerDto.getInCompanyName()).getCompanyIdx();
		Pageable pageRequest = PageRequest.of(findApiServerDto.getPage() - 1,  findApiServerDto.getPageSize(), Sort.by("deletedYn").ascending());
		if(findApiServerDto.getUserRole().equals("ROLE_MASTER")) {		
			if(findApiServerDto.getFindTarget() != null && findApiServerDto.getFindKeyword() != null) {
				String target = findApiServerDto.getFindTarget();
				String keyword = findApiServerDto.getFindKeyword();
				if(target.equals("serverHost")) {
					data = pagingApiServerListRepository.findByServerHostLike("%" + keyword + "%", pageRequest);
				}else {
					data = pagingApiServerListRepository.findAll(pageRequest);
				}
			}else {
				data = pagingApiServerListRepository.findAll(pageRequest);
			}
		}else {
			if(findApiServerDto.getFindTarget() != null && findApiServerDto.getFindKeyword() != null) {
				String target = findApiServerDto.getFindTarget();
				String keyword = findApiServerDto.getFindKeyword();
				if(target.equals("serverHost")) {
					data = pagingApiServerListRepository.findByServerHostLikeAndCompanyIdx_CompanyIdx("%" + keyword + "%", companyIdx, pageRequest);
					System.out.println(data);
				}else {
					data = pagingApiServerListRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
				}
			}else {
				data = pagingApiServerListRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
			}		
		}
		result.put("data", data);
		result.put("companyList", companyLsit);
		return result;
	}
	
	@Override
	public HashMap<String, Object> insertApiServer(List<InsertApiServerDto> insertApiServerList) {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Object> data = new HashMap<String, Object>();
		int successInsertCount = 0;
		int failInsertCount = 0;
		List<String> failNameList = new ArrayList<String>();
		for (InsertApiServerDto apiServerInfo : insertApiServerList) {
			ApiServerListEntity insertApiServer = new ApiServerListEntity();
			//중복 IP가 존재하지 않아여야함.
			if(apiServerListRepository.findByServerHost(apiServerInfo.getServerHost()) == null){
				insertApiServer.setCompanyIdx(companyRepository.findByCompanyName(apiServerInfo.getCompanyName()));
//				insertApiServer.setDomainNameToAccessProtectServer(apiServerInfo.getDomainNameToAccessProtectServer());
				insertApiServer.setDomainNameToAccessProtectServer("platespin");
				insertApiServer.setPasswordToAccessProtectServer(apiServerInfo.getPasswordToAccessProtectServer());
				insertApiServer.setServerHost(apiServerInfo.getServerHost());
				insertApiServer.setUserNameToAccessProtectServer(apiServerInfo.getUserNameToAccessProtectServer());
				apiServerListRepository.save(insertApiServer);
				successInsertCount++;
			}else {
				failInsertCount++;
				failNameList.add(apiServerInfo.getServerHost());
			}
		}
		data.put("successInsertCount", successInsertCount);
		data.put("failInsertCount", failInsertCount);
		data.put("failNameList", failNameList);
		result.put("data", data);
		if(successInsertCount == 0) {
			result.put("success", false);
			result.put("code", -1);
			result.put("msg", "실패하였습니다.");
		}else {
			result.put("success", true);
			result.put("code", 0);
			result.put("msg", "성공하였습니다.");
		}
		return result;
	}

	@Override
	public ApiServerListEntity updateApiServer(UpdateApiServerDto updateApiServerValue) {
		// TODO Auto-generated method stub
		
		CompanyEntity companyEntity = companyRepository.findByCompanyName(updateApiServerValue.getCompanyName());
		ApiServerListEntity updateEntity = apiServerListRepository.findById(updateApiServerValue.getApiserverIdx()).orElseThrow(CCompanyNotFoundException::new);
		if(updateApiServerValue.getDomainNameToAccessProtectServer() != null) {
			updateEntity.setDomainNameToAccessProtectServer(updateApiServerValue.getDomainNameToAccessProtectServer());
		}
		if(updateApiServerValue.getPasswordToAccessProtectServer() != null) {
			updateEntity.setPasswordToAccessProtectServer(updateApiServerValue.getPasswordToAccessProtectServer());
		}
		if(updateApiServerValue.getServerHost() != null) {
			updateEntity.setServerHost(updateApiServerValue.getServerHost());
		}
		if(updateApiServerValue.getUserNameToAccessProtectServer() != null) {
			updateEntity.setUserNameToAccessProtectServer(updateApiServerValue.getUserNameToAccessProtectServer());
		}
		if(updateApiServerValue.getDeletedYn() != null) {
			updateEntity.setDeletedYn(updateApiServerValue.getDeletedYn());
		}
		updateEntity.setCompanyIdx(companyEntity);
		updateEntity = apiServerListRepository.save(updateEntity);
		
		return updateEntity;
	}

	@Override
	public HashMap<String, Object> deleteApiServer(List<String> deleteApiServerIdxList) {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = new HashMap<String, Object>();
		int successDeleteCount = 0;
		int failDeleteCount = 0;
		List<String> deletedCompanyList = new ArrayList<String>();
		for(String apiServerIdx : deleteApiServerIdxList) {
			ApiServerListEntity deleteEntity = apiServerListRepository.findById(Integer.valueOf(apiServerIdx)).orElseThrow(CCompanyNotFoundException::new);
			deleteEntity.setDeletedYn("Y");
			deleteEntity = apiServerListRepository.save(deleteEntity);
			successDeleteCount++;
			deletedCompanyList.add(deleteEntity.getServerHost());
		}
		result.put("successDeleteCount", successDeleteCount);
		result.put("failDeleteCount", failDeleteCount);
		result.put("deletedCompanyList", deletedCompanyList);
		
		return result;
	}

}
