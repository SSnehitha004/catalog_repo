import org.json.JSONObject;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PolynomialSecret {

    public static void main(String[] args) {
        // Load the JSON file content
        String jsonFilePath = "input2.json";
        String jsonInput = loadJSONFile(jsonFilePath);
        if (jsonInput == null) {
            System.out.println("Failed to load the JSON file.");
            return;
        }

        // Parse JSON input
        JSONObject input = new JSONObject(jsonInput);
        JSONObject keys = input.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<int[]> points = new ArrayList<>();

        // Extract points (x, y) and decode y
        for (String key : input.keySet()) {
            if (!key.equals("keys")) {
                int x = Integer.parseInt(key);
                JSONObject root = input.getJSONObject(key);
                int base = root.getInt("base");
                String valueStr = root.getString("value");

                // Decode y value from the given base
                BigInteger yDecoded = new BigInteger(valueStr, base);
                int y = yDecoded.intValue();

                // Store the point (x, y)
                points.add(new int[]{x, y});
            }
        }

        // Solve for the constant term c using Lagrange Interpolation
        double c = lagrangeInterpolation(points, k);
        System.out.printf("The constant term c is: %.0f%n", c);
    }

    // Function to load the JSON file content
    private static String loadJSONFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return null;
        }
    }

    // Function to perform Lagrange Interpolation
    public static double lagrangeInterpolation(List<int[]> points, int k) {
        double result = 0.0;

        for (int i = 0; i < k; i++) {
            int[] pointI = points.get(i);
            int xi = pointI[0];
            int yi = pointI[1];

            // Calculate the Lagrange basis polynomial Li(x)
            double li = 1.0;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int[] pointJ = points.get(j);
                    int xj = pointJ[0];
                    li *= (0 - xj) / (double) (xi - xj);  // Li(0) because we're finding the constant term
                }
            }

            // Add the contribution of yi * Li(0)
            result += yi * li;
        }

        return result;
    }
}
