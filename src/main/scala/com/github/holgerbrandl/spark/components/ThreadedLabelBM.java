package com.github.holgerbrandl.spark.components;

import ij.io.FileSaver;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
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
public class ThreadedLabelBM {

    static {
        Logger.getLogger("org").setLevel(org.apache.log4j.Level.OFF);
    }


    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        //        @Param({"127", "128", "129", "130", "131"})
        //        @Param({"131"})
        @Param({"127", "129", "131"})
        int threshold;

        @Param({"1", "3", "5", "7"})
        Integer numThreads;


        Img<BitType> testImage;

        // see https://stackoverflow.com/questions/26168254/how-to-set-amount-of-spark-executors


        @Setup(Level.Trial)
        public void setUp() {
            int size = 1000;

            testImage = makeTestImage(new int[]{size, size}, threshold);

            String imageFile = "test_images/thres" + threshold + "_threads" + numThreads + "_" + System.currentTimeMillis() + ".png";
            new FileSaver(ImageJFunctions.wrapBit(testImage, "")).saveAsPng(imageFile);
        }
    }


    @Benchmark
    @Fork(1)
    public void labelComponents(ExecutionPlan plan) {
        new LabelComponents(plan.testImage, Utils.localSpark(plan.numThreads)).labelImage();
    }
}