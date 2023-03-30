package com.wwm.nettycommon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

import com.wwm.nettycommon.dto.UserViewDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 用户分组
 * </p>
 *
 * @author 
 * @since 2023-03-25
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("im_friend_group")
public class FriendGroup extends Model<FriendGroup> {

    private static final long serialVersionUID=1L;

      /**
     * 用户id
     */
        private Integer id;

      /**
     * 分组名称
     */
      @TableField("group_name")
      private String groupname;

      /**
     * 用户ID
     */
    private Integer userId;

      /**
     * 创建时间
     */
      private LocalDateTime createTime;

      /**
     * 更改时间
     */
      private LocalDateTime updateTime;

      @TableField(exist = false)
      private List<UserViewDto>  list;


    @Override
    protected Serializable pkVal() {
          return this.id;
      }

}
