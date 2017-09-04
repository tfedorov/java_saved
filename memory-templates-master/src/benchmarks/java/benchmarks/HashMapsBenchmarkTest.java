package benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class HashMapsBenchmarkTest {

    private static final Map<Integer, String> hashMap = MapInitialiser.initMap(new HashMap<>());


    @Benchmark
    public String get() {
        return hashMap.get(MapInitialiser.GUARANT_KEY);
    }

    @Benchmark
    public String put() {
        return hashMap.put(MapInitialiser.GUARANT_KEY, "putCount1.toString()");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashMapsBenchmarkTest.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
