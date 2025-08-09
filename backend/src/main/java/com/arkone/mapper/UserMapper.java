package com.arkone.mapper;

import com.arkone.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户Mapper接口
 *
 * @author ArkOne
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND deleted = 0")
    User selectByEmail(@Param("email") String email);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username} AND deleted = 0 AND id != #{excludeId}")
    int checkUsernameExists(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email} AND deleted = 0 AND id != #{excludeId}")
    int checkEmailExists(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE users SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 获取用户统计信息
     */
    @Select("SELECT COUNT(*) as total, " +
            "SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) as active, " +
            "SUM(CASE WHEN role = 'admin' THEN 1 ELSE 0 END) as admin " +
            "FROM users WHERE deleted = 0")
    Object getUserStats();
}