package com.wwm.nettycommon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群聊用户表
 * </p>
 *
 * @author 
 * @since 2023-03-27
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("im_group_user")
public class GroupUser extends Model<GroupUser> {

    private static final long serialVersionUID=1L;

      /**
     * 用户id
     */
        private Integer id;

      /**
     * 群组id
     */
      private Integer groupId;

      /**
     * 用户id
     */
      private Integer userId;

      /**
     * 是否是群组管理园
     */
      private Integer isGroupManager;

      /**
     * 创建时间
     */
      private LocalDateTime createTime;

      /**
     * 更改时间
     */
      private LocalDateTime updateTime;


    @Override
    protected Serializable pkVal() {
          return this.id;
      }

}
