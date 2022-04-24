package com.yuan.order.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.yuan.order.entity.Order;
import com.yuan.order.mapper.OrderMapper;
import com.yuan.order.service.OrderService;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Resource
    OrderMapper orderMapper;


    /**
     * 创建订单，发送到MQ延迟队列，超时自动关单
     *
     * @param order
     */
    @Override
    public void createOrder(Order order) throws InterruptedException {
//        Thread.sleep(2000);
        //发给mq的延迟队列，过期未支付则关单
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order);
        //发给mq,保存订单到Mysql
        rabbitTemplate.convertAndSend("order-event-exchange", "order.save.order", order);
//        LOGGER.info("发送订单到MQ，订单号为：" + order.getOrderSn());
    }

    @Override
    public boolean rushOrder(Order order) {
        //TODO 使用redis的setIfAbsent（userId）确认某人是否已经秒杀过，秒杀成功就占位，保证接口的幂等性
        //使用redisson的Semaphore来实现抢商品功能
        RSemaphore storage = redissonClient.getSemaphore("storage");
        try {
            boolean success = storage.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
            if (success) {
                //定义熔断策略入口
                Entry entry = SphU.entry("createOrder");
                //抢成功，直接下单，失败，返回秒杀失败
                try {
                    createOrder(order);
                } finally {
                    if (entry != null) {
                        entry.exit();
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            System.out.println();
            return false;
        } catch (BlockException e) {
            //如果消息队列处理不过来,发生熔断，不需要调用createOrder方法，直接并释放redisson的信号量，解锁库存，返回false
            storage.release(1);
            return false;
        }
    }

    @Override
    public void saveOrder(Order order) {
        orderMapper.insertSelective(order);
    }


}
