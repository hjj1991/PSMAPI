package com.psm.api.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.common.exception.PasswordNotMatchException;
import com.psm.api.configuration.security.JwtTokenProvider;
import com.psm.api.user.dto.UserLoginDto;
import com.psm.api.user.entity.TokenEntity;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.TokenRepository;
import com.psm.api.user.repository.UserRepository;

import io.jsonwebtoken.Jwts;

@Service
public class SignServiceImpl implements SignService{
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("spring.jwt.secret")
	private String secretKey;
	@Override
	public HashMap<String, String> signIn(UserLoginDto userLoginDto) throws Exception {
		
		UserEntity user = userRepository.findByUserId(userLoginDto.getUserId()).orElseThrow(CUserNotFoundException::new);
		if (!passwordEncoder.matches(userLoginDto.getUserPw(), user.getPassword()))
			throw new PasswordNotMatchException();
		
		HashMap<String, String> result = new HashMap<>();
		List<String> tokenInfo = new ArrayList<String>();
		tokenInfo = jwtTokenProvider.createToken(user.getUsername(), user.getUserRoles());
		result.put("X_AUTH_TOKEN", tokenInfo.get(0));
		result.put("exAuthToken", tokenInfo.get(1));
		result.put("X_REFRESH_TOKEN", jwtTokenProvider.createRefreshToken(user.getUsername(), user.getUserRoles()));
		result.put("name", user.getName());
		result.put("emailAddr", user.getUserEmail());
		
		
		return result;
	}	
	
	@Override
	public HashMap<String, String> signOut(String refreshToken) throws Exception {
		String jwtUserId = null;
		HashMap<String, String> result = new HashMap<String, String>();
		if(jwtTokenProvider.validateRefreshToken(refreshToken)) {	//리프레쉬 토큰 검증 후 토큰 디코딩하여 정보가져옴
			jwtUserId = Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(refreshToken).getBody().getSubject();
			tokenRepository.deleteById(jwtUserId);
			result.put("code",  "0");
			result.put("msg", "로그아웃되었습니다.");
			result.put("success", "true");
		}else {
			result.put("code",  "-1");
			result.put("msg", "잘못된 요청입니다.");
			result.put("success", "false");
		}
		
		return result;
		
//		result.put("X-AUTH-TOKEN", jwtTokenProvider.createToken(jwtUserId,jwtRoles)); //가져온 정보로 토큰 재생성
		
	}
	
	@Override
	public HashMap<String, Object> tokenReissue(String refreshToken) throws Exception {
		List<String> jwtRoles =  new ArrayList<String>();
		String jwtUserId = null;
		HashMap<String, Object> result = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> data = new HashMap<String, Object>();
		List<String> tokenInfo = new ArrayList<String>();
		if(jwtTokenProvider.validateRefreshToken(refreshToken)) {	//리프레쉬 토큰 검증 후 토큰 디코딩하여 정보가져옴
			TokenEntity tokenEntity = tokenRepository.findByRefreshToken(refreshToken);
			jwtRoles.add(Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(refreshToken).getBody().get("roles").toString().replace("[", "").replace("]", ""));
			jwtUserId = Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(refreshToken).getBody().getSubject();
			tokenInfo = jwtTokenProvider.createToken(jwtUserId,jwtRoles);
			result.put("status", "0");
			data.put("X_AUTH_TOKEN", tokenInfo.get(0));
			data.put("exAuthToken", tokenInfo.get(1));
			result.put("data", data);
			result.put("resultMsg", "재발급되었습니다.");
			result.put("success", true);
		}else {
			result.put("status",  "-1");
			result.put("resultMsg", "잘못된 요청입니다.");
			result.put("success", false);
		}
		
		return result;
		
//		result.put("X-AUTH-TOKEN", jwtTokenProvider.createToken(jwtUserId,jwtRoles)); //가져온 정보로 토큰 재생성
		
		

	}
}
