package com.psm.api.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.psm.api.user.entity.UserEntity;

public interface PagingUserRepository extends JpaRepository<UserEntity, Integer>, Repository<UserEntity, Integer> {
	Page<UserEntity> findAll(Pageable request);
	Page<UserEntity> findByDeletedYn(String deletedYn, Pageable request);
	Page<UserEntity> findByNameLike(String name, Pageable request);
	Page<UserEntity> findByUserIdLike(String userId, Pageable request);
	Page<UserEntity> findByNameLikeAndCompanyIdx_CompanyIdx(String name, int companyIdx, Pageable request);
	Page<UserEntity> findByUserIdLikeAndCompanyIdx_CompanyIdx(String userId, int companyIdx,  Pageable pageRequest);
	Page<UserEntity> findByCompanyIdx_CompanyIdx(int companyIdx, Pageable pageRequest);

	
}