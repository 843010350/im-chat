package com.wwm.nettycommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author
 * @since 2023-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不可为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不可为空")
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像图标
     */
    private String avatar;


    /**
     * 在线状态
     */
    private String status;

    /**
     * 在线签名
     */
    private String sign;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更改时间
     */
    private LocalDateTime updateTime;



    @TableField(exist = false)
    private Integer UserFailCount = 10;
    /**
     * 是否主动下线
     */
    @TableField(exist = false)
    private boolean isOffLine = false;

    @TableField(exist = false)
    private String token;

    /**
     * 是否重连
     */
    @TableField(exist = false)
    private boolean isReconnect = false;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
