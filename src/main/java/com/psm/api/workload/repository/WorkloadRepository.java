package com.psm.api.workload.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.workload.entity.WorkloadEntity;

public interface WorkloadRepository extends JpaRepository<WorkloadEntity, Integer>, Repository<WorkloadEntity, Integer> {
	Page<WorkloadEntity> findAll(Pageable request);
	Page<WorkloadEntity> findByCompanyIdx_CompanyNameLike(String companyName, Pageable pageRequest);
	Page<WorkloadEntity> findByCompanyIdx_CompanyIdx(int companyIdx, Pageable pageRequest);
	WorkloadEntity findByWorkloadId(String workloadId);
	
}
