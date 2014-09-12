package fr.ippon.dojo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;

public class TestSparkStreaming {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("NetworkWordCount");
        JavaStreamingContext jsc = new JavaStreamingContext(conf, new Duration(1000));

        JavaReceiverInputDStream<String> lines = jsc.socketTextStream("localhost", 9999);

        lines.flatMap(line -> Arrays.asList(line.split(" ")))
            .mapToPair(x -> new Tuple2<String, Integer>(x, 1))
            .reduceByKey((i, j) -> i + j)
            .print();

        jsc.start();
        jsc.awaitTermination();
    }
}
