import java.util.HashMap;
import java.util.Map;

public class LocalMaximumFinder {

//    public static void main(String[] args) {
//        double[] dataset = {5, 2, 8, 4, 9, 1, 6, 3}; // Sample dataset
//        Map<Integer, Double> localMaximumIndices = findLocalMaximums(dataset);
//        System.out.println("Local minimums found at indices: " + localMaximumIndices);
//    }

    public static Map<Integer, Double> findLocalMaximums(double[] dataset) {
        Map<Integer, Double> localMaximumIndices = new HashMap<>();
        // O(n)
        for (int i = 1; i < dataset.length - 1; i++) {
            if (dataset[i] > dataset[i - 1] && dataset[i] > dataset[i + 1]) {
                localMaximumIndices.put(i, dataset[i]);
            }
        }
        return localMaximumIndices;
    }
}