package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import io.swagger.util.Json;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    //常量
    private static final String WX_URL ="https://api.weixin.qq.com/sns/jscode2session";
    //Bean
    @Autowired
    private UserMapper userMapper;
   @Autowired
   private WeChatProperties weChatProperties;
    /**
     * 获取openid
     * @param code
     * @return
     */
    private String getOpenId(String code){
        //请求微信获取用户唯一标识openid
        //封装请求数据
        Map<String, String> getmap=new HashMap<>();
        getmap.put("appid",weChatProperties.getAppid());
        getmap.put("secret",weChatProperties.getSecret());
        getmap.put("js_code",code);
        getmap.put("grant_type","authorization_code");
        //发送请求
        String json = HttpClientUtil.doGet(WX_URL, getmap);

        //解析返回数据,获取openid
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
    /**
     * 微信用户登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User userLogin(UserLoginDTO userLoginDTO) {
        String openid=getOpenId(userLoginDTO.getCode());
        //登录失败提示
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //查询是否为新用户
        User user = userMapper.getByOpenId(openid);
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            //存储新用户数据
            userMapper.insert(user);
        }
        return user;
    }
}
