package com.github.holgerbrandl.spark.components;

import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;
import org.openjdk.jmh.annotations.*;

import java.io.File;
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

        //        @Param({"1", "5", "10", "15", "20"})
        @Param("5")
        Integer numCores;

        //        @Param({"500", "1000", "2000"})
        @Param({"1000"})
        Integer imageSize;


        Img<BitType> testImage;
        SparkSession spark;

        // see https://stackoverflow.com/questions/26168254/how-to-set-amount-of-spark-executors


        @Setup(Level.Trial)
        public void setUp() {
            // 2-d images

            String clusterURL = System.getenv("SPARK_CLUSTER_URL");

            if (clusterURL.isEmpty())
                throw new RuntimeException("Can not connect to spark: SPARK_CLUSTER_URL is not defined.");

            String applJar = "target/scala-2.11/component_labeling_2.11-0.1.jar";
            if (!new File(applJar).exists())
                throw new RuntimeException("application jar does not exist. run 'sbt package' first!");

            spark = SparkSession.builder()
                    .appName("Component_Labeling")
                    .master(clusterURL)
                    // https://stackoverflow.com/questions/41722993/spark-2-0-set-jars
                    .config("spark.jars", applJar)
                    // default 1g see https://spark.apache.org/docs/latest/configuration.html
//                    .config("spark.executor.memory", "2g")
                    // the maximum amount of CPU cores to request for the application from across the cluster,
                    // see https://spark.apache.org/docs/latest/configuration.html
                    .config("spark.cores.max", "" + numCores)
                    .getOrCreate();

            testImage = makeTestImage(new int[]{imageSize, imageSize}, threshold);

//            int numSlices = 200;
//            testImage = makeTestImage(new int[]{imageSize, imageSize, numSlices}, threshold);
        }
    }


    @Benchmark
    @Fork(1)
    public void labelComponents(ExecutionPlan plan) {
        new LabelComponents(plan.testImage, plan.spark).labelImage();
    }
}