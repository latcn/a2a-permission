package io.github.latcn.a2a.permission.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {

    @Update("UPDATE t_user SET perm_version = perm_version + 1, updated_at = CURRENT_TIMESTAMP(6) " +
            "WHERE id = #{userId} AND perm_version = #{oldVersion}")
    int incrementVersionIfMatch(@Param("userId") Long userId, @Param("oldVersion") Long oldVersion);
}