package com.psm.api.workload.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psm.api.workload.entity.ScheduleEntity;

public interface ScheduleRepository  extends JpaRepository<ScheduleEntity, Integer> {
}
