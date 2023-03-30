package com.wwm.nettycommon.service.impl;

import com.wwm.nettycommon.entity.Group;
import com.wwm.nettycommon.mapper.GroupMapper;
import com.wwm.nettycommon.service.GroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群聊信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2023-03-27
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

}
