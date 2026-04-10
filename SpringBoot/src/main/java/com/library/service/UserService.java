package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.PasswordDTO;
import com.library.dto.UserDTO;
import com.library.entity.Borrow;
import com.library.entity.User;
import com.library.mapper.BorrowMapper;
import com.library.mapper.UserMapper;
import com.library.utils.JwtUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BorrowMapper borrowMapper;

    /**
     * 用户登录
     */
    public Result<?> login(LoginDTO dto) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = this.getOne(wrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        // 生成Token
        String token = jwtUtil.createToken(user.getId(), user.getUsername(), user.getRole());

        // 封装返回数据
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);

        // 返回用户信息（不含密码）
        User userInfo = new User();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setPassword(null);
        map.put("userInfo", userInfo);

        return Result.success("登录成功", map);
    }

    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> register(UserDTO dto) {
        // 校验用户名唯一
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (this.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("user"); // 注册默认为普通用户
        this.save(user);

        return Result.success("注册成功");
    }

    /**
     * 新增用户（管理员）
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addUser(UserDTO dto) {
        // 校验用户名唯一
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (this.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        // 默认密码 123456
        String password = StrUtil.isNotBlank(dto.getPassword()) ? dto.getPassword() : "123456";
        user.setPassword(passwordEncoder.encode(password));
        if (StrUtil.isBlank(dto.getRole())) {
            user.setRole("user");
        }
        this.save(user);

        return Result.success("新增用户成功");
    }

    /**
     * 修改用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateUser(UserDTO dto) {
        User user = this.getById(dto.getId());
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 如果修改了用户名，需要校验唯一性
        if (StrUtil.isNotBlank(dto.getUsername()) && !dto.getUsername().equals(user.getUsername())) {
            LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(User::getUsername, dto.getUsername());
            if (this.count(wrapper) > 0) {
                return Result.error("用户名已存在");
            }
        }

        if (StrUtil.isNotBlank(dto.getUsername())) user.setUsername(dto.getUsername());
        if (StrUtil.isNotBlank(dto.getPhone())) user.setPhone(dto.getPhone());
        if (StrUtil.isNotBlank(dto.getEmail())) user.setEmail(dto.getEmail());
        if (StrUtil.isNotBlank(dto.getAddress())) user.setAddress(dto.getAddress());
        if (StrUtil.isNotBlank(dto.getRole())) user.setRole(dto.getRole());
        if (StrUtil.isNotBlank(dto.getRemark())) user.setRemark(dto.getRemark());

        this.updateById(user);
        return Result.success("修改成功");
    }

    /**
     * 删除用户
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        // 检查是否有未归还的图书
        LambdaQueryWrapper<Borrow> borrowWrapper = Wrappers.lambdaQuery();
        borrowWrapper.eq(Borrow::getUserId, id);
        borrowWrapper.eq(Borrow::getStatus, "unreturned");
        long count = borrowMapper.selectCount(borrowWrapper);
        if (count > 0) {
            return Result.error("该用户有未归还的图书，无法删除");
        }

        this.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 修改密码
     */
    public Result<?> updatePassword(Long userId, PasswordDTO dto) {
        User user = this.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return Result.error("旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
        return Result.success("密码修改成功");
    }

    /**
     * 重置密码（管理员）
     */
    public Result<?> resetPassword(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        user.setPassword(passwordEncoder.encode("123456"));
        this.updateById(user);
        return Result.success("密码已重置为123456");
    }

    /**
     * 分页查询用户列表
     */
    public Result<?> getUserPage(Integer pageNum, Integer pageSize, String username, String phone) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();

        if (StrUtil.isNotBlank(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StrUtil.isNotBlank(phone)) {
            wrapper.like(User::getPhone, phone);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> result = this.page(page, wrapper);
        // 清除密码
        result.getRecords().forEach(u -> u.setPassword(null));

        return Result.success(result);
    }

    /**
     * 获取用户详情
     */
    public Result<?> getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }
}