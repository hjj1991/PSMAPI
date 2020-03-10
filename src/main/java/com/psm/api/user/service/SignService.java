package com.psm.api.user.service;

import java.util.HashMap;

import com.psm.api.user.dto.UserLoginDto;
import com.psm.api.user.dto.UserSignUpDto;

public interface SignService {
	HashMap<String, Object> tokenReissue(String refreshToken) throws Exception;

	HashMap<String, Object> signIn(UserLoginDto userLoginDto) throws Exception;

	HashMap<String, String> signOut(String refreshToken) throws Exception;

	HashMap<String, Object> signUp(UserSignUpDto userSignUpDto);
}
