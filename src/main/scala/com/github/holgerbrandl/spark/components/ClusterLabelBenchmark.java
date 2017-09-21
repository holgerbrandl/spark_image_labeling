package com.github.holgerbrandl.spark.components;

import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.apache.log4j.Logger;
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
    public void labelCompoments(ExecutionPlan plan) {
        new LabelComponents(plan.testImage, Utils.localSpark(plan.numThreads)).labelImage();
    }
}