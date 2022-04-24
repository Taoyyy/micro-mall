package com.yuan.order.controller;

import com.yuan.common.api.CommonResult;
import com.yuan.order.entity.Order;
import com.yuan.order.service.BusinessService;
import com.yuan.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    BusinessService businessService;
    @Autowired
    OrderService orderService;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    /**
     * 创建订单，发到MQ,过期未支付自动关单
     */
    @GetMapping("/create")
    public CommonResult<String> createOrder() throws InterruptedException {
        //订单下单成功
        Order order = new Order();
        order.setOrderSn(UUID.randomUUID().toString());
        order.setModifyTime(new Date());
        orderService.createOrder(order);
        return CommonResult.success(order.getOrderSn(), "订单下单成功，请尽快支付");
    }


    /**
     * 秒杀活动
     */
    @GetMapping("/rushOrder")
    public CommonResult<String> rushOrder() {
        //生成订单
        Order order = new Order();
        order.setOrderSn(UUID.randomUUID().toString());
        order.setModifyTime(new Date());
        //抢商品
        boolean success = orderService.rushOrder(order);
        //抢成功，直接下单，失败，返回秒杀失败
        if (success) {
            //下单成功，直接返回
            return CommonResult.success(order.getOrderSn(), "订单下单成功，请尽快支付");
        } else {
            return CommonResult.failed("秒杀商品失败");
        }
    }

    /**
     * 使用SEATA实现分布式事务，购买，生成订单，减库存，扣账户余额
     */
    @GetMapping("/purchase")
    public CommonResult<String> purchase() {
        businessService.purchase("1", "C201901140001", 20);
        return CommonResult.success("下单成功");
    }
}
