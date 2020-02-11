package com.psm.api.user.entity;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;


import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tbl_token")
@NoArgsConstructor
@Data
public class TokenEntity {
	@Id
	private String userId;
	
	@Column(nullable=false)
	private String refreshToken;
	
	@Column(nullable=false)
	private LocalDateTime createdDatetime = LocalDateTime.now();
	
	@Column(nullable=false)
	private Date expiredDatetime;
}


	
	