package com.yuan.order.mapper;

import com.yuan.order.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author 6
 * @description 针对表【oms_order(订单)】的数据库操作Mapper
 * @createDate 2022-04-05 00:35:43
 * @Entity com.yuan.order.entity.Order
 */
public interface OrderMapper extends BaseMapper<Order> {
    int insertSelective(Order order);
}




