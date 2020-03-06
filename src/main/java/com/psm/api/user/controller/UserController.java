package com.psm.api.user.controller;

import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.psm.api.apiserver.dto.FindApiServerDto;
import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.common.response.CommonResult;
import com.psm.api.common.response.SingleResult;
import com.psm.api.common.response.service.ResponseService;
import com.psm.api.company.repository.CompanyRepository;
import com.psm.api.configuration.security.JwtTokenProvider;
import com.psm.api.user.dto.FindUserDto;
import com.psm.api.user.dto.UserDetailDto;
import com.psm.api.user.dto.UserLoginDto;
import com.psm.api.user.dto.UserSignUpDto;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.UserRepository;
import com.psm.api.user.service.SignService;
import com.psm.api.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = { "1. User" })
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
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ApiImplicitParams({
			@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header") })
	@ApiOperation(value = "회원 단건 조회", notes = "userId로 회원을 조회한다")
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public SingleResult<UserDetailDto> findUserById(@RequestHeader("X_AUTH_TOKEN") String authToken) {
		return responseService.getSingleResult(userService.getUserDetail(userRepository
				.findByUserId(jwtTokenProvider.getUserPk(authToken)).orElseThrow(CUserNotFoundException::new)));
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "X_AUTH_TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header") })
	@ApiOperation(value = "사용자 조회", notes = "사용자를 조회한다")
	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public SingleResult<?> findUser(FindUserDto findUserDto, @RequestHeader("X_AUTH_TOKEN") String authToken) throws Exception {
//	responseService.getSingleResult(companyRepository.findByDeletedYn("Y", pageable));
		HashMap<String, Object> result = userService.findUser(findUserDto, authToken);

		return responseService.getSingleResult(result);

//	return responseService.getSingleResult(userService.getUserDetail(userRepository
//			.findByUserId(jwtTokenProvider.getUserPk(authToken)).orElseThrow(CUserNotFoundException::new)));
	}

	@ApiOperation(value = "로그인", notes = "아이디로 로그인을 한다.")
	@PostMapping(value = "/signin")
	public SingleResult<?> signin(@RequestBody UserLoginDto userLoginDto) throws Exception {
		HashMap<String, Object> result = signService.signIn(userLoginDto);

		return responseService.getSingleResult(result);
	}

	@ApiOperation(value = "로그아웃", notes = "로그아웃을 한다.")
	@PostMapping(value = "/signout")
	public SingleResult<?> signout(@RequestHeader("X_REFRESH_TOKEN") String refreshToken) throws Exception {

		HashMap<String, String> result = signService.signOut(refreshToken);
		return responseService.getNotDataSingleResult(Integer.parseInt(result.get("code")), result.get("msg"),
				Boolean.valueOf(result.get("success")).booleanValue());
	}

	@ApiOperation(value = "Access토큰 재발급", notes = "refreshToken을 이용하여 accessToken 재발급")
	@PostMapping(value = "/tokenreissue")
	public SingleResult<?> tokenReissue(@RequestBody Map<String, Object> param) throws Exception {
		HashMap<String, Object> result = signService.tokenReissue((String) param.get("refreshToken"));
		return responseService.getSingleResult(result.get("data"), Integer.parseInt(result.get("status").toString()),
				(String) result.get("resultMsg"), Boolean.valueOf((boolean) result.get("success")).booleanValue());
	}

	@ApiOperation(value = "가입", notes = "회원가입을 한다.")
	@PutMapping(value = "/signup")
	public CommonResult signin(@RequestBody @Valid UserSignUpDto userSignUpDto, BindingResult result) {
		if (result.hasErrors()) {
			List<FieldError> errors = result.getFieldErrors();
			HashMap<String, String> errorList = new HashMap<String, String>();
			for (FieldError error : errors) {
				errorList.put(error.getField(), error.getDefaultMessage());
				// System.out.println (error.getField() + " - " + error.getDefaultMessage());
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
		userEntity.setName(userSignUpDto.getName());
		userEntity.setUserEmail(userSignUpDto.getUserEmail());
		userEntity.setUserRoles(Collections.singletonList("ROLE_USER"));
		userEntity.setUserTel(userSignUpDto.getUserTel());
		userEntity.setUserPhone(userSignUpDto.getUserPhone());
		userEntity.setCompanyIdx(companyRepository.getOne(userSignUpDto.getCompanyIdx()));

		userRepository.save(userEntity);
		return responseService.getSuccessResult();
	}
}
