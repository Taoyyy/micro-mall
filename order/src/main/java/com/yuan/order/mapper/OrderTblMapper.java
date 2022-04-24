package com.yuan.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.mallapi.domain.order.OrderTbl;

/**
 * @author 6
 * @description 针对表【order_tbl】的数据库操作Mapper
 * @createDate 2022-04-19 18:19:36
 * @Entity com.yuan.order.entity.OrderTbl
 */
public interface OrderTblMapper extends BaseMapper<OrderTbl> {
    int insertSelective(OrderTbl orderTbl);

}




