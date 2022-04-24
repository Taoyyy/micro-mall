package com.yuan.order.service;

import com.yuan.order.entity.Order;

/**
 * @author 6
 * @description 针对表【oms_order(订单)】的数据库操作Service
 * @createDate 2022-04-05 00:35:43
 */
public interface OrderService {

    void createOrder(Order order) throws InterruptedException;

    boolean rushOrder(Order order);

    void saveOrder(Order order);
}
