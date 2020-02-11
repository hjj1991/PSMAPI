package com.psm.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDetailDto {
	private String userId;
	private String name;
	private String userEmail;
	private String userTel;
	private String userPhone;
	private String createdDate;
	private String companyName;
	private String userRole;
}
