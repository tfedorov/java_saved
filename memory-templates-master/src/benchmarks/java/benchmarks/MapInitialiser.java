package benchmarks;

import java.util.Map;
import java.util.Random;

class MapInitialiser {

    static final int MAX_VAL = 100000;
    static final int GUARANT_KEY = 43;

    static Map<Integer, String> initMap(Map<Integer, String> source) {
        Random randomGenerator = new Random();

        source.put(GUARANT_KEY, "Some");
        for (int i = 0; i < MAX_VAL; i++) {
            Integer random = randomGenerator.nextInt(MAX_VAL);
            source.put(random, random.toString());
        }
        return source;
    }

    static Map<Integer, String> toMap(Map<Integer, String> source, Map<Integer, String> target) {
        for (Map.Entry<Integer, String> sourceMapEntry : source.entrySet()) {
            target.put(sourceMapEntry.getKey(), sourceMapEntry.getValue());
        }
        return target;
    }


}
