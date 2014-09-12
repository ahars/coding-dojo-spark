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

        final String PATH = "hdfs://10.10.200.119:9000/";

        SparkConf conf = new SparkConf()
                //.setMaster("local[2]")
                .setMaster("spark://10.10.200.199:7077")
                .setJars(new String[]{PATH + "/dojo-spark-0.0.1-SNAPSHOT.jar"})
                .setAppName("NetworkWordCount");
        JavaStreamingContext jsc = new JavaStreamingContext(conf, new Duration(5000));

        JavaReceiverInputDStream<String> lines = jsc.socketTextStream("10.10.200.119", 9999);

        lines.flatMap(line -> Arrays.asList(line.split(" ")))
            .mapToPair(x -> new Tuple2<String, Integer>(x, 1))
            .reduceByKey((i, j) -> i + j)
            .dstream()
            .saveAsTextFiles(PATH + "/spark_streaming/", "/netcat_streaming");

        jsc.start();
        jsc.awaitTermination();
    }
}
