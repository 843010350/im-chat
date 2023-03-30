package com.wwm.nettycommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户信箱消息索引表
 * </p>
 *
 * @author 
 * @since 2023-03-13
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("im_user_msg_box")
public class UserMsgBox extends Model<UserMsgBox> {

    private static final long serialVersionUID=1L;

      /**
     * 信箱拥有用户id
     */
      private Integer ownerUid;

      /**
     * 消息参与的另一方
     */
      private Integer otherUid;

      /**
     * 消息id
     */
      private Integer mid;

      /**
     * 信箱类型：1-发件箱，2-收件箱
     */
      private Integer boxType;

    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
          return this.ownerUid;
      }

}
