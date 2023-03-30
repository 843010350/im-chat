package com.wwm.nettycommon.mapper;

import com.wwm.nettycommon.dto.SearchUserDto;
import com.wwm.nettycommon.entity.Group;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 群聊信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2023-03-27
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {


}
