package com.psm.api.apiserver.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.psm.api.apiserver.entity.ApiServerListEntity;

public interface ApiServerListRepository extends CrudRepository<ApiServerListEntity, Integer>  {
	List<ApiServerListEntity> findAll();
	ApiServerListEntity findByServerHost(String serverHost);
	ApiServerListEntity findByServerHostAndDeletedYn(String serverHost, String deletedYn);

}
