package com.psm.api.workload.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psm.api.workload.entity.ScheduleEntity;

public interface ScheduleRepository  extends JpaRepository<ScheduleEntity, Integer> {
	ScheduleEntity findByWorkloadId_WorkloadId(String workloadId);
}
