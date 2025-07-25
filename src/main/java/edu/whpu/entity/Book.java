package edu.whpu.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bin_wen
 * @date 2024/9/19 01:15
 */
@TableName("book")
@Data
public class Book {

    @TableId
    private Long bookId;

    private String bookName;

    private String bookAuthor;

    private BigDecimal bookPrice;

    private String bookPublisher;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;

}
