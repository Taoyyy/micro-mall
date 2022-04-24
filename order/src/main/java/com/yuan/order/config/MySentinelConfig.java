package com.yuan.order.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MySentinelConfig {

    //自定义熔断规则
    @PostConstruct
    private static void initDegradeRules() {
        System.out.println("执行了静态方法");
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("createOrder");
        //设置熔断策略为慢调用比例模式
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        // 设置慢调用临界阈值,单位是ms,响应时间超过1s就熔断
        rule.setCount(1000);
        //定义熔断时长10s
        rule.setTimeWindow(10);
        //设置慢调用比例阈值
        rule.setSlowRatioThreshold(0.1);
        //熔断触发的最小请求数，请求数小于该值时即使异常比率超出阈值也不会熔断
        rule.setMinRequestAmount(5);
        //设置统计时长，1s
        rule.setStatIntervalMs(1000);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }
}
