package com.wwm.nettycommon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群聊信息表
 * </p>
 *
 * @author 
 * @since 2023-03-27
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("im_group")
public class Group extends Model<Group> {

    private static final long serialVersionUID=1L;

      /**
     * 用户id
     */
        private Integer id;

      /**
     * 群聊名称
     */
      @TableField("group_name")
      private String groupname;



      /**
     * 群聊头像图标
     */
      private String avatar;

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
