package com.psm.api.user.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.psm.api.apiserver.dto.FindApiServerDto;
import com.psm.api.apiserver.entity.ApiServerListEntity;
import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.configuration.security.JwtTokenProvider;
import com.psm.api.user.dto.FindUserDto;
import com.psm.api.user.dto.UserDetailDto;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.PagingUserRepository;
import com.psm.api.user.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PagingUserRepository pagingUserRepository;
	
	@Value("spring.jwt.secret")
	private String secretKey;

	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return userRepository.findByUserId(String.valueOf(userId)).orElseThrow(CUserNotFoundException::new);
	}
	
	@Override
	public HashMap<String, Object> findUser(FindUserDto findUserDto, String authToken) throws Exception {
		// TODO Auto-generated method stub
//		if(findCompanyDto.getPage())
		int companyIdx; //회사 idx
		HashMap<String, Object> result = new HashMap<String, Object>();
		Page<UserEntity> data = null;
		String userRole;
		
		//토큰에서 사용자 아이디를 가져와서 repository에서 해당 사용자 검색
		String userId = Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(authToken).getBody().getSubject();
		UserEntity userInfo = userRepository.findByUserId(String.valueOf(userId)).orElseThrow(CUserNotFoundException::new);
		
		//유저의 권한 불러온다.
		userRole = userInfo.getUserRoles().get(0);
		
		//소속회사 idx번호를 구한다.
		companyIdx = userInfo.getCompanyIdx().getCompanyIdx();
		Pageable pageRequest = PageRequest.of(findUserDto.getPage() - 1,  findUserDto.getPageSize(), Sort.by("deletedYn").ascending());
		if(userRole.equals("ROLE_MASTER")) {				//사용자 권한에 따른 검색분기 나눔
			if(findUserDto.getFindTarget() != null && findUserDto.getFindKeyword() != null) {
				String target = findUserDto.getFindTarget();
				String keyword = findUserDto.getFindKeyword();
				if(target.equals("name")) {
					data = pagingUserRepository.findByNameLike("%" + keyword + "%", pageRequest);
				}else if(target.equals("userId")) {
					data = pagingUserRepository.findByUserIdLike("%" + keyword + "%", pageRequest);
				}else {
					data = pagingUserRepository.findAll(pageRequest);
				}
			}else {
				data = pagingUserRepository.findAll(pageRequest);
			}
		}else {
			if(findUserDto.getFindTarget() != null && findUserDto.getFindKeyword() != null) {
				String target = findUserDto.getFindTarget();
				String keyword = findUserDto.getFindKeyword();
				if(target.equals("name")) {
					data = pagingUserRepository.findByNameLikeAndCompanyIdx_CompanyIdx("%" + keyword + "%", companyIdx, pageRequest);
				}else if(target.equals("userId")) {
					data = pagingUserRepository.findByUserIdLikeAndCompanyIdx_CompanyIdx("%" + keyword + "%", companyIdx, pageRequest);
				}else {
					data = pagingUserRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
				}
			}else {
				data = pagingUserRepository.findByCompanyIdx_CompanyIdx(companyIdx, pageRequest);
			}		
		}
		result.put("data", data);
		return result;
	}


	@Override
	public UserDetailDto getUserDetail(UserEntity userEntity) {
		
		System.out.println(userEntity.getUsername());
		
		UserDetailDto userDetailDto = UserDetailDto.builder()
			.userId(userEntity.getUserId())
			.name(userEntity.getName())
			.userPhone(userEntity.getUserPhone())
			.userRole(userEntity.getUserRoles().get(0))
			.userTel(userEntity.getUserPhone())
			.createdDate(userEntity.getCreatedDate().toString())
			.companyName(userEntity.getCompanyIdx().getCompanyName())
			.build();
		return userDetailDto;
	}
	
}
