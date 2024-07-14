package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.springframework.stereotype.Service;

public interface UserService {
    /**
     * 微信用户登录
     * @param userLoginDTO
     * @return
     */
    User userLogin(UserLoginDTO userLoginDTO);

}
