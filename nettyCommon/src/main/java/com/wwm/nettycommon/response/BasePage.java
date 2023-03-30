package com.wwm.nettycommon.response;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class BasePage {
    @TableField(exist = false)
    public Integer pageNum = 1;

    @TableField(exist = false)
    public Integer pageSize = 10;
}
