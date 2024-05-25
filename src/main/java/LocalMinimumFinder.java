import java.util.HashMap;
import java.util.Map;

public class LocalMinimumFinder {

//    public static void main(String[] args) {
//        double[] dataset = {5, 2, 8, 4, 9, 1, 6, 3}; // Sample dataset
//
//        // Find local minimums
//        Map<Integer, Double> localMinimumIndices = findLocalMinimums(dataset);
//
//        // Print local minimums
//        System.out.println("Local minimums found at indices: " + localMinimumIndices);
//    }

    public static Map<Integer, Double> findLocalMinimums(double[] dataset) {
        Map<Integer, Double> localMinimumIndices = new HashMap<>();
        // O(n)
        for (int i = 1; i < dataset.length - 1; i++) {
            if (dataset[i] < dataset[i - 1] && dataset[i] < dataset[i + 1]) {
                localMinimumIndices.put(i, dataset[i]);
            }
        }
        return localMinimumIndices;
    }
}