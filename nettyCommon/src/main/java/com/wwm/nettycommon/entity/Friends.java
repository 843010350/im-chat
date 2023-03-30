package com.wwm.nettycommon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户好友表
 * </p>
 *
 * @author 
 * @since 2023-03-25
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("im_friends")
public class Friends extends Model<Friends> {

    private static final long serialVersionUID=1L;

      /**
     * 用户id
     */
        private Integer id;

      /**
     * 用户id
     */
      private Integer userId;

      /**
     * 好友用户id
     */
      private Integer friendId;

      /**
     * 群组id
     */
      private Integer groupId;

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
