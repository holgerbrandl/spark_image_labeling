package com.github.holgerbrandl.spark.components;

import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static com.github.holgerbrandl.spark.components.ImageUtils.makeTestImage;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class ClusterLabelBenchmark {

    static {
        Logger.getLogger("org").setLevel(org.apache.log4j.Level.OFF);
    }


    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        //        @Param({"127", "128", "129", "130", "131"})
//        @Param({"127", "129", "131"})
        @Param({"131"})
        int threshold;

        @Param({"1", "3", "6"})
        Integer numThreads;

        @Param({"500", "1000", "2000"})
        Integer imageSize;


        Img<BitType> testImage;

        // see https://stackoverflow.com/questions/26168254/how-to-set-amount-of-spark-executors


        @Setup(Level.Trial)
        public void setUp() {
            testImage = makeTestImage(new int[]{imageSize, imageSize, 1}, threshold);
        }
    }


    @Benchmark
    @Fork(1)
    public void labelComponents(ExecutionPlan plan) {
        String clusterURL = System.getenv("SPARK_CLUSTER");
        SparkSession spark = SparkSession.builder()
                .appName("Spark SQL basic example")
                .master(clusterURL)
                .config("spark.some.config.option", "some-value")
                .getOrCreate();

        new LabelComponents(plan.testImage, spark).labelImage();
    }
}