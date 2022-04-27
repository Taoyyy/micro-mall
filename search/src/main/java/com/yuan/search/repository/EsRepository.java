package com.yuan.search.repository;


import com.yuan.search.domain.Content;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsRepository extends ElasticsearchRepository<Content,Long> {


}
