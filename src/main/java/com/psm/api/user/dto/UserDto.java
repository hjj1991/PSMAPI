package com.psm.api.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class UserDto {
	@Pattern(regexp = "^[a-z0-9]{5,20}$", message ="5~20자의 영문 소문자, 숫자만 사용 가능합니다.")
	private String userId;
	@Pattern(regexp = "^[a-z0-9]{7,14}$", message ="비밀번호는 영문 숫자 조합 7 ~ 14자리 이상입니다.")
	private String userPw;
	@Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message ="공백제외 한글, 영문, 숫자 2 ~ 10자로 입력해주세요.")
	private String name;
	@Email(message="이메일잘못됨")
	private String userEmail;
	private String userTel;
	private String userPhone;
	private String createdDate;
	private String companyName;
}
