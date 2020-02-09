package com.psm.api.workload.repository;

import org.springframework.data.repository.CrudRepository;

import com.psm.api.workload.entity.WorkloadEntity;

public interface WorkloadRepository extends CrudRepository<WorkloadEntity, Integer> {
	WorkloadEntity findByWorkloadId(String workloadId);

}
