package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.PasswordDTO;
import com.library.dto.UserDTO;
import com.library.entity.User;
import com.library.vo.UserVO;

public interface UserService extends IService<User> {
    Result<?> login(LoginDTO dto);
    Result<?> register(UserDTO dto);
    Result<?> addUser(UserDTO dto);
    Result<?> updateUser(UserDTO dto);
    Result<?> deleteUser(Long id);
    Result<?> updatePassword(Long userId, PasswordDTO dto);
    Result<?> resetPassword(Long userId);
    Result<UserVO> getUserInfo(Long userId);
    Result<Page<UserVO>> getUserPage(Integer pageNum, Integer pageSize, String username, String nickname, String phone);
}