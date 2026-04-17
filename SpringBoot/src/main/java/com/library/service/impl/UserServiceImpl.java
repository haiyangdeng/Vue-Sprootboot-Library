package com.library.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.PasswordDTO;
import com.library.dto.UserDTO;
import com.library.entity.Borrow;
import com.library.entity.User;
import com.library.mapper.BorrowMapper;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import com.library.utils.JwtUtil;
import com.library.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BorrowMapper borrowMapper;

    @Override
    public Result<?> login(LoginDTO dto) {
        User user = this.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtil.createToken(user.getId(), user.getUsername(), user.getRole());


        // 屏蔽敏感信息
        user.setPassword(null);
        UserVO userInfo = new UserVO();
        BeanUtils.copyProperties(user, userInfo); // 这样就不需要手动 setPassword(null) 了，VO里压根没密码字段

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("userInfo", userInfo);
        return Result.success("登录成功", map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> register(UserDTO dto) {
        if (this.count(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) > 0) {
            return Result.error("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("user");
        this.save(user);
        return Result.success("注册成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addUser(UserDTO dto) {
        if (this.count(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) > 0) {
            return Result.error("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        String password = StrUtil.isNotBlank(dto.getPassword()) ? dto.getPassword() : "123456";
        user.setPassword(passwordEncoder.encode(password));
        if (StrUtil.isBlank(dto.getRole())) user.setRole("user");
        this.save(user);
        return Result.success("新增用户成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateUser(UserDTO dto) {
        User user = this.getById(dto.getId());
        if (user == null) return Result.error(404, "用户不存在");

        if (StrUtil.isNotBlank(dto.getUsername()) && !dto.getUsername().equals(user.getUsername())) {
            if (this.count(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) > 0) {
                return Result.error("用户名已存在");
            }
        }

        BeanUtils.copyProperties(dto, user, "password"); // 禁止通过此接口修改密码
        this.updateById(user);
        return Result.success("修改成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteUser(Long id) {
        long count = borrowMapper.selectCount(Wrappers.<Borrow>lambdaQuery()
                .eq(Borrow::getUserId, id)
                .eq(Borrow::getStatus, "unreturned"));
        if (count > 0) return Result.error("该用户有未归还的图书，无法删除");

        this.removeById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result<?> updatePassword(Long userId, PasswordDTO dto) {
        User user = this.getById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return Result.error("旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
        return Result.success("密码修改成功");
    }

    @Override
    public Result<?> resetPassword(Long userId) {
        User user = this.getById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        user.setPassword(passwordEncoder.encode("123456"));
        this.updateById(user);
        return Result.success("密码已重置为123456");
    }

    @Override
    public Result<Page<UserVO>> getUserPage(Integer pageNum, Integer pageSize, String username, String nickname, String phone) {
        // 1. 初始化分页对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 2. 构造查询条件
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(username), User::getUsername, username)
                .like(StrUtil.isNotBlank(nickname), User::getNickname, nickname)
                .like(StrUtil.isNotBlank(phone), User::getPhone, phone)
                .orderByDesc(User::getCreateTime);

        // 3. 执行分页查询
        Page<User> userPage = this.page(page, wrapper);

        // 4. 使用 IPage 的 convert 方法进行优雅转换
        // 它会自动帮你处理 records 的流转和分页信息的拷贝
        Page<UserVO> voPage = (Page<UserVO>) userPage.convert(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        });

        return Result.success(voPage);
    }

    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) return Result.error(404, "用户不存在");

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }
}