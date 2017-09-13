package com.github.holgerbrandl.spark.misc;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.openjdk.jmh.annotations.*;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * http://codingjunkie.net/micro-benchmarking-with-caliper/
 * <p>
 * Cool example https://github.com/eugenp/tutorials/blob/master/jmh/src/main/java/com/baeldung/BenchMark.java
 *
 * @author Holger Brandl
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 2, time = 200, timeUnit = TimeUnit.NANOSECONDS)
@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.NANOSECONDS)
public class ExampleBenchmark2 {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        //        @Param({ "100", "200", "300", "500", "1000" })
        @Param({"10", "20"})
        int iterations;

//        @Param({"foo", "bar"})
//        String something;

        public Hasher murmur3;

        public String password = "4v3rys3kur3p455w0rd";


        @Setup(Level.Invocation)
        public void setUp() {
            murmur3 = Hashing.murmur3_128().newHasher();
        }
    }


    @Benchmark
    @Fork(1)
    public void benchMurmur3_128(ExecutionPlan plan) {

        for (int i = plan.iterations; i > 0; i--) {
            plan.murmur3.putString(plan.password, Charset.defaultCharset());
        }

        plan.murmur3.hash();
    }


    @Benchmark
    @Fork(1)
    public void init() throws InterruptedException {
        // Do nothing
        Thread.sleep((long) (new Random().nextFloat() * 1000));
    }
}
