package com.psm.api.workload.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.psm.api.workload.entity.ApiServerListEntity;

public interface ApiServerListRepository extends CrudRepository<ApiServerListEntity, Integer>  {
	List<ApiServerListEntity> findAll();
	ApiServerListEntity findByServerHost(String serverHost);

}
