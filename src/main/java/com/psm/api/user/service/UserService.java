package com.psm.api.user.service;

import com.psm.api.user.dto.UserDetailDto;
import com.psm.api.user.entity.UserEntity;

public interface UserService {
	UserDetailDto getUserDetail(UserEntity userEntity);

}
