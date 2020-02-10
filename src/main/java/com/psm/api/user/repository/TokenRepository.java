package com.psm.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.psm.api.user.entity.TokenEntity;

public interface TokenRepository extends JpaRepository<TokenEntity, String>  {
	TokenEntity findByUserId(String userId);
	
	boolean existsByUserId(String userId);

	TokenEntity findByRefreshToken(String refreshToken);
}
