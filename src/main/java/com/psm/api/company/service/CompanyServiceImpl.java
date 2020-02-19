package com.psm.api.company.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.psm.api.common.exception.CCompanyNotFoundException;
import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.company.dto.FindCompanyDto;
import com.psm.api.company.dto.InsertCompanyDto;
import com.psm.api.company.dto.UpdateCompanyDto;
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
		PageRequest pageRequest = PageRequest.of(findCompanyDto.getPage() - 1,  findCompanyDto.getPageSize(), Sort.by("deletedYn").ascending().and(Sort.by("companyIdx").ascending())); 
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
	public HashMap<String, Object> insertCompany(List<InsertCompanyDto> insertCompanyList)  {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = new HashMap<String, Object>();
		int successInsertCount = 0;
		int failInsertCount = 0;
		List<String> failNameList = new ArrayList<String>();
		for (InsertCompanyDto companyInfo : insertCompanyList) {
			CompanyEntity insertCompany = new CompanyEntity();
			//회사명, 회사이이디 중복체크 해야함 없으면 insert를 진행한다.
			if(companyRepository.findByCompanyName(companyInfo.getCompanyName()) == null && companyRepository.findByCompanyId(companyInfo.getCompanyId()) == null){
				insertCompany.setCompanyId(companyInfo.getCompanyId());
				insertCompany.setCompanyName(companyInfo.getCompanyName());
				companyRepository.save(insertCompany);
				successInsertCount++;
			}else {
				failInsertCount++;
				failNameList.add(companyInfo.getCompanyName());
			}
		}
		result.put("successInsertCount", successInsertCount);
		result.put("failInsertCount", failInsertCount);
		result.put("failNameList", failNameList);
		return result;
	}

	@Override
	public CompanyEntity updateCompany(UpdateCompanyDto updateCompanyValue) {
		// TODO Auto-generated method stub
		String companyName, companyId, deletedYn;
		CompanyEntity updateEntity = companyRepository.findById(updateCompanyValue.getCompanyIdx()).orElseThrow(CCompanyNotFoundException::new);
		if(updateCompanyValue.getCompanyName() == null) {
			companyName = updateEntity.getCompanyName();
		}else {
			companyName = updateCompanyValue.getCompanyName();
		}
		if(updateCompanyValue.getCompanyId() == null) {
			companyId = updateEntity.getCompanyId();
		}else {
			companyId = updateCompanyValue.getCompanyId();
		}
		if(updateCompanyValue.getDeletedYn() == null) {
			deletedYn = updateEntity.getDeletedYn();
		}else {
			deletedYn = updateCompanyValue.getDeletedYn();
		}
		updateEntity.setCompanyName(companyName);
		updateEntity.setCompanyId(companyId);
		updateEntity.setDeletedYn(deletedYn);
		updateEntity = companyRepository.save(updateEntity);
		return updateEntity;
	}

	@Override
	public HashMap<String, Object> deleteCompany(List<String> deleteCompanyIdxList) {
		// TODO Auto-generated method stub
		HashMap<String, Object> result = new HashMap<String, Object>();
		int successDeleteCount = 0;
		int failDeleteCount = 0;
		List<String> deletedCompanyList = new ArrayList<String>();
		for(String companyIdx : deleteCompanyIdxList) {
			CompanyEntity deleteEntity = companyRepository.findById(Integer.valueOf(companyIdx)).orElseThrow(CCompanyNotFoundException::new);
			deleteEntity.setDeletedYn("Y");
			System.out.println("호이0");
			deleteEntity = companyRepository.save(deleteEntity);
			System.out.println("호이1");
			successDeleteCount++;
			deletedCompanyList.add(deleteEntity.getCompanyName());
		}
		result.put("successDeleteCount", successDeleteCount);
		result.put("failDeleteCount", failDeleteCount);
		result.put("deletedCompanyList", deletedCompanyList);
		
		return result;
	}

}
