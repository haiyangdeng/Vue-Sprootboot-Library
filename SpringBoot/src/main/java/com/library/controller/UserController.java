// ============================================
// 文件: com/library/controller/UserController.java
// ============================================
package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.PasswordDTO;
import com.library.dto.UserDTO;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody @Validated LoginDTO dto) {
        return userService.login(dto);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody @Validated UserDTO dto) {
        return userService.register(dto);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getUserInfo(userId);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/updateInfo")
    public Result<?> updateInfo(@RequestBody @Validated UserDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        dto.setId(userId);
        return userService.updateUser(dto);
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePwd")
    public Result<?> updatePassword(@RequestBody @Validated PasswordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.updatePassword(userId, dto);
    }

    // ====== 管理员接口 ======

    /**
     * 分页查询用户列表（管理员）
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone) {
        return userService.getUserPage(pageNum, pageSize, username, phone);
    }

    /**
     * 新增用户（管理员）
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> addUser(@RequestBody @Validated UserDTO dto) {
        return userService.addUser(dto);
    }

    /**
     * 修改用户（管理员）
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateUser(@RequestBody @Validated UserDTO dto) {
        return userService.updateUser(dto);
    }

    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 重置密码（管理员）
     */
    @PutMapping("/resetPwd/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> resetPassword(@PathVariable Long id) {
        return userService.resetPassword(id);
    }
}
