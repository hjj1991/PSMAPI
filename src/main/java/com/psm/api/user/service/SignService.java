package com.psm.api.user.service;

import java.util.HashMap;

import com.psm.api.user.dto.UserLoginDto;

public interface SignService {
	HashMap<String, String> tokenReissue(String refreshToken) throws Exception;

	HashMap<String, String> signIn(UserLoginDto userLoginDto) throws Exception;

	HashMap<String, String> signOut(String refreshToken) throws Exception;
}
