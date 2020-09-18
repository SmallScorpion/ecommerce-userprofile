package com.warehouse.ecommerceuserprofile.etls.platlform;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;

/**
 * 热词提取(词云)
 */
public class HotWordEtl {
    public static void main(String[] args) {
        // 创建一个java spark context，方便后面调用transform操作
        SparkConf sparkConf = new SparkConf()
                .setAppName("hot word etl")
                .setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        // 1. 首先从hdfs上读取数据
        System.setProperty("HADOOP_USER_NAME", "atguigu");
        JavaRDD<String> linesRdd = jsc.textFile("hdfs://192.168.43.102:9000/data/SogouQ.sample.txt");

        // 2. mapToPair得到二元组，准备word count
        JavaPairRDD<String, Integer> pairRDD = linesRdd.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                String word = s.split("\t")[2];
                return new Tuple2<>(word, 1);
            }
        });

        // 3. 以word作为key进行reduce聚合操作
        JavaPairRDD<String, Integer> countRdd = pairRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });

        // 4. 元素互换位置
        JavaPairRDD<Integer, String> swapedRdd = countRdd.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
            @Override
            public Tuple2<Integer, String> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                return stringIntegerTuple2.swap();
            }
        });

        // 5. 按照count值降序排序
        JavaPairRDD<Integer, String> sortedRdd = swapedRdd.sortByKey(false);

        // 6. 再互换位置回到之前的状态，提取TopN，得到一个list
        List<Tuple2<String, Integer>> resultList = sortedRdd.mapToPair(new PairFunction<Tuple2<Integer, String>, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Tuple2<Integer, String> integerStringTuple2) throws Exception {
                return integerStringTuple2.swap();
            }
        })
                .take(10);

        // 打印输出
        for (Tuple2<String, Integer> hotWordCount : resultList) {
            // [汶川地震原因] === count 335
            System.out.println(hotWordCount._1 + " === count " + hotWordCount._2 + "\n");
        }
    }
}