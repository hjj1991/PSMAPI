package com.psm.api.user.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.psm.api.user.entity.TokenEntity;

public interface TokenRepository extends JpaRepository<TokenEntity, String>  {
	TokenEntity findByUserId(String userId);
	
	boolean existsByUserId(@Param("userId") String userId);

	TokenEntity findByRefreshToken(String refreshToken);

	void deleteByUserId(String jwtUserId);
}
