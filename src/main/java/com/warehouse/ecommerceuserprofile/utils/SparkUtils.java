package com.warehouse.ecommerceuserprofile.utils;

import org.apache.spark.sql.SparkSession;

/**
 * 从Pool中获取Spark Session
 */
public class SparkUtils {

    // 定义一个spark session的会话池
    private static ThreadLocal<SparkSession> sessionPool = new ThreadLocal();

    // 初始化park session的方法
    public static SparkSession initSession(){
        // 先判断会话池中是否有session，如果有直接用，没有创建
        if( sessionPool.get() != null ) {
            return sessionPool.get();
        }

        SparkSession session = SparkSession.builder()
                .appName("userprofile-etl")
                .master("local[*]")
                .config("es.nodes","hadoop102")
                .config("es.prot","9200")
                .config("es.index.auto.creae","false")
                .enableHiveSupport()
                .getOrCreate();

        sessionPool.set(session);

        return session;
    }

}
