package com.yuan.order.service.impl;

import com.yuan.mallapi.service.OrderService;
import com.yuan.mallapi.service.StorageService;
import com.yuan.order.service.BusinessService;

import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessServiceImpl implements BusinessService {
    @DubboReference
    private StorageService storageService;
    @Autowired
    private OrderService orderService;

    /**
     * 采购
     */
    @GlobalTransactional
    public void purchase(String userId, String commodityCode, int orderCount) {
        //库存服务减库存
        storageService.deduct(commodityCode, orderCount);
        //生成新订单
        orderService.create(userId, commodityCode, orderCount);
    }
}
