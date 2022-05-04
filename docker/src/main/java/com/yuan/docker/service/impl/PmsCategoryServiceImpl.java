package com.yuan.docker.service.impl;


//import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.docker.entity.PmsCategory;
import com.yuan.docker.entity.PmsCategoryNode;
import com.yuan.docker.mapper.PmsCategoryMapper;
import com.yuan.docker.service.PmsCategoryService;
//import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 6
 * @description 针对表【pms_category(商品三级分类)】的数据库操作Service实现
 * @createDate 2022-03-26 17:13:10
 */
@Service
public class PmsCategoryServiceImpl extends ServiceImpl<PmsCategoryMapper, PmsCategory> implements PmsCategoryService {
    @Autowired
    private StringRedisTemplate redisTemplate;
//    @Autowired
//    RedissonClient redisson;


//    @Override
//    @Cacheable(value = "product",key = "#root.method.name")
//    public List<PmsCategoryNode> showProTreeByRedis() {
//
//        List<PmsCategoryNode> categoryNodeList = showProTree();
//        System.out.println("=====================");
//        System.out.println("查询了数据库");
//        String s = JSONObject.toJSONString(categoryNodeList);
//        return categoryNodeList;
//
//
////        =================================================================================
////        //redisson的分布式锁
////        RLock lock = redisson.getLock("tree");
//////        //上锁
////        lock.lock(10, TimeUnit.SECONDS);
////        try {
////            String categoryJson = redisTemplate.opsForValue().get("categoryNode");
////            if (!StringUtils.hasText(categoryJson)) {
////                List<PmsCategoryNode> categoryNodeList = showProTree();
////                System.out.println("=====================");
////                System.out.println("查询了数据库");
////                String s = JSONObject.toJSONString(categoryNodeList);
////                redisTemplate.opsForValue().set("categoryNode", s,12, TimeUnit.HOURS);
////                return categoryNodeList;
////            } else {
////                return JSONObject.parseArray(categoryJson, PmsCategoryNode.class);
////            }
////
////        } finally {
////            //解锁
////            lock.unlock();
////        }
//    }

    public List<PmsCategoryNode> showProTree() {

        List<PmsCategory> pmsCategoryList = baseMapper.selectList(null);
        List<PmsCategoryNode> result = pmsCategoryList
                .stream()
                .filter(pmsCategory -> pmsCategory.getParentCid().equals(0L))
                .map(pmsCategory -> setChildren(pmsCategory, pmsCategoryList))
                .collect(Collectors.toList());
        return result;
    }

    private PmsCategoryNode setChildren(PmsCategory pmsCategory, List<PmsCategory> pmsCategoryList) {
        PmsCategoryNode node = new PmsCategoryNode();
        BeanUtils.copyProperties(pmsCategory, node);
        List<PmsCategoryNode> children = pmsCategoryList.stream()
                .filter(pmsCategory1 -> pmsCategory1.getParentCid().equals(pmsCategory.getCatId()))
                .map(pmsCategory1 -> setChildren(pmsCategory1, pmsCategoryList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}




