package com.warehouse.ecommerceuserprofile.app.service;
/**
 * <p>
 * Project: EcommerceUserProfile
 * Version: 1.0
 * <p>
 * Created by wushengran on 2020/6/12 15:18
 */

import com.alibaba.fastjson.JSON;

import com.warehouse.ecommerceuserprofile.app.support.EsTagQuery;
import com.warehouse.ecommerceuserprofile.app.support.MemberTag;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class EsQueryService {
    @Resource(name="highLevelClient")
    RestHighLevelClient highLevelClient;

    public List<MemberTag> getUserInfo(List<EsTagQuery> tagQuerys){
        // 构建一个SearchRequest，用于向es发出查询请求
        SearchRequest request = new SearchRequest();
        // 一个es查询的request，最重要的就是指定index、type和source
        request.indices("usertag");
        request.types("_doc");

        // 用一个SearchSourceBuilder来构建source
        SearchSourceBuilder builder = new SearchSourceBuilder();
        request.source(builder);

        // 接下来是builder的配置
        builder.from(0);     // 查询结果，从0开始，到1000结束
        builder.size(1000);

        // 要对builder配置一个布尔查询的构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        builder.query(boolQueryBuilder);

        // 对布尔查询builder进行配置
        List<QueryBuilder> should = boolQueryBuilder.should();
        List<QueryBuilder> must = boolQueryBuilder.must();
        List<QueryBuilder> mustNot = boolQueryBuilder.mustNot();

        // 遍历查询条件，按照type，分配到对应的组中去
        for( EsTagQuery tagQuery: tagQuerys ){
            String name = tagQuery.getName();
            String value = tagQuery.getValue();
            String type = tagQuery.getType();

            if(type.equals("match")){
                should.add(QueryBuilders.matchQuery(name, value));
            }
            if (type.equals("notMatch")) {
                mustNot.add(QueryBuilders.matchQuery(name, value));
            }
            if (type.equals("rangeBoth")) {
                String[] split = value.split("-");
                String v1 = split[0];
                String v2 = split[1];
                should.add(QueryBuilders.rangeQuery(name).lte(v2).gte(v1));
            }
            if (type.equals("rangeGte")) {
                should.add(QueryBuilders.rangeQuery(name).gte(value));
            }
            if (type.equals("rangeLte")) {
                should.add(QueryBuilders.rangeQuery(name).lte(value));
            }
            if (type.equals("exists")) {
                should.add(QueryBuilders.existsQuery(name));
            }
        }

        // 使用client发送请求
        RequestOptions options = RequestOptions.DEFAULT;
        // 定义一个List，用来保存查询结果
        List<MemberTag> memberTags = new ArrayList<>();
        try{
            SearchResponse searchResponse = highLevelClient.search(request, options);
            SearchHits hits = searchResponse.getHits();
            // 用迭代器进行遍历
            Iterator<SearchHit> iterator = hits.iterator();
            while(iterator.hasNext()){
                SearchHit hit = iterator.next();
                // 从每条命中中提取source，转换数据类型
                String source = hit.getSourceAsString();
                MemberTag memberTag = JSON.parseObject(source, MemberTag.class);
                memberTags.add(memberTag);
            }
            return memberTags;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
