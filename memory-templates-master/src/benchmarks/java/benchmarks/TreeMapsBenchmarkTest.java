package benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class TreeMapsBenchmarkTest {

    private static final Map<Integer, String> treeMap = MapInitialiser.initMap(new TreeMap<>());

    @Benchmark
    public String get() {
        return treeMap.get(MapInitialiser.GUARANT_KEY);
    }

    @Benchmark
    public String put() {
        return treeMap.put(MapInitialiser.GUARANT_KEY, "putCount1.toString()");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TreeMapsBenchmarkTest.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
