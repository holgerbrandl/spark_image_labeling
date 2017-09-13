package com.github.holgerbrandl.spark.misc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 10, time = 200, timeUnit = TimeUnit.NANOSECONDS)
@Measurement(iterations = 20, time = 200, timeUnit = TimeUnit.NANOSECONDS)
public class RandomBenchmark {

    public long lastValue;


    @Benchmark
    @Fork(1)
    public void blankMethod() {
    }


    @Benchmark
    @Fork(1)
    public void simpleMethod(Blackhole blackhole) {
        int i = 0;
        blackhole.consume(i++);
    }


    @Benchmark
    @Fork(1)
    public void granularityMethod(Blackhole blackhole) {
        long initialTime = System.nanoTime();
        long measuredTime;
        do {
            measuredTime = System.nanoTime();
        } while (measuredTime == initialTime);
        blackhole.consume(measuredTime);
    }
}