package com.psm.api.workload.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.psm.api.workload.entity.AvailableActionEntity;

public interface AvailableActionRepository extends JpaRepository<AvailableActionEntity, Integer> {

	List<AvailableActionEntity> findByWorkloadId(String workloadId);

	@Modifying
	@Query(value = "DBCC CHECKIDENT(tbl_available_action, reseed, 1)", nativeQuery = true)
	void resetIdAvailableActionTable();

}
