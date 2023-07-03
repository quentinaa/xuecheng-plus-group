package com.xuecheng.base.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Mr.M
 * @version 1.0
 * @description 分页查询分页参数
 * @date 2023/2/11 15:33
 */
@Data
@AllArgsConstructor
public class PageParams {
    //默认起始页数
    public static final long DEFAULT_PAGE_CURRENT=1l;
    //默认每页记录数
    public static final long DEFAULT_PAGE_SIZE=10l;
    //当前页码
    @ApiModelProperty("页码")
    private Long pageNo = DEFAULT_PAGE_CURRENT;
    //每页显示记录数
    @ApiModelProperty("每页记录数")
    private Long pageSize = DEFAULT_PAGE_SIZE;


}
