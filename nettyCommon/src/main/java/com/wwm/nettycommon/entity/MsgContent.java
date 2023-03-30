package com.wwm.nettycommon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 消息内容表
 * </p>
 *
 * @author 
 * @since 2023-03-13
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("im_msg_content")
public class MsgContent extends Model<MsgContent> {

    private static final long serialVersionUID=1L;

      /**
     * 消息id
     */
      @TableId(value = "mid")
      private Integer mid;

      /**
     * 消息内容
     */
      private String content;

      /**
     * 发送消息用户id
     */
      private Integer senderId;

      /**
     * 接收消息用户id
     */
      private Integer recipientId;

      /**
     * 消息类型：1-文本消息，2-红包消息，3-语音消息，4-视频消息
     */
      private Integer msgType;

      /**
     * 消息是否已被接收：0-未接收，1-已接收
     */
      private Integer isReceived;

    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
          return this.mid;
      }

}
