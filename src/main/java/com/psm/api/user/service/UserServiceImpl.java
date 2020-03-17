package com.psm.api.user.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.util.Converter;
import com.psm.api.common.exception.CUserNotFoundException;
import com.psm.api.company.dto.ResponseSimpleCompanyList;
import com.psm.api.company.entity.CompanyEntity;
import com.psm.api.company.repository.CompanyRepository;
import com.psm.api.user.dto.FindUserDto;
import com.psm.api.user.dto.ResponseUserListDto;
import com.psm.api.user.dto.UserDetailDto;
import com.psm.api.user.dto.UserModifyDTO;
import com.psm.api.user.dto.UserSignUpDto;
import com.psm.api.user.entity.UserEntity;
import com.psm.api.user.repository.PagingUserRepository;
import com.psm.api.user.repository.UserRepository;

import io.jsonwebtoken.Jwts;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private PagingUserRepository pagingUserRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
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

		
		ModelMapper modelMapper = new ModelMapper();
		//Entity를 DTO형태로 변환해야 값 수정을 하더라도 dirtyChecking에 걸리지 않는다.
		Page<ResponseUserListDto> responseUserListDto = data.map(new Function<UserEntity, ResponseUserListDto>(){
			@Override
			public ResponseUserListDto apply(UserEntity tempEntity) {
				ResponseUserListDto dto = new ResponseUserListDto();
				dto = modelMapper.map(tempEntity, ResponseUserListDto.class);
				//권한에 따른 유저정보를 수정한다.
				if(dto.getUserRoles().get(0).equals("ROLE_MASTER")){
					dto.getUserRoles().set(0, "전체 관리자");
				}else {
					dto.getUserRoles().set(0, "일반 사용자");
				}
				return dto;
			}
		});
		
		List<CompanyEntity> companyEntity = companyRepository.findByDeletedYn("N");
		List<ResponseSimpleCompanyList> responseSimpleCompanyList = companyEntity.stream().map(new Function<CompanyEntity, ResponseSimpleCompanyList>() {
			@Override
			public ResponseSimpleCompanyList apply(CompanyEntity t) {
				// TODO Auto-generated method stub
				ResponseSimpleCompanyList dto = modelMapper.map(t, ResponseSimpleCompanyList.class);
				return dto;
			}
			
		}).collect(Collectors.toList());
		
		result.put("data", responseUserListDto);
		result.put("companyList", responseSimpleCompanyList);
		return result;
	}


	@Override
	public UserDetailDto getUserDetail(UserEntity userEntity) {
		
		System.out.println(userEntity.getUsername());
		String userRole;
		if(userEntity.getUserRoles().get(0).equals("ROLE_MASTER")) {
			userRole = "전체 관리자";
		}else {
			userRole = "일반 사용자";
		}
		
		UserDetailDto userDetailDto = UserDetailDto.builder()
			.userId(userEntity.getUserId())
			.name(userEntity.getName())
			.userEmail(userEntity.getUserEmail())
			.userPhone(userEntity.getUserPhone())
			.userRole(userRole)
			.userTel(userEntity.getUserTel())
			.createdDate(userEntity.getCreatedDate().toString())
			.companyName(userEntity.getCompanyIdx().getCompanyName())
			.build();
		return userDetailDto;
	}

	@Override
	public HashMap<String, Object> modifyUser(UserModifyDTO userModifyDTO) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByUserId(String.valueOf(userModifyDTO.getUserId())).orElseThrow(CUserNotFoundException::new);
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<String> userRoles = new ArrayList<String>();
		if(userModifyDTO.getUserRole().equals("전체 관리자")) {
			userRoles.add("ROLE_MASTER");
		}else {
			userRoles.add("ROLE_USER");
		}
		if(userModifyDTO.getUserPw() != null && !userModifyDTO.getUserPw().equals("")) {
			Pattern pattern = Pattern.compile("^[a-z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$");
			Matcher match = pattern.matcher(userModifyDTO.getUserPw());
			boolean checkPw = match.matches();
			if(checkPw == true) {
				userEntity.setUserPw(passwordEncoder.encode(userModifyDTO.getUserPw()));
				userEntity.setName(userModifyDTO.getName());
				userEntity.setUserEmail(userModifyDTO.getUserEmail());
				userEntity.setUserPhone(userModifyDTO.getUserPhone());
				userEntity.setUserTel(userModifyDTO.getUserTel());
				userEntity.setUserRoles(userRoles);
				userEntity.setDeletedYn(userModifyDTO.getDeletedYn());
				userEntity.setCompanyIdx(companyRepository.findById(userModifyDTO.getCompanyIdx()).orElseThrow(CUserNotFoundException::new));
				userRepository.save(userEntity);
				
				result.put("success", true);
				result.put("code", 0);
				result.put("msg", "성공하였습니다.");
				result.put("data", userEntity);
			}else {
				result.put("success", false);
				result.put("code", -1);
				result.put("msg", "패스워드 정규식이 잘못되어 실패하였습니다.");
			}
		}else {
			userEntity.setName(userModifyDTO.getName());
			userEntity.setUserEmail(userModifyDTO.getUserEmail());
			userEntity.setUserPhone(userModifyDTO.getUserPhone());
			userEntity.setUserTel(userModifyDTO.getUserTel());
			userEntity.setDeletedYn(userModifyDTO.getDeletedYn());
			userEntity.setUserRoles(userRoles);
			userEntity.setCompanyIdx(companyRepository.findById(userModifyDTO.getCompanyIdx()).orElseThrow(CUserNotFoundException::new));
			userRepository.save(userEntity);
			result.put("success", true);
			result.put("code", 0);
			result.put("msg", "성공하였습니다.");
			result.put("data", userEntity);
		}
		
		
		
		

		
		return result;
	}
	
}
