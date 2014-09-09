package fr.ippon.dojo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        final String PATH = "C:\\Users\\IPPON_2\\coding-dojo-spark";

        /*************************** Initialisation de Spark ***************************/
        SparkConf conf = new SparkConf()
                .setAppName("test")
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        String filename = PATH + "\\data\\paris-arbresalignementparis2010\\arbres.txt";
        //String filename = PATH + "\\data\\paris-arbresalignementparis2010\\arbresalignementparis2010.csv";
        sc.textFile(filename);

        /*************************** Distribution chiffres dans des RDD ***************************/
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> distData = sc.parallelize(data);
        System.out.println(data);
        System.out.println(distData.collect());

        /*************************** Lines Count ***************************/
        JavaRDD<String> lines = sc.textFile(filename); // lines est un pointeur sur le fichier
        System.out.println("\n" + filename);
        System.out.println("\nnb lines = " + lines.count());

        /*************************** Words Count ***************************/
        JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")));
        JavaPairRDD<String, Integer> counts = words.mapToPair(w -> new Tuple2<String, Integer>(w, 1))
                .reduceByKey((x, y) -> x + y);
        //System.out.println(counts.collect());
        int word_count = counts.values().reduce((a, b) -> a + b);
        System.out.println("nb words = " + word_count);

        /*************************** nb lettres method 1 ***************************/
        JavaRDD<Integer> lineLengths = lines.map(s -> s.length());
        int length = lineLengths.reduce((a, b) -> a + b);
        System.out.println("nb lettres 1 = " + length);

        /*************************** nb lettres method 2 ***************************/
        JavaRDD<Integer> l2 = lines.map(new Function<String, Integer>() {
            public Integer call(String s) { return s.length(); }
        });
        int totalLength = l2.reduce(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer a, Integer b) { return a + b; }
        });
        System.out.println("nb lettres 2 = " + totalLength);

        /*************************** Parcours elts RDD ***************************/
        System.out.println();
        sc.textFile(filename)
                .foreach(x -> System.out.println(x));

        /*************************** Parcours nom arbres ***************************/
        System.out.println();
        sc.textFile(filename)
                .filter(s -> s.startsWith("geom_x_y") == false)
                .map(line -> line.split(";"))
                .foreach(x -> System.out.println(x[4]));

        /*************************** Parcours arbres avec condition ***************************/
        System.out.println();
        sc.textFile(filename)
                .filter(s -> s.startsWith("geom_x_y") == false)
                .map(line -> line.split(";"))
                .filter(x -> x[5].equals("") == false)
                .foreach(x -> System.out.println(x[4]));

        /*************************** Hauteur Count ***************************/
        System.out.println();
        sc.textFile(filename)
                .filter(s -> s.startsWith("geom_x_y") == false)
                .map(line -> line.split(";"))
                .filter(x -> x[3].equals("") == false)
                .mapToPair(s -> new Tuple2<String, Long>(s[3], 1l))
                .reduceByKey((x, y) -> x + y)
                .foreach(x -> System.out.println(x._1 + " = " + x._2));

        /*************************** Type Arbre sort Count ***************************/
        System.out.println();
        sc.textFile(filename)
                .filter(s -> s.startsWith("geom_x_y") == false)
                .map(line -> line.split(";"))
                .filter(x -> x[3].equals("") == false)
                .mapToPair(s -> new Tuple2<String, Long>(s[4], 1l))
                .reduceByKey((x, y) -> x + y)
                .sortByKey()
                .foreach(x -> System.out.println(x._1 + " = " + x._2));

        /*************************** Arbre tri Count ***************************/
        System.out.println();
        sc.textFile(filename)
                .filter(s -> s.startsWith("geom_x_y") == false)
                .map(line -> line.split(";"))
                .filter(x -> x[3].equals("") == false)
                .mapToPair(s -> new Tuple2<String, Long>(s[4], 1l))
                .reduceByKey((x, y) -> x + y)
                .mapToPair(s -> new Tuple2<Long, String>(s._2, s._1))
                .sortByKey()
                .foreach(x -> System.out.println(x._2 + " = " + x._1));
    }
}
