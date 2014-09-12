package fr.ippon.dojo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;

public class AnalyseTonnageDechets {

    public static void main(String[] args) {

        final String PATH = "C:\\Users\\IPPON_2\\coding-dojo-spark\\data\\paris-tonnagesdechets\\";
        //final String PATH = "hdfs://10.10.200.119:9000/";

        SparkConf conf = new SparkConf()
                .setAppName("tonnages_dechets_bacs_jaunes2011")
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        String filename = PATH + "\\tonnages_des_dechets_bacs_jaunes.csv";
        System.out.println();

        String[] header = sc.textFile(filename)
                .map(line -> line.split(";"))
                .first();

        System.out.println(header[0] + " | avr_11");
        sc.textFile(filename)
                .filter(s -> s.startsWith("granularite") == false)
                .map(line -> line.split(";"))
                .filter(t -> !t[0].equals("Tout Paris"))
                .foreach(x -> System.out.println(x[0] + " | " + x[chooseMonth("avr_11")]));

        final String OUTPUT = PATH + "\\data\\paris-tonnagesdechets\\tonnage_avr11_";

        sc.textFile(filename)
                .map(line -> line.split(";"))
                .filter(t -> !t[0].equals("Tout Paris"))
                .map(t -> t[0] + " | " + t[chooseMonth("avr_11")])
                .saveAsTextFile(OUTPUT + "1");
        System.out.println("file 1 edited");

        sc.textFile(filename)
                .map(line -> line.split(";"))
                .filter(t -> !t[0].equals("Tout Paris"))
                .map(t -> t[0] + " | " + t[chooseMonth("avr_11")])
                .saveAsObjectFile(OUTPUT + "2");
        System.out.println("file 2 edited");
    }

    private static Integer chooseMonth(String month) {

        switch(month) {
            case "janv_11":
                return 1;
            case "fevr_11":
                return 2;
            case "mars_11":
                return 3;
            case "avr_11":
                return 4;
            case "mai_11":
                return 5;
            case "juin_11":
                return 6;
            case "juil_11":
                return 7;
            case "aout_11":
                return 8;
            case "sept_11":
                return 9;
            case "oct_11":
                return 10;
            case "nov_11":
                return 11;
            case "dec_11":
                return 12;
        }
        throw new RuntimeException("Unknown: " + month);
    }
}
