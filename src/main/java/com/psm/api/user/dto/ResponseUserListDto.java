package com.psm.api.user.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.psm.api.company.entity.CompanyEntity;

import lombok.Data;
@Data
public class ResponseUserListDto {
	private int userIdx;
	private CompanyEntity companyIdx;
	private String userId;
	private String name;
	private String userTel;
	private String userPhone;
	private String userEmail;
	private Date createdDate;
	private String deletedYn;
	private List<String> userRoles = new ArrayList<>();
}
