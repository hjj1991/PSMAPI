package com.psm.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psm.api.user.entity.CompanyEntity;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {

}
