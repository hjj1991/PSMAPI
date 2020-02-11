package com.psm.api.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.configuration.security.JwtTokenProvider;
import com.psm.api.user.dto.UserDetailDto;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return userRepository.findByUserId(String.valueOf(userId)).orElseThrow(CUserNotFoundException::new);
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
