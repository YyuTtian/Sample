package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2025-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_hobby")
public class TbHobby implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 爱好id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 爱好
     */
    private String hobbyName;


}
