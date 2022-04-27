package com.yuan.search.service.impl;


import com.yuan.search.domain.Content;
import com.yuan.search.service.EsService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EsServiceImpl implements EsService {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 搜索产品，价格区间，聚合分析有哪些商铺在卖这些产品
     */
    @Override
    public void search(String keyword, Integer pageNum, Integer pageSize) {
        //构建原生查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 构建布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //构建普通查询,根据产品名称全文搜索
        MatchQueryBuilder titleQuery = QueryBuilders.matchQuery("title", keyword);
        //构建普通查询,过滤出在指定价格区间内的产品
        RangeQueryBuilder price = QueryBuilders.rangeQuery("price");
        price.gte(3000);
        price.lte(5000);
        //把普通查询放入布尔查询
        boolQueryBuilder.must(titleQuery);
        //布尔查询，把原生查询放入布尔查询种
        boolQueryBuilder.filter(price);
        //聚合分析出卖该产品的所有商铺
        TermsAggregationBuilder shop = AggregationBuilders.terms("店铺").field("shop").size(10);
        //将布尔查询放入原生查询
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        //将聚合查询放入原生查询
        nativeSearchQueryBuilder.addAggregation(shop);
        //设置分页参数
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum, pageSize));
        //执行原生查询,查看搜索结果
        SearchHits<Content> searchs = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), Content.class);
        for (SearchHit<Content> search : searchs
        ) {
            System.out.println(search.getContent());
        }
        //查看聚合分析结果，有哪些商铺在卖这种产品
        Aggregations aggregations = searchs.getAggregations();
        assert aggregations != null;
        Aggregation aggregation = aggregations.get("店铺");
        List<? extends Terms.Bucket> buckets = ((ParsedStringTerms) aggregation).getBuckets();
        for (Terms.Bucket bucket : buckets) {
            // 获取商铺的集合
            String key = (String) bucket.getKey();
            System.out.println(key);
        }
    }
}
