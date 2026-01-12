import java.util.HashMap;
import java.util.Map;

public class StickProbability {

    public static Map<Integer, Double> getProbabilities() {
        Map<Integer, Double> probabilities = new HashMap<>();

        probabilities.put(1, 4.0 / 16);
        probabilities.put(2, 6.0 / 16);
        probabilities.put(3, 4.0 / 16);
        probabilities.put(4, 1.0 / 16);
        probabilities.put(5, 1.0 / 16);

        return probabilities;
    }
}
