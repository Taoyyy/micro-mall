package com.yuan.order.service.impl;


import com.yuan.mallapi.domain.order.OrderTbl;
import com.yuan.mallapi.service.AccountService;
import com.yuan.mallapi.service.OrderService;
import com.yuan.order.mapper.OrderTblMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 6
 * @description 针对表【oms_order(订单)】的数据库操作Service实现
 * @createDate 2022-04-05 00:35:43
 */

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    OrderTblMapper orderMapper2;
    @DubboReference
    AccountService accountService;

    /**
     * 使用分布式事务创建订单
     *
     * @param userId
     * @param commodityCode
     * @param orderCount
     * @return
     */
    @Override
    public int create(String userId, String commodityCode, int orderCount) {
        int orderMoney = calculate(commodityCode, orderCount);
        accountService.debit(userId, orderMoney);
        OrderTbl order = new OrderTbl();
        order.setUserId(userId);
        order.setCommodityCode(commodityCode);
        order.setCount(orderCount);
        order.setMoney(orderMoney);
        orderMapper2.insertSelective(order);
//        解开下面注释，测试Seata分布式事务回滚
//        boolean flag=false;
//        if (!flag) {
//            throw new RuntimeException("测试抛异常后，分布式事务回滚！");
//        }
        // INSERT INTO orders ...
        return 1;
    }

    private int calculate(String commodityCode, int orderCount) {
        //TODO 拿到该商品的价格，乘以该商品的数量，假设该商品的价格为10
        return 10 * orderCount;
    }


}




