package com.yuan.search.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Document(indexName = "jd_goods")
public class Content implements Serializable {
    @Id
    private String id;
    @Field(analyzer = "standard",type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Keyword,index = false)
    private String img;
    @Field(type = FieldType.Keyword )
    private float price;
    @Field(type = FieldType.Keyword )
    private String shop;


}
