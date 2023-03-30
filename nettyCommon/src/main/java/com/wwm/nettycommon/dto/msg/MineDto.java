package com.wwm.nettycommon.dto.msg;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class MineDto {

    /**
     * 用户id
     */

    private Integer id;

    /**
     * 用户名
     */
    private String username;




    /**
     * 头像图标
     */
    private String avatar;

    /**
     * 是否是自己发送的消息
     */
    private Boolean mine;

    /**
     * 发送消息的内容
     */
    private String content;







}
