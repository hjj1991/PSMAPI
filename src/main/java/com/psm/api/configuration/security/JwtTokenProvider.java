package com.psm.api.configuration.security;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.psm.api.user.entity.TokenEntity;
import com.psm.api.user.repository.TokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

//import 생략

@RequiredArgsConstructor
@Component
public class JwtTokenProvider { // JWT 토큰을 생성 및 검증 모듈
	
	@Autowired
	TokenRepository tokenRepository;

	@Value("spring.jwt.secret")
	private String secretKey;

	private long tokenValidMilisecond = 1000L * 60 * 20; // 30분만 토큰 유효
	private long refreshTokenValidMilisecond = 1000L * 60 * 1440 * 14; // 2주간 토큰 유효

	private final UserDetailsService userDetailsService;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	// Jwt 토큰 생성
	public List<String> createToken(String userPk, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(userPk);
		System.out.println("으아아아!" + roles);
		claims.put("roles", roles);
		Date now = new Date();
		long expireTime = new Date().getTime() + tokenValidMilisecond;
		System.out.println("중요:" + expireTime);
		String token = Jwts.builder().setClaims(claims) // 데이터
				.setIssuedAt(now) // 토큰 발행일자
				.setExpiration(new Date(expireTime)) // set Expire Time
				.signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
				.compact();
		
		List<String> result = new ArrayList<String>();
		
		result.add(token);
		result.add(String.valueOf(expireTime));
		
		return result;
		
	}
	
	public String createRefreshToken(String userPk, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(userPk);
		claims.put("roles", roles);
		Date now = new Date();
		LocalDateTime ldt = Instant.ofEpochMilli( now.getTime() )
                            .atZone( ZoneId.systemDefault() )
                            .toLocalDateTime();
		Date expiredDate = new Date(now.getTime() + refreshTokenValidMilisecond);
		
		String refreshToken = Jwts.builder().setClaims(claims) // 데이터
				.setIssuedAt(now) // 토큰 발행일자
				.setExpiration(expiredDate) // set Expire Time
				.signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
				.compact();
		
		if(tokenRepository.existsByUserId(userPk)) {
			TokenEntity updateTokenEntity = tokenRepository.findByUserId(userPk);
			Calendar cal = Calendar.getInstance();
			Date currentExpiredDate = updateTokenEntity.getExpiredDatetime();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			
			cal.setTime(new Date());
			cal.add(Calendar.DATE, -2);
			if(df.format(currentExpiredDate).compareTo(df.format(cal.getTime())) < 0) {
				updateTokenEntity.setExpiredDatetime(expiredDate);
				updateTokenEntity.setRefreshToken(refreshToken);
				tokenRepository.save(updateTokenEntity);	
			}else {
				refreshToken = updateTokenEntity.getRefreshToken();
			}
		}else {
			TokenEntity tokenEntity = new TokenEntity();
			tokenEntity.setUserId(userPk);
			tokenEntity.setCreatedDatetime(ldt);
			tokenEntity.setRefreshToken(refreshToken);
			tokenEntity.setExpiredDatetime(expiredDate);
			tokenRepository.save(tokenEntity);
		}
		
		return refreshToken;
	}

	// Jwt 토큰으로 인증 정보를 조회
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	// Jwt 토큰에서 회원 구별 정보 추출
	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	// Request의 Header에서 token 파싱 : "X-AUTH-TOKEN: jwt토큰"
	public String resolveToken(HttpServletRequest req) {
		return req.getHeader("X_AUTH_TOKEN");
	}
	// Request의 Header에서 refreshToken 파싱 : "X-REFRESH-TOKEN: jwt토근"
	public String resolveRefreshToken(HttpServletRequest req) {
		return req.getHeader("X_REFRESH_TOKEN");
	}

	// Jwt 토큰의 유효성 + 만료일자 확인
	public boolean validateToken(String jwtToken) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
	// Jwt 토큰의 유효성 + 만료일자 확인
	public boolean validateRefreshToken(String jwtToken) {
		if(tokenRepository.findByRefreshToken(jwtToken) != null) {
			try {
				Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
				return !claims.getBody().getExpiration().before(new Date());
			} catch (Exception e) {
				return false;
			}
		}else {
			return false;
		}
	}
}