package com.psm.api.user.service;

import java.util.HashMap;

import javax.validation.Valid;

import com.psm.api.user.dto.FindUserDto;
import com.psm.api.user.dto.UserDetailDto;
import com.psm.api.user.dto.UserModifyDTO;
import com.psm.api.user.dto.UserSignUpDto;
import com.psm.api.user.entity.UserEntity;

public interface UserService {
	UserDetailDto getUserDetail(UserEntity userEntity);

	HashMap<String, Object> findUser(FindUserDto findUserDto, String authToken) throws Exception;

	HashMap<String, Object> modifyUser(UserModifyDTO userModifyDTO);

}
