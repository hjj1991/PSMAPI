package com.psm.api.company.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.psm.api.company.entity.CompanyEntity;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer>, Repository<CompanyEntity, Integer> {
	Page<CompanyEntity> findAll(Pageable request);
	Page<CompanyEntity> findByDeletedYn(String deletedYn, Pageable request);
	Page<CompanyEntity> findByCompanyNameLike(String companyName, Pageable request);
	Page<CompanyEntity> findByCompanyIdLike(String companyId, Pageable request);
	
}
