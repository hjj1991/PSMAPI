package com.psm.api.apiserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.psm.api.apiserver.entity.ApiServerListEntity;

public interface PagingApiServerListRepository extends JpaRepository<ApiServerListEntity, Integer>, Repository<ApiServerListEntity, Integer> {
	Page<ApiServerListEntity> findAll(Pageable request);
	Page<ApiServerListEntity> findByDeletedYn(String deletedYn, Pageable request);
	Page<ApiServerListEntity> findByServerHostLike(String serverHost, Pageable request);
	Page<ApiServerListEntity> findByServerHostLikeAndCompanyIdx_CompanyIdx(String serverHost, int companyIdx, Pageable request);
//	ApiServerListEntity findByCompanyName(String companyName);
//	ApiServerListEntity findByCompanyId(String companyId);
	Page<ApiServerListEntity> findByCompanyIdx_CompanyIdx(int companyIdx, Pageable pageRequest);
	
}
