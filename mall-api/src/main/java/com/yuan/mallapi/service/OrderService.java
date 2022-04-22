package com.yuan.mallapi.service;

public interface OrderService {
    /**
     * 创建订单
     */
    int create(String userId, String commodityCode, int orderCount);

}
