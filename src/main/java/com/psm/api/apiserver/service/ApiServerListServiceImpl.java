package com.psm.api.apiserver.service;

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
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.apiserver.repository.PagingApiServerListRepository;
import com.psm.api.company.dto.UpdateCompanyDto;
import com.psm.api.company.repository.CompanyRepository;

@Service
public class ApiServerListServiceImpl implements ApiServerListService {
	

	@Autowired
	CompanyRepository companyRepository;
	@Autowired
	PagingApiServerListRepository pagingApiServerListRepository;
	@Override
	public Page<ApiServerListEntity> findApiServer(FindApiServerDto findApiServerDto) {
		// TODO Auto-generated method stub
//		if(findCompanyDto.getPage())
		int companyIdx; //회사 idx
		Page<ApiServerListEntity> result = null;
		HashMap<String, Object> 
		//소속회사이름으로 회사 idx번호를 구한다.
		companyIdx = companyRepository.findByCompanyName(findApiServerDto.getInCompanyName()).getCompanyIdx();
		Pageable pageRequest = PageRequest.of(findApiServerDto.getPage() - 1,  findApiServerDto.getPageSize(), Sort.by("deletedYn").ascending());
		if(findApiServerDto.getUserRole().equals("ROLE_MASTER")) {		
			if(findApiServerDto.getFindTarget() != null && findApiServerDto.getFindKeyword() != null) {
				String target = findApiServerDto.getFindTarget();
				String keyword = findApiServerDto.getFindKeyword();
				if(target.equals("serverHost")) {
					result = pagingApiServerListRepository.findByServerHostLike("%" + keyword + "%", pageRequest);
					for(ApiServerListEntity apiValue : result.getContent()) {
						apiValue.setCompanyIdx(apiValue.getCompanyIdx().getCompanyName());
						
					}
					System.out.println(result);
				}else {
					result = pagingApiServerListRepository.findAll(pageRequest);
					System.out.println(pagingApiServerListRepository.findAll(pageRequest).getContent());
				}
			}else {
				System.out.println("큭");
				result = pagingApiServerListRepository.findAll(pageRequest);
				System.out.println(pagingApiServerListRepository.findAll(pageRequest));
				System.out.println("으억");
			}
		}else {
			if(findApiServerDto.getFindTarget() != null && findApiServerDto.getFindKeyword() != null) {
				String target = findApiServerDto.getFindTarget();
				String keyword = findApiServerDto.getFindKeyword();
				if(target.equals("serverHost")) {
					result = pagingApiServerListRepository.findByServerHostLikeAndCompanyIdx_CompanyIdx("%" + keyword + "%", companyIdx, pageRequest);
					System.out.println(result);
				}else {
					result = pagingApiServerListRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
				}
			}else {
				System.out.println("케케");
				result = pagingApiServerListRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
			}		
		}
		return result;
	}
	
	@Override
	public HashMap<String, Object> insertApiServer(List<InsertApiServerDto> insertApiServerList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiServerListEntity updateApiServer(UpdateCompanyDto updateApiServerValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> deleteApiServer(List<String> deleteApiServerIdxList) {
		// TODO Auto-generated method stub
		return null;
	}

}
