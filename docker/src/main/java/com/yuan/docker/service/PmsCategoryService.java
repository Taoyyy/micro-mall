package com.yuan.docker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.docker.entity.PmsCategory;
import com.yuan.docker.entity.PmsCategoryNode;

import java.util.List;

/**
* @author 6
* @description 针对表【pms_category(商品三级分类)】的数据库操作Service
* @createDate 2022-03-26 17:13:10
 *
*/
public interface PmsCategoryService extends IService<PmsCategory> {

//    List<PmsCategoryNode> showProTreeByRedis();
    List<PmsCategoryNode> showProTree();
}
