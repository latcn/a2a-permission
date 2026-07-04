package io.github.latcn.a2a.permission.admin.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.latcn.a2a.permission.admin.infrastructure.persistence.entity.UserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<UserDO> {

    @Update("UPDATE t_user SET perm_version = perm_version + 1, updated_at = CURRENT_TIMESTAMP(6) " +
            "WHERE id = #{userId} AND perm_version = #{oldVersion}")
    int incrementVersionIfMatch(@Param("userId") Long userId, @Param("oldVersion") Long oldVersion);
}