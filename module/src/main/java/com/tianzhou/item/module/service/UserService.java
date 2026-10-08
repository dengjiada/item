package com.tianzhou.item.module.service;

import com.alibaba.fastjson2.JSON;
import com.tianzhou.item.module.entity.User;
import com.tianzhou.item.module.entity.UserTokenPayload;
import com.tianzhou.item.module.mapper.UserMapper;
import com.tianzhou.item.module.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 用户表 业务层
 *
 * @author test
 * @since 2026-10-06
 */

@Service
public class UserService {
    @Autowired
    private UserMapper mapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtils jwtUtils;

    //根据id查询详情，需要判断逻辑删除标记（is_deleted）
    public User getById(Long id) {
        return mapper.getById(id);
    }

    //根据id查询详情，不需要判断逻辑删除标记（is_deleted）
    public User extractById(Long id) {
        return mapper.extractById(id);
    }

    //新增
    public int insert(String phone, String password, String salt, String name, String avatar) {
        User entity = new User().setPhone(phone)
                .setPassword(password)
                .setSalt(salt)
                .setName(name)
                .setAvatar(avatar)
                .setCreateTime((int) (System.currentTimeMillis() / 1000L));
        return mapper.insert(entity);
    }

    //修改
    public int update(Long id, String phone, String password, String salt, String name, String avatar) {
        User entity = new User().setId(id)
                .setPhone(phone)
                .setPassword(password)
                .setSalt(salt)
                .setName(name)
                .setAvatar(avatar);
        return mapper.update(entity);
    }

    //根据id删除逻辑删除
    public int delete(Long id) {
        return mapper.delete(id, (int) (System.currentTimeMillis() / 1000L));
    }

    //app端注册
    public String register(String phone, String password) {
        //校验手机号和密码
        if (phone == null || phone.isEmpty()) {
            throw new RuntimeException("phone cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("password cannot be empty");
        }

        //校验该手机号是否被注册过
        User userByPhone = userMapper.getUserByPhone(phone.trim());
        if (userByPhone != null) {
            throw new RuntimeException("this phone number is already registered");
        }

        //生成一个随机salt
        String salt = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        //使用MD5算法对password和随机salt做处理，得到要插入到数据库的密码
        String md5Password = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));

        //创建该用户
        User user = new User().setPhone(phone.trim())
                .setPassword(md5Password)
                .setSalt(salt)
                .setName("用户" + phone.substring(7))
                .setAvatar("")
                .setCreateTime((int) (System.currentTimeMillis() / 1000L))
                .setIsDeleted(0);

        //插入数据库
        userMapper.insert(user);

        //返回sign
        return buildSign(user);
    }

    //构造sign
    private String buildSign(User user) {
        //对用户id进行类序列化
        UserTokenPayload tokenPayload = new UserTokenPayload(user.getId());
        String jsonString = JSON.toJSONString(tokenPayload);

        //构造jwt,并返回
        return jwtUtils.generateAppToken(jsonString);
    }

    //app端登录
    public String appLogin(String phone, String password) {
        //根据手机号和密码拿到用户
        User user = getUser(phone, password);

        //返回sign
        return buildSign(user);
    }

    private User getUser(String phone, String password) {
        //校验手机号和密码
        if (phone == null || phone.isEmpty()) {
            throw new RuntimeException("phone cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("password cannot be empty");
        }

        //判断用户是否在数据库中
        User user = userMapper.getUserByPhone(phone.trim());
        if (user == null) {
            throw new RuntimeException("this user has not registered yet");
        }

        //将password和查出来的salt做md5算法，得到密码，将该密码和查出来的密码比对，如果一样，则密码正确，否则错误
        String md5Password = DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes(StandardCharsets.UTF_8));
        if (!md5Password.equals(user.getPassword())) {
            throw new RuntimeException("incorrect password");
        }
        return user;
    }

    //console端登录
    public String consoleLogin(String phone, String password) {
        //根据手机号和密码拿到用户
        User user = getUser(phone, password);

        //对用户id进行类序列化
        UserTokenPayload tokenPayload = new UserTokenPayload(user.getId());
        String jsonString = JSON.toJSONString(tokenPayload);

        //构造jwt
        return jwtUtils.generateConsoleToken(jsonString);
    }
}
