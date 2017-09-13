package com.github.holgerbrandl.spark.components;

import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static com.github.holgerbrandl.spark.components.LabelTester.makeTestImage;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class LocalSparkComponentsBM {


    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        //        @Param({"127", "128", "129", "130", "131"})
//        @Param({"127", "129", "131"})
        @Param({"131"})
        int threshold;

        @Param({"1", "3", "6"})
        Integer numThreads;


        Img<BitType> testImage;

        // see https://stackoverflow.com/questions/26168254/how-to-set-amount-of-spark-executors


        @Setup(Level.Trial)
        public void setUp() {
            testImage = makeTestImage(new int[]{2000, 2000, 1}, threshold);
        }
    }


    @Benchmark
    @Fork(1)
    public void blankMethod(ExecutionPlan plan) {
        new LabelComponents(plan.testImage, Utils.localSpark(plan.numThreads)).labelImage();
    }
}