package com.psm.api.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psm.api.common.response.CommonResult;
import com.psm.api.common.response.SingleResult;
import com.psm.api.common.response.service.ResponseService;
import com.psm.api.user.dto.UserDto;
import com.psm.api.user.dto.UserLoginDto;
import com.psm.api.user.dto.UserSignUpDto;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.CompanyRepository;
import com.psm.api.user.repository.UserRepository;
import com.psm.api.user.service.SignService;
import com.psm.api.user.service.UserService;

import java.util.Collections;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = { "1. Member" })
@RequiredArgsConstructor
@RestController // 결과값을 JSON으로 출력합니다.
@RequestMapping(value = "/v1")
public class UserController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SignService signService;
	@Autowired
	private ResponseService responseService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;


	@ApiOperation(value = "로그인", notes = "아이디로 로그인을 한다.")
	@PostMapping(value = "/signin")
	public SingleResult<?> signin(@RequestBody UserLoginDto userLoginDto) throws Exception {
		HashMap<String, String> result = new HashMap<>();
		result = signService.signIn(userLoginDto);
		
		return responseService.getSingleResult(result);
	}
	
	@ApiOperation(value = "Access토큰 재발급", notes = "refreshToken을 이용하여 accessToken 재발급")
	@PostMapping(value = "/tokenreissue")
	public SingleResult<?> tokenReissue(@RequestBody Map<String, Object> param) throws Exception {
		return responseService.getSingleResult(signService.tokenReissue((String)param.get("refreshToken")));
	}
 
	@ApiOperation(value = "가입", notes = "회원가입을 한다.")
	@PutMapping(value = "/signup")
	public CommonResult signin(@RequestBody @Valid UserSignUpDto userSignUpDto, BindingResult result) {
		if(result.hasErrors()) {
			List<FieldError> errors = result.getFieldErrors();
			HashMap<String, String> errorList = new HashMap<String, String>();
			 for (FieldError error : errors ) {
				 errorList.put(error.getField(), error.getDefaultMessage());
			        //System.out.println (error.getField() + " - " + error.getDefaultMessage());
			 }
			return responseService.getSingleResult(null, -1, "안됩니다.", false);
		}
//		userRepository.save(UserEntity.builder()
//				.userId(userDto.getUserId())
//				.userPw(passwordEncoder.encode(userDto.getUserPw()))
//				.userName(userDto.getUserName())
//				.userEmail(userDto.getUserEmail())
//				.roles(Collections.singletonList("ROLE_USER")).build());
		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(userSignUpDto.getUserId());
		userEntity.setUserPw(passwordEncoder.encode((userSignUpDto.getUserPw())));
		userEntity.setUserName(userSignUpDto.getUserName());
		userEntity.setUserEmail(userSignUpDto.getUserEmail());
		userEntity.setRoles(Collections.singletonList("ROLE_USER"));
		userEntity.setUserTel(userSignUpDto.getUserTel());
		userEntity.setUserPhone(userSignUpDto.getUserPhone());
		userEntity.setCompanyIdx(companyRepository.getOne(userSignUpDto.getCompanyId()));
		
		userRepository.save(userEntity);
		return responseService.getSuccessResult();
	}
}
