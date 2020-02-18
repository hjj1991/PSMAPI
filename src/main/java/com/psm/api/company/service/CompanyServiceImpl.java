package com.psm.api.company.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.psm.api.company.dto.FindCompanyDto;
import com.psm.api.company.dto.InsertCompanyDto;
import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.company.repository.CompanyRepository;

@Service
public class CompanyServiceImpl implements CompanyService {
	

	@Autowired
	CompanyRepository companyRepository;

	@Override
	public Page<CompanyEntity> findCompany(FindCompanyDto findCompanyDto) {
		// TODO Auto-generated method stub
//		if(findCompanyDto.getPage())
		Page<CompanyEntity> result = null;
		PageRequest pageRequest = PageRequest.of(findCompanyDto.getPage() - 1,  findCompanyDto.getPageSize()); 
		if(findCompanyDto.getFindTarget() != null && findCompanyDto.getFindKeyword() != null) {
			String target = findCompanyDto.getFindTarget();
			String keyword = findCompanyDto.getFindKeyword();
			if(target.equals("companyName")) {
				result = companyRepository.findByCompanyNameLike("%" + keyword + "%", pageRequest);
				System.out.println(result);
			}else if(target.equals("companyId")) {
				result = companyRepository.findByCompanyIdLike("%" + keyword + "%", pageRequest);
			}else {
				result = companyRepository.findAll(pageRequest);
			}
		}else {
			result = companyRepository.findAll(pageRequest);
		}
		
		return result;
	}

	@Override
	public HashMap<String, Object> insertCompany(List<InsertCompanyDto> insertCompanyList) {
		// TODO Auto-generated method stub
		return null;
	}

}
