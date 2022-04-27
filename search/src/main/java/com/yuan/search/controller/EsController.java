package com.yuan.search.controller;

import com.yuan.search.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/es")
public class EsController {
    @Autowired
    private EsService esService;


    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public String search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        esService.search(keyword, pageNum, pageSize);
        return "请查看控制台";
    }

}
