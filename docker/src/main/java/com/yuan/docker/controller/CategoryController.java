package com.yuan.docker.controller;

//import com.yuan.common.api.CommonResult;
import com.yuan.docker.entity.PmsCategoryNode;
import com.yuan.docker.service.PmsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class CategoryController {
    @Autowired
    PmsCategoryService pmsCategoryService;


    @GetMapping("/tree")
    public List<PmsCategoryNode> service() {
        return pmsCategoryService.showProTree();
    }
}
